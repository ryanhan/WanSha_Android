package com.ipang.wansha.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.ipang.wansha.R;
import com.ipang.wansha.adapter.BookingListAdapter;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.dao.impl.UserDaoImpl;
import com.ipang.wansha.model.Booking;
import com.ipang.wansha.utils.Const;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyBookingListActivity extends SherlockListActivity {

	private ActionBar actionBar;
	private SharedPreferences pref;
	private String userId;
	private BookingListAdapter adapter;
	private List<Booking> bookingList;
	private UserDao userDao;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_booking_list);
		pref = getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		userId = pref.getString(Const.USERID, null);
		userDao = new UserDaoImpl();
		imageLoader = ImageLoader.getInstance();
		setActionBar();
		setViews();
	}

	private void setActionBar() {
		actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.my_booking));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {

		bookingList = new ArrayList<Booking>();
		adapter = new BookingListAdapter(this, bookingList, imageLoader);
		setListAdapter(adapter);

		BookingListAsyncTask asyncTask = new BookingListAsyncTask();
		asyncTask.execute(userId);
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
			try {
				bookingList.clear();
				//bookingList.addAll(userDao.getBookingOfUser(params[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bookingList;
		}

		@Override
		protected void onPostExecute(List<Booking> result) {
			adapter.notifyDataSetChanged();
		}
	}
}
