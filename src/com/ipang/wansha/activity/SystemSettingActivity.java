package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;

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

	private NotificationManager notificationManager;

	// private RemoteViews remoteView;
	// private Notification notification;
	// private NotificationManager manager = null;

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
			}
		});

		RelativeLayout about = (RelativeLayout) findViewById(R.id.layout_about);
		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
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

					// if (currentVersion < latestVersion) {
					confirmDownload(result.getUrl());
					// }

				} catch (Exception e) {
					e.printStackTrace();
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
					}

				}).create();
		alertDialog.show();
	}

	private void startDownload(String url) {

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		String tickerText = "开始下载更新";

		int icon = R.drawable.ic_launcher;

		Intent notificationIntent = new Intent(this, this.getClass());
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis());

		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notification_download);
		contentView.setTextViewText(R.id.download_rate, "35%");
		contentView.setProgressBar(R.id.download_progress, 100, 35, false);

		notification.contentView = contentView;
		notification.contentIntent = pendingIntent;

		notificationManager.notify(1, notification);
	}
}
