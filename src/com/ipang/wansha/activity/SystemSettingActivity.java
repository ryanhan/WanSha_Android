package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.dao.AppDao;
import com.ipang.wansha.dao.impl.AppDaoImpl;
import com.ipang.wansha.utils.CheckUpdateAsyncTask;
import com.ipang.wansha.utils.Const;

public class SystemSettingActivity extends Activity {
	private ActionBar actionBar;
	private SharedPreferences pref;
	private AppDao appDao;

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
				CheckUpdateAsyncTask checkUpdateAysncTask = new CheckUpdateAsyncTask(SystemSettingActivity.this, true);
				checkUpdateAysncTask.execute();
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
}
