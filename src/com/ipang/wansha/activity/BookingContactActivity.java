package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ipang.wansha.R;
import com.ipang.wansha.utils.Const;

public class BookingContactActivity extends Activity{

	private ActionBar actionBar;
	private SharedPreferences pref;
	private LayoutInflater iInflater;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking_contact);
		getBundle();
		setActionBar();
		setViews();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		pref = getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.book_now));
		actionBar.setDisplayUseLogoEnabled(false);
		
	}

	private void setViews() {
		iInflater = LayoutInflater.from(this);
//		View v = iInflater.inflate(R.layout.segment_contact_detail, null);

	}
	
}
