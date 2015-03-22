package com.ipang.wansha.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.BookingListAdapter;
import com.ipang.wansha.customview.XListView;
import com.ipang.wansha.customview.XListView.IXListViewListener;
import com.ipang.wansha.dao.BookingDao;
import com.ipang.wansha.dao.impl.BookingDaoImpl;
import com.ipang.wansha.model.Booking;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class MyBookingActivity extends Activity implements IXListViewListener {
	private ActionBar actionBar;
	private SharedPreferences pref;
	private String userName;
	private String password;
	private String jSessionId;
	private BookingDao bookingDao;
	private XListView bookingList;
	private BookingListAdapter adapter;
	private List<Booking> bookings;
	private ImageView loadingImage;
	private AnimationDrawable animationDrawable;
	private LinearLayout loadingLayout;
	private LinearLayout bookingLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_booking);
		pref = this.getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		getPrefs();
		setActionBar();
		setViews();
	}

	private void getPrefs() {
		userName = pref.getString(Const.USERNAME, null);
		password = pref.getString(Const.PASSWORD, null);
		jSessionId = pref.getString(Const.JSESSIONID, null);
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.all_order));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {
		bookingDao = new BookingDaoImpl();
		loadingImage = (ImageView) findViewById(R.id.image_loading);
		loadingImage.setBackgroundResource(R.anim.progress_animation);
		animationDrawable = (AnimationDrawable) loadingImage.getBackground();

		loadingLayout = (LinearLayout) findViewById(R.id.layout_loading);
		bookingLayout = (LinearLayout) findViewById(R.id.layout_my_booking);
		bookingLayout.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();

		bookings = new ArrayList<Booking>();

		bookingList = (XListView) findViewById(R.id.list_my_booking);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		int height = metric.widthPixels / 5;
		adapter = new BookingListAdapter(this, bookings, height);
		bookingList.setAdapter(adapter);
		bookingList.setPullRefreshEnable(true);
		bookingList.setPullLoadEnable(false);
		bookingList.setXListViewListener(this);

		BookingListAsyncTask asyncTask = new BookingListAsyncTask();
		asyncTask.execute(userName, password, jSessionId);
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

	private class BookingListAsyncTask extends
			AsyncTask<String, Integer, List<Booking>> {

		@Override
		protected List<Booking> doInBackground(String... params) {

			User user = Utility.getUserLoginStatus(params[0], params[1],
					params[2], MyBookingActivity.this, pref);

			List<Booking> bookingList = null;
			try {
				bookingList = bookingDao.getBookingList(user.getJSessionId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bookingList;
		}

		@Override
		protected void onPostExecute(List<Booking> result) {
			if (result != null) {
				bookings.clear();
				bookings.addAll(result);
				adapter.notifyDataSetChanged();
			}
			stopRefresh();
		}
	}

	@Override
	public void onRefresh() {
		bookingLayout.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();
		BookingListAsyncTask asyncTask = new BookingListAsyncTask();
		asyncTask.execute(userName, password, jSessionId);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	private void stopRefresh() {
		bookingList.stopRefresh();
		bookingList.setRefreshTime("刚刚");
		loadingLayout.setVisibility(View.INVISIBLE);
		bookingLayout.setVisibility(View.VISIBLE);
		animationDrawable.stop();
	}
}
