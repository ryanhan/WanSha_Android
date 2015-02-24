package com.ipang.wansha.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.exception.HttpException;
import com.ipang.wansha.exception.ProductException;
import com.ipang.wansha.model.Download;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.DatabaseHelper;

public class OfflineGuideDownloadService extends IntentService {

	private ProductDao productDao;
	private OfflineDao offlineDao;
	private int downloadingId;
	private int totalSize;
	private int downloadedSize;
	private final IBinder mBinder;

	public OfflineGuideDownloadService() {
		super("OfflineGuideDownloadService");
		productDao = new ProductDaoImpl();
		offlineDao = new OfflineDaoImpl();
		mBinder = new DownloadBinder();
	}
	
	public int getCurrentDownloadProductId(){
		return downloadingId;
	}

	public int getFileSize(int productId){
		if (downloadingId == productId){
			return totalSize;
		}
		return 0;
	}
	
	public int getDownloadedSize(int productId){
		if (downloadingId == productId){
			return downloadedSize;
		}
		return 0;
	}
	
	public void stopDownload(int productId) {
		
	}

	private Intent mIntent = new Intent(Const.DOWNLOADRECEIVER);

	@Override
	protected void onHandleIntent(Intent intent) {

		Download download = (Download) intent
				.getSerializableExtra(Const.DOWNLOAD);
		downloadingId = download.getProductId();
		try {
			Product product = productDao.getProductDetail(download
					.getProductId());
			totalSize = offlineDao.getDownloadSize(product);

			mIntent.putExtra(Const.COMMAND, Const.UPDATEFILESIZE);
			mIntent.putExtra(Const.PRODUCTID, product.getProductId());
			mIntent.putExtra(Const.FILESIZE, totalSize);
			sendBroadcast(mIntent);

			DatabaseHelper dbHelper = new DatabaseHelper(this,
					DatabaseHelper.DATABASENAME);
			SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
			try {
				insertProduct(sqliteDatabase, product);
				downloadImages(product);
				completeDownload(product.getProductId());
			} catch (Exception e) {
				e.printStackTrace();
				offlineDao.rollback(this, product.getProductId());
			} finally {
				dbHelper.close();
			}
		} catch (ProductException e) {
			e.printStackTrace();
		}
	}

	private void insertProduct(SQLiteDatabase sqliteDatabase, Product product) {

		sqliteDatabase.delete(DatabaseHelper.OFFLINEGUIDE,
				DatabaseHelper.PRODUCTID + "=?",
				new String[] { product.getProductId() + "" });
		sqliteDatabase.delete(DatabaseHelper.LOCALIMAGE,
				DatabaseHelper.PRODUCTID + "=?",
				new String[] { product.getProductId() + "" });

		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.PRODUCTID, product.getProductId());
		values.put(DatabaseHelper.PRODUCTNAME, product.getProductName());
		values.put(DatabaseHelper.CITYID, product.getCityId());
		values.put(DatabaseHelper.CITYNAME, product.getCityName());
		values.put(DatabaseHelper.COUNTRYID, product.getCountryId());
		values.put(DatabaseHelper.COUNTRYNAME, product.getCountryName());
		values.put(DatabaseHelper.PRODUCTTYPE, product.getProductType());
		values.put(DatabaseHelper.DETAIL, product.getDetail());
		values.put(DatabaseHelper.EXPENSEDESCR, product.getExpenseDescr());
		values.put(DatabaseHelper.INSTRUCTION, product.getInstruction());
		values.put(DatabaseHelper.ORDERDESCR, product.getOrderDescr());
		values.put(DatabaseHelper.BRIEF, product.getBrief());
		values.put(DatabaseHelper.LASTMODIFIED, product.getLastModified());
		sqliteDatabase.insert(DatabaseHelper.OFFLINEGUIDE, null, values);

		List<String> imageList = product.getProductImages();
		String path = "file://"
				+ this.getExternalFilesDir(
						android.os.Environment.DIRECTORY_PICTURES).getPath();
		for (int i = 0; i < imageList.size(); i++) {
			ContentValues imageValues = new ContentValues();
			imageValues.put(DatabaseHelper.PRODUCTID, product.getProductId());
			imageValues.put(DatabaseHelper.FILENAME,
					path + "/" + product.getProductId() + "/" + i);
			imageValues.put(DatabaseHelper.IMAGEURL, imageList.get(i));
			imageValues.put(DatabaseHelper.SEQ, i);
			sqliteDatabase.insert(DatabaseHelper.LOCALIMAGE, null, imageValues);
		}
	}

	private void downloadImages(Product product) throws Exception {

		mIntent.putExtra(Const.COMMAND, Const.DOWNLOADSTART);
		mIntent.putExtra(Const.PRODUCTID, product.getProductId());
		offlineDao.updateDownloadStatus(this, product.getProductId(), Download.STARTED);
		sendBroadcast(mIntent);
		
		downloadedSize = 0;
		File path = prepareDir(product.getProductId());
		List<String> imageUrls = product.getProductImages();
		for (int i = 0; i < imageUrls.size(); i++) {
			URL url = new URL(imageUrls.get(i));
			HttpURLConnection urlConn = null;
			try {

				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.connect();

				InputStream is = new BufferedInputStream(url.openStream());

				FileOutputStream fos = new FileOutputStream(new File(path, i
						+ ""));

				int count;
				byte data[] = new byte[1024];
				while ((count = is.read(data)) != -1) {
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
			}
		}
	}
	

	private void completeDownload(int productId) {
		offlineDao.removeDownload(this, productId);
		mIntent.putExtra(Const.COMMAND, Const.DOWNLOADCOMPLETE);
		mIntent.putExtra(Const.PRODUCTID, productId);
		sendBroadcast(mIntent);
		System.out.println("Downloaded " + getCurrentDownloadProductId());
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
}
