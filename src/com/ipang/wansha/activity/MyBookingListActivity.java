package com.ipang.wansha.activity;

import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListActivity;
import com.ipang.wansha.R;
import com.ipang.wansha.adapter.BookingListAdapter;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.dao.impl.UserDaoImpl;
import com.ipang.wansha.model.Booking;
import com.ipang.wansha.utils.Const;

public class MyBookingListActivity extends SherlockListActivity {

	private SharedPreferences pref;
	private String userId;
	private BookingListAdapter adapter;
	private List<Booking> bookingList;
	private UserDao userDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_booking_list);
		userId = pref.getString(Const.USERID, null);
		userDao = new UserDaoImpl();
		setViews();
	}

	private void setViews(){
		
		bookingList = userDao.getBookingOfUser(userId);
		adapter = new BookingListAdapter(this, bookingList);
		setListAdapter(adapter);
	}
}
