package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.dao.impl.UserDaoImpl;
import com.ipang.wansha.exception.UserException;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;

public class UserAdminActivity extends Activity {

	private ActionBar actionBar;
	private SharedPreferences pref;
	private UserDao userDao;
	private int userId;
	private String userName;
	private String password;
	private TextView email;
	private TextView mobile;
	private TextView nickName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_admin);
		pref = this.getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		userDao = new UserDaoImpl();
		getUserInfo();
		setActionBar();
		setViews();
	}

	private void getUserInfo() {
		userId = pref.getInt(Const.USERID, 0);
		userName = pref.getString(Const.USERNAME, null);
		password = pref.getString(Const.PASSWORD, null);
		UserInfoAsyncTask userInfoAsyncTask = new UserInfoAsyncTask();
		userInfoAsyncTask.execute(userName, password);
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.user_info));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {
		nickName = (TextView) findViewById(R.id.nickname);
		nickName.setText(userName);
		email = (TextView) findViewById(R.id.email);
		mobile = (TextView) findViewById(R.id.mobile);
		RelativeLayout changePassword = (RelativeLayout) findViewById(R.id.layout_change_password);
		changePassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(UserAdminActivity.this,
						ChangePasswordActivity.class);
				startActivityForResult(intent, Const.CHANGE_PASSWORD);
			}
		});

		RelativeLayout logout = (RelativeLayout) findViewById(R.id.layout_logout);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LogoutAsyncTask logoutAsyncTask = new LogoutAsyncTask();
				logoutAsyncTask.execute();

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

	private class LogoutAsyncTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			User user = null;
			try {
				user = userDao.isAlive(pref.getString(Const.JSESSIONID, null));
				if (user != null) {
					userDao.logout(pref.getString(Const.JSESSIONID, null));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Editor editor = pref.edit();
			editor.clear();
			editor.putBoolean(Const.HASLOGIN, false);
			editor.commit();
			UserAdminActivity.this.finish();
		}
	}

	private class UserInfoAsyncTask extends AsyncTask<String, Integer, User> {

		@Override
		protected User doInBackground(String... params) {

			User user = null;
			try {
				user = userDao.isAlive(pref.getString(Const.JSESSIONID, null));
			} catch (UserException e) {
				e.printStackTrace();
				try {
					user = userDao.login(params[0], params[1]);
				} catch (UserException e1) {
					e1.printStackTrace();
					return null;
				}
				Editor editor = pref.edit();
				editor.putString(Const.JSESSIONID, user.getJSessionId());
				editor.commit();
			}
			
			return user;
		}

		@Override
		protected void onPostExecute(User result) {
			if (result != null) {
				nickName.setText(result.getUserName());
				email.setText(result.getEmail() == null ? getResources()
						.getString(R.string.not_bind) : result.getEmail());
				mobile.setText(result.getMobile() == null ? getResources()
						.getString(R.string.not_bind) : result.getMobile());
			}

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Const.CHANGE_PASSWORD
				&& resultCode == Activity.RESULT_OK) {
			// pref. change password
		}
	}
}
