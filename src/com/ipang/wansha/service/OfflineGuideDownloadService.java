package com.ipang.wansha.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.ipang.wansha.R;
import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.exception.HttpException;
import com.ipang.wansha.exception.ProductException;
import com.ipang.wansha.model.Download;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;

public class OfflineGuideDownloadService extends IntentService {

	private ProductDao productDao;
	private OfflineDao offlineDao;
	private Intent mIntent;
	private int downloadingId;
	private int hasDownloaded;
	private int allDownload;
	private int totalSize;
	private int downloadedSize;
	private final IBinder mBinder;
	private Queue<Integer> downloadQueue;
	private boolean isCancelled;
	private NotificationManager notifyManager;
	private Builder mBuilder;

	public OfflineGuideDownloadService() {
		super("OfflineGuideDownloadService");
		productDao = new ProductDaoImpl();
		offlineDao = new OfflineDaoImpl();
		mBinder = new DownloadBinder();
		mIntent = new Intent(Const.DOWNLOADRECEIVER);
		downloadQueue = new LinkedList<Integer>();
		isCancelled = false;
		hasDownloaded = 0;
		allDownload = 0;
	}

	public int getCurrentDownloadProductId() {
		return downloadingId;
	}

	public int getFileSize(int productId) {
		if (downloadingId == productId) {
			return totalSize;
		}
		return 0;
	}

	public int getDownloadedSize(int productId) {
		if (downloadingId == productId) {
			return downloadedSize;
		}
		return 0;
	}

	public boolean isRunning() {
		return !downloadQueue.isEmpty();
	}

	public void stopDownload(int productId) {
		if (productId == downloadingId) {
			isCancelled = true;
			Log.v(Const.DOWNLOAD, "Project ID " + productId
					+ " is the current task. Ready to cancel.");
		} else if (downloadQueue.contains(productId)) {
			downloadQueue.remove(productId);
			offlineDao.updateDownloadStatus(this, productId, Download.STOPPED);
			allDownload--;
			updateNotification();
			Log.v(Const.DOWNLOAD, "Project  ID " + productId
					+ " has not started. --> DATABASE STATUS: STOPPED");
		} else {
			Log.v(Const.DOWNLOAD, "Project ID " + productId
					+ " is not found in download queue.");
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean isFirstDownload = !isRunning();
		int productId = intent.getIntExtra(Const.DOWNLOAD, 0);
		downloadQueue.offer(productId);
		allDownload++;
		offlineDao.updateDownloadStatus(this, productId, Download.NOTSTARTED);
		Log.v(Const.DOWNLOAD,
				"Project ID "
						+ productId
						+ " has been added to download list. --> DATABASE STATUS: NOTSTARTED");
		if (isFirstDownload) {
			notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mBuilder = new Builder(this);
			mBuilder.setContentTitle("玩啥").setSmallIcon(R.drawable.ic_launcher)
					.setTicker("开始下载离线路书").setOngoing(true);
		}
		updateNotification();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		isCancelled = false;
		downloadingId = intent.getIntExtra(Const.DOWNLOAD, 0);
		if (downloadQueue.peek() == null
				|| downloadingId != downloadQueue.peek()) {
			downloadingId = 0;
			return;
		}
		try {
			Product product = productDao.getProductDetail(downloadingId);
			totalSize = offlineDao.getDownloadSize(product);
			Log.v(Const.DOWNLOAD, "Project ID " + product.getProductId()
					+ " has been received size: " + totalSize
					+ ". --> DATABASE STATUS: NOTSTARTED");
			mIntent.putExtra(Const.COMMAND, Const.UPDATEFILESIZE);
			mIntent.putExtra(Const.PRODUCTID, product.getProductId());
			mIntent.putExtra(Const.FILESIZE, totalSize);
			sendBroadcast(mIntent);

			try {
				mIntent.putExtra(Const.COMMAND, Const.DOWNLOADSTART);
				mIntent.putExtra(Const.PRODUCTID, product.getProductId());
				offlineDao.updateDownloadStatus(this, product.getProductId(),
						Download.STARTED);
				Log.v(Const.DOWNLOAD, "Project ID " + product.getProductId()
						+ " start downloading. --> DATABASE STATUS: STARTED");
				sendBroadcast(mIntent);

				offlineDao.insertProduct(this, product);
				Log.v(Const.DOWNLOAD,
						"Project ID "
								+ product.getProductId()
								+ " has been inserted into database. --> DATABASE STATUS: STARTED");
				downloadImages(product);
				completeDownload(product.getProductId());
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof HttpException
						&& ((HttpException) e).getExceptionCause() == HttpException.REQUEST_CANCELLED) {
					offlineDao.updateDownloadStatus(this,
							product.getProductId(), Download.STOPPED);
					allDownload--;
					updateNotification();
					Log.v(Const.DOWNLOAD,
							"Project ID "
									+ product.getProductId()
									+ " cancelled downloading. --> DATABASE STATUS: STOPPED");
					mIntent.putExtra(Const.COMMAND, Const.DOWNLOADCANCELLED);
					mIntent.putExtra(Const.PRODUCTID, product.getProductId());
					sendBroadcast(mIntent);
				} else {
					reportError(product.getProductId());
				}
				offlineDao.rollback(this, product.getProductId());
				Log.v(Const.DOWNLOAD, "Project ID " + product.getProductId()
						+ " download failed. --> DATABASE STATUS: ROLLBACK");
			}
		} catch (ProductException e) {
			e.printStackTrace();
			reportError(downloadingId);
		} finally {
			downloadQueue.poll();
			if (!isRunning()) {
				String info = null;
				if (hasDownloaded == allDownload) {
					info = "下载全部完成";
				} else {
					info = "下载结束，" + (allDownload - hasDownloaded)
							+ "个文件下载失败，请重试";
				}
				mBuilder.setContentText(info);
				mBuilder.setProgress(allDownload, hasDownloaded, false);
				mBuilder.setOngoing(false);
				notifyManager.notify(1, mBuilder.build());
				hasDownloaded = 0;
				allDownload = 0;
			}
		}
	}

