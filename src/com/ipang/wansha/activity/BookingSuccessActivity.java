package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ipang.wansha.R;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.dao.impl.UserDaoImpl;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;

public class BookingSuccessActivity extends Activity {

	private ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking_success);
		setActionBar();
		setViews();
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.login));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {
		Button button = (Button) findViewById(R.id.back_to_home);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(BookingSuccessActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}
}
