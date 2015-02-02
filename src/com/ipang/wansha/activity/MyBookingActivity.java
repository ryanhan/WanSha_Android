package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.ipang.wansha.R;
import com.ipang.wansha.utils.Const;

public class MyBookingActivity extends Activity {
	private ActionBar actionBar;
	private SharedPreferences pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_booking);
		pref = this.getSharedPreferences(Const.APPINFO, Context.MODE_PRIVATE);
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