	private void downloadImages(Product product) throws Exception {
		if (isCancelled) {
			throw new HttpException(HttpException.REQUEST_CANCELLED);
		}
		downloadedSize = 0;
		HttpURLConnection urlConn = null;
		File path = prepareDir(product.getProductId());
		List<String> imageUrls = product.getProductImages();
		for (int i = 0; i < imageUrls.size(); i++) {
			URL url = new URL(imageUrls.get(i));
			try {

				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.connect();

				InputStream is = new BufferedInputStream(url.openStream());

				FileOutputStream fos = new FileOutputStream(new File(path, i
						+ ""));

				int count;
				byte data[] = new byte[10240];
				while ((count = is.read(data)) != -1 && !isCancelled) {
					downloadedSize += count;
					mIntent.putExtra(Const.COMMAND, Const.UPDATEPROGRESS);
					mIntent.putExtra(Const.PRODUCTID, product.getProductId());
					mIntent.putExtra(Const.DOWNLOADPROGRESS, downloadedSize);
					sendBroadcast(mIntent);
					fos.write(data, 0, count);
				}
				fos.flush();
				fos.close();
				is.close();
			} catch (Exception e) {
				throw new HttpException(HttpException.HOST_CONNECT_FAILED);
			} finally {
				if (urlConn != null) {
					urlConn.disconnect();
				}
				if (isCancelled) {
					throw new HttpException(HttpException.REQUEST_CANCELLED);
				}
			}
		}
	}

	private void reportError(int productId) {
		offlineDao.updateDownloadStatus(this, productId, Download.ERROR);
		Log.v(Const.DOWNLOAD, "Project ID " + productId
				+ " has error in downloading. --> DATABASE STATUS: ERROR");
		mIntent.putExtra(Const.COMMAND, Const.DOWNLOADERROR);
		mIntent.putExtra(Const.PRODUCTID, productId);
		sendBroadcast(mIntent);
	}

	private void completeDownload(int productId) {
		hasDownloaded++;
		updateNotification();
		offlineDao.removeDownload(this, productId);
		offlineDao.updateGuideStatus(this, productId, Download.COMPLETED);
		Log.v(Const.DOWNLOAD, "Project ID " + productId
				+ " complete downloading. --> DATABASE STATUS: REMOVED");
		mIntent.putExtra(Const.COMMAND, Const.DOWNLOADCOMPLETE);
		mIntent.putExtra(Const.PRODUCTID, productId);
		sendBroadcast(mIntent);
	}

	private File prepareDir(int productId) {
		File path = new File(
				this.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
				productId + "");
		if (!path.exists()) {
			path.mkdir();
		} else if (path.isDirectory()) {
			File[] files = path.listFiles();
			for (File file : files) {
				file.delete();
				System.out.println("delete: " + file.getPath());
			}
		}
		return path;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class DownloadBinder extends Binder {

		public OfflineGuideDownloadService getService() {
			return OfflineGuideDownloadService.this;
		}
	}

	@Override
	public void onCreate() {
		Log.v(Const.DOWNLOAD, "Download Service Create.");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.v(Const.DOWNLOAD, "Download Service Destroy.");
		super.onDestroy();
	}

	private void updateNotification() {
		mBuilder.setContentText(
				"正在下载中: " + (hasDownloaded + 1) + " / " + allDownload)
				.setProgress(allDownload, hasDownloaded, false);
		notifyManager.notify(1, mBuilder.build());
	}
}
