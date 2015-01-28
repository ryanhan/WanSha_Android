package com.ipang.wansha.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipang.wansha.R;
import com.ipang.wansha.dao.AppDao;
import com.ipang.wansha.dao.impl.AppDaoImpl;
import com.ipang.wansha.exception.AppInfoException;
import com.ipang.wansha.model.AppInfo;
import com.ipang.wansha.utils.Const;

public class SystemSettingActivity extends Activity {
	private ActionBar actionBar;
	private SharedPreferences pref;
	private AppDao appDao;
	private NotificationManager notifyManager;
	private Builder mBuilder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_settings);
		pref = this.getSharedPreferences(Const.APPINFO, Context.MODE_PRIVATE);
		appDao = new AppDaoImpl();
		setActionBar();
		setViews();
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.system_settings));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {

		RelativeLayout feedback = (RelativeLayout) findViewById(R.id.layout_feedback);
		feedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		RelativeLayout clearCache = (RelativeLayout) findViewById(R.id.layout_clear_cache);
		clearCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		RelativeLayout checkUpdate = (RelativeLayout) findViewById(R.id.layout_check_update);
		checkUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckUpdateAysncTask checkUpdateAysncTask = new CheckUpdateAysncTask();
				checkUpdateAysncTask.execute();
				Toast.makeText(SystemSettingActivity.this, "正在检查更新...",
						Toast.LENGTH_SHORT).show();
			}
		});

		RelativeLayout about = (RelativeLayout) findViewById(R.id.layout_about);
		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		TextView checkUpdateText = (TextView) findViewById(R.id.check_update);

		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			String currentVersion = packInfo.versionName;
			checkUpdateText.setText(currentVersion);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class CheckUpdateAysncTask extends
			AsyncTask<Void, Integer, AppInfo> {

		@Override
		protected AppInfo doInBackground(Void... params) {
			try {
				AppInfo appInfo = appDao.getLatestVersion();
				return appInfo;
			} catch (AppInfoException e) {
				e.printStackTrace();
				Toast.makeText(SystemSettingActivity.this, "检查更新出错，请重试",
						Toast.LENGTH_SHORT).show();
				return null;
			}
		}

		@Override
		protected void onPostExecute(AppInfo result) {
			if (result != null) {
				try {
					PackageManager packageManager = getPackageManager();
					PackageInfo packInfo = packageManager.getPackageInfo(
							getPackageName(), 0);
					int currentVersion = packInfo.versionCode;
					int latestVersion = Integer.parseInt(result.getVersion());

					System.out.println("Current Version: " + currentVersion);
					System.out.println("Latest Version: " + latestVersion);

					if (currentVersion < latestVersion) {
						confirmDownload(result.getUrl());
					} else {
						Toast.makeText(SystemSettingActivity.this, "已是最新版本",
								Toast.LENGTH_SHORT).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(SystemSettingActivity.this, "检查更新出错，请重试",
							Toast.LENGTH_SHORT).show();
				}

			}
		}

	}

	private void confirmDownload(final String url) {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("有更新的版本")
				.setMessage("是否确认更新？").setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						startDownload(url);
						Toast.makeText(SystemSettingActivity.this, "开始下载更新...",
								Toast.LENGTH_SHORT).show();
					}

				}).create();
		alertDialog.show();
	}

	private void startDownload(String url) {
		notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new Builder(this);
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
			try {
				int count;

				URL url = new URL(params[0]);
				URLConnection connection = url.openConnection();
				connection.connect();

				int lenghtOfFile = connection.getContentLength();
				InputStream input = new BufferedInputStream(url.openStream());

				String fileName = "wansha.apk";
				File downloadDir = new File(getExternalCacheDir(),
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
			}

		}

		@Override
		protected void onPostExecute(File result) {
			if (result == null) {
				mBuilder.setContentText("下载失败");
				mBuilder.setProgress(0, 0, false);
				mBuilder.setOngoing(false);
				notifyManager.notify(1, mBuilder.build());
				Toast.makeText(SystemSettingActivity.this, "下载发生错误",
						Toast.LENGTH_SHORT).show();
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
						SystemSettingActivity.this, 0, intent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				mBuilder.setContentIntent(contentIntent);

				notifyManager.notify(1, mBuilder.build());

				System.out.println(result.getPath());
				Toast.makeText(SystemSettingActivity.this, "下载成功",
						Toast.LENGTH_SHORT).show();
				installApk(intent);
			}
		}
	}

	private void installApk(Intent intent) {
		startActivity(intent);
	}
}
