package com.ipang.wansha.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

import com.ipang.wansha.R;
import com.ipang.wansha.dao.AppDao;
import com.ipang.wansha.dao.impl.AppDaoImpl;
import com.ipang.wansha.exception.AppInfoException;
import com.ipang.wansha.model.AppInfo;

public class CheckUpdateAsyncTask extends AsyncTask<Void, Integer, AppInfo> {

	private Context context;
	private boolean isActive;
	private NotificationManager notifyManager;
	private Builder mBuilder;

	public CheckUpdateAsyncTask(Context context, boolean isActive) {
		this.context = context;
		this.isActive = isActive;
	}

	@Override
	protected void onPreExecute() {
		if (isActive) {
			Toast.makeText(context, "正在检查更新...", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected AppInfo doInBackground(Void... params) {
		AppDao appDao = new AppDaoImpl();
		try {
			AppInfo appInfo = appDao.getLatestVersion();
			return appInfo;
		} catch (AppInfoException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(AppInfo result) {
		if (result == null) {
			if (isActive) {
				Toast.makeText(context, "检查更新出错，请重试", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			Date date = Calendar.getInstance().getTime();
			SharedPreferences pref = context.getSharedPreferences(
					Const.APPINFO, Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putString(Const.LASTUPDATE, Utility.FormatDateTime(date));
			editor.commit();

			try {
				PackageManager packageManager = context.getPackageManager();
				PackageInfo packInfo = packageManager.getPackageInfo(
						context.getPackageName(), 0);
				int currentVersion = packInfo.versionCode;
				int latestVersion = Integer.parseInt(result.getVersion());

				System.out.println("Current Version: " + currentVersion);
				System.out.println("Latest Version: " + latestVersion);

				if (currentVersion < latestVersion) {
					confirmDownload(result.getUrl());
				} else {
					if (isActive) {
						Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT)
								.show();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				if (isActive) {
					Toast.makeText(context, "检查更新出错，请重试", Toast.LENGTH_SHORT)
							.show();
				}
			}

		}
	}

	private void confirmDownload(final String url) {
		Dialog alertDialog = new AlertDialog.Builder(context)
				.setTitle("有更新的版本").setMessage("是否确认更新？")
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startDownload(url);
						Toast.makeText(context, "开始下载更新...", Toast.LENGTH_SHORT)
								.show();
					}

				}).create();
		alertDialog.show();
	}

	private void startDownload(String url) {
		notifyManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new Builder(context);
		mBuilder.setContentTitle("玩啥").setContentText("开始准备下载")
				.setSmallIcon(R.drawable.ic_launcher).setTicker("玩啥开始下载更新")
				.setOngoing(true);

		AppDownloaderAysncTask appDownloader = new AppDownloaderAysncTask();
		appDownloader.execute(url);
	}

	private class AppDownloaderAysncTask extends
			AsyncTask<String, Integer, File> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mBuilder.setProgress(100, 0, false);
			notifyManager.notify(1, mBuilder.build());
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mBuilder.setContentText("正在下载中 ( " + values[0] + "% )");
			mBuilder.setProgress(100, values[0], false);
			notifyManager.notify(1, mBuilder.build());
			super.onProgressUpdate(values);
		}

		@Override
		protected File doInBackground(String... params) {
			HttpURLConnection connection = null;
			try {
				int count;

				URL url = new URL(params[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				int lenghtOfFile = connection.getContentLength();
				InputStream input = new BufferedInputStream(url.openStream());

				String fileName = "wansha.apk";
				File downloadDir = new File(context.getExternalCacheDir(),
						Const.DOWNLOAD_DIRECTORY);
				if (!downloadDir.exists()) {
					downloadDir.mkdir();
				}

				File filePath = new File(downloadDir, fileName);

				FileOutputStream fos = new FileOutputStream(filePath);

				byte data[] = new byte[1024];
				long total = 0;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress((int) ((total * 100) / lenghtOfFile));
					fos.write(data, 0, count);
				}
				fos.flush();
				fos.close();
				input.close();
				return filePath;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}

		}

		@Override
		protected void onPostExecute(File result) {
			if (result == null) {
				mBuilder.setContentText("下载失败");
				mBuilder.setProgress(0, 0, false);
				mBuilder.setOngoing(false);
				notifyManager.notify(1, mBuilder.build());
				Toast.makeText(context, "下载发生错误", Toast.LENGTH_SHORT).show();
			} else {
				mBuilder.setContentText("下载完成");
				mBuilder.setProgress(0, 0, false);
				mBuilder.setOngoing(false);

				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(result),
						"application/vnd.android.package-archive");

				PendingIntent contentIntent = PendingIntent.getActivity(
						context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				mBuilder.setContentIntent(contentIntent);

				notifyManager.notify(1, mBuilder.build());

				System.out.println(result.getPath());
				Toast.makeText(context, "下载成功", Toast.LENGTH_SHORT).show();
				context.startActivity(intent);
			}
		}
	}
}