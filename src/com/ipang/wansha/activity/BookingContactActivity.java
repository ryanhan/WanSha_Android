package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipang.wansha.R;
import com.ipang.wansha.dao.BookingDao;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.dao.impl.BookingDaoImpl;
import com.ipang.wansha.dao.impl.UserDaoImpl;
import com.ipang.wansha.exception.BookingException;
import com.ipang.wansha.model.Contact;
import com.ipang.wansha.model.Traveller;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class BookingContactActivity extends Activity {

	private ActionBar actionBar;
	private SharedPreferences pref;
	private LayoutInflater iInflater;
	private int productId;
	private String userName;
	private String password;
	private User user;
	private int adultNumber;
	private int childNumber;
	private int totalPrice;
	private String travelDate;
	private String currency;
	private View[] contactViews;
	private Traveller[] travellers;
	private UserDao userDao;
	private BookingDao bookingDao;
	private ImageView loadingImage;
	private AnimationDrawable animationDrawable;
	private LinearLayout loadingLayout;
	private LinearLayout bookingContactLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking_contact);
		getBundle();
		setActionBar();
		getUserInfo();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		pref = getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		userName = pref.getString(Const.USERNAME, null);
		password = pref.getString(Const.PASSWORD, null);
		productId = bundle.getInt(Const.PRODUCTID);
		adultNumber = bundle.getInt(Const.ADULTNUMBER);
		childNumber = bundle.getInt(Const.CHILDNUMBER);
		totalPrice = bundle.getInt(Const.TOTALPRICE);
		travelDate = bundle.getString(Const.TRAVELDATE);
		currency = bundle.getString(Const.CURRENCY);
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.book_now));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void getUserInfo() {
		userDao = new UserDaoImpl();

		loadingImage = (ImageView) findViewById(R.id.image_loading);
		loadingImage.setBackgroundResource(R.anim.progress_animation);
		animationDrawable = (AnimationDrawable) loadingImage.getBackground();

		loadingLayout = (LinearLayout) findViewById(R.id.layout_loading);
		bookingContactLayout = (LinearLayout) findViewById(R.id.layout_booking_contact);
		bookingContactLayout.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();

		UserInfoAsyncTask userInfoAsyncTask = new UserInfoAsyncTask();
		userInfoAsyncTask.execute(userName, password);
	}

	private void setViews() {
		bookingDao = new BookingDaoImpl();
		iInflater = LayoutInflater.from(this);
		TextView adultNumberText = (TextView) findViewById(R.id.adult_number);
		TextView childNumberText = (TextView) findViewById(R.id.child_number);
		TextView dateText = (TextView) findViewById(R.id.date);
		TextView totalPriceText = (TextView) findViewById(R.id.total_price);

		LinearLayout contactDetailLayout = (LinearLayout) findViewById(R.id.layout_contact_detail);

		adultNumberText.setText("" + adultNumber);
		childNumberText.setText("" + childNumber);
		dateText.setText(travelDate);
		totalPriceText.setText(currency + totalPrice);

		contactViews = new View[adultNumber + childNumber];
		travellers = new Traveller[adultNumber + childNumber];
		for (int i = 0; i < travellers.length; i++) {
			travellers[i] = new Traveller();
		}

		if (adultNumber > 0) {
			for (int i = 0; i < adultNumber; i++) {
				contactViews[i] = createMemberDetailLayout(i, true);
				contactDetailLayout.addView(contactViews[i]);
			}
		}
		if (childNumber > 0) {
			for (int i = 0; i < childNumber; i++) {
				contactViews[i + adultNumber] = createMemberDetailLayout(i,
						false);
				contactDetailLayout.addView(contactViews[i + adultNumber]);
			}
		}

		for (int i = 0; i < contactViews.length; i++) {
			TextView memberNameText = (TextView) contactViews[i]
					.findViewById(R.id.member_name);
			final int index = i;
			memberNameText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(BookingContactActivity.this,
							AddContactActivity.class);
					if (travellers[index] != null) {
						intent.putExtra(Const.ISNULL, false);
						intent.putExtra(Const.TRAVELLERNAME,
								travellers[index].getName());
						intent.putExtra(Const.TRAVELLERPINYIN,
								travellers[index].getPinyin());
						intent.putExtra(Const.TRAVELLERPASSPORT,
								travellers[index].getPassport());
						intent.putExtra(Const.TRAVELLERMOBILE,
								travellers[index].getMobile());
					} else {
						intent.putExtra(Const.ISNULL, true);
					}
					startActivityForResult(intent, index);
				}
			});
		}

		final EditText contactName = (EditText) findViewById(R.id.contact_name);
		final EditText contactTel = (EditText) findViewById(R.id.contact_tel);
		final EditText contactEmail = (EditText) findViewById(R.id.contact_email);

		Button payNow = (Button) findViewById(R.id.pay_now);
		payNow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (contactName.getText().toString() == null
						|| contactName.getText().toString().equals("")) {
					Toast.makeText(
							BookingContactActivity.this,
							getResources().getString(
									R.string.please_input_contact_name),
							Toast.LENGTH_SHORT).show();
				} else if ((contactTel.getText().toString() == null || contactTel
						.getText().toString().equals(""))
						&& (contactEmail.getText().toString() == null || contactEmail
								.getText().toString().equals(""))) {
					Toast.makeText(
							BookingContactActivity.this,
							getResources().getString(
									R.string.please_input_contact_method),
							Toast.LENGTH_SHORT).show();
				} else {
					Contact contact = new Contact();
					contact.setName(contactName.getText().toString());
					if (contactTel.getText().toString() == null
							|| contactTel.getText().toString().equals("")) {
						contact.setTel(contactTel.getText().toString());
					}
					if (contactEmail.getText().toString() == null
							|| contactEmail.getText().toString().equals("")) {
						contact.setEmail(contactEmail.getText().toString());
					}
					BookingAsyncTask bookingAsyncTask = new BookingAsyncTask();
					bookingAsyncTask.execute(contact);
				}
			}
		});
	}

	private View createMemberDetailLayout(int i, boolean isAdult) {

		View v = iInflater.inflate(R.layout.segment_contact_detail, null);
		TextView memberTypeText = (TextView) v.findViewById(R.id.member_type);

		if (isAdult) {
			memberTypeText.setText(getResources().getString(R.string.adult)
					+ " " + (i + 1));
		} else {
			memberTypeText.setText(getResources().getString(R.string.child)
					+ " " + (i + 1));
		}

		return v;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			BookingContactActivity.this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			TextView contactName = (TextView) contactViews[requestCode]
					.findViewById(R.id.member_name);
			String name = data.getStringExtra(Const.TRAVELLERNAME);
			String pinyin = data.getStringExtra(Const.TRAVELLERPINYIN);
			String passport = data.getStringExtra(Const.TRAVELLERPASSPORT);
			String mobile = data.getStringExtra(Const.TRAVELLERMOBILE);

			travellers[requestCode].setName(name);
			travellers[requestCode].setPinyin(pinyin);
			travellers[requestCode].setPassport(passport);
			travellers[requestCode].setMobile(mobile);

			contactName.setText(name);
			contactName.setTextColor(getResources().getColor(R.color.black));
		}

	}

	private class UserInfoAsyncTask extends AsyncTask<String, Integer, User> {

		@Override
		protected User doInBackground(String... params) {

			user = Utility.getUserLoginStatus(params[0], params[1],
					pref.getString(Const.JSESSIONID, null),
					BookingContactActivity.this, pref);
			return user;
		}

		@Override
		protected void onPostExecute(User result) {
			if (result != null) {
				setViews();
				loadingLayout.setVisibility(View.INVISIBLE);
				bookingContactLayout.setVisibility(View.VISIBLE);
				animationDrawable.stop();
			} else {
				BookingContactActivity.this.finish();
			}
		}
	}

	private class BookingAsyncTask extends AsyncTask<Contact, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Contact... params) {
			try {
				bookingDao.submitBooking(productId, travelDate, totalPrice,
						adultNumber, childNumber, 0, travellers, params[0],
						user.getJSessionId());
				return true;
			} catch (BookingException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Intent intent = new Intent();
				intent.setClass(BookingContactActivity.this,
						BookingSuccessActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(BookingContactActivity.this,
						getResources().getString(R.string.book_failed),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
