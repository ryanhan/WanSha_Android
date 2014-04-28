package com.ipang.wansha.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.ipang.wansha.R;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.dao.impl.UserDaoImpl;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;

public class LoginActivity extends SherlockActivity {

	private ActionBar actionBar;
	private SharedPreferences pref;
	private EditText username;
	private EditText password;
	private UserDao userDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		pref = this.getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		userDao = new UserDaoImpl();
		setActionBar();
		setViews();
	}

	private void setActionBar() {
		actionBar = this.getSupportActionBar();
		actionBar.setTitle("Login");
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void setViews() {
		username = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);

		Button loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
				loginAsyncTask.execute(username.getText().toString());
			}
		});
		LinearLayout register = (LinearLayout) findViewById(R.id.login_register_button);
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setUserLogin(User user) {
		Editor editor = pref.edit();
		editor.putString(Const.USERID, user.getUserId());
		editor.putString(Const.USERNAME, user.getUserName());
		// editor.putString(Const.TOKEN, password.getText().toString());
		editor.putBoolean(Const.HASLOGIN, true);
		editor.commit();
	}

	private void startResultActivity() {
		Intent intent = new Intent();
		intent.putExtra(Const.HASLOGIN, true);
		setResult(RESULT_OK, intent);
		finish();
	}

	private class LoginAsyncTask extends AsyncTask<String, Integer, User> {

		@Override
		protected User doInBackground(String... params) {
			User user = null;
			try {
				user = userDao.getUserInfo(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return user;
		}

		@Override
		protected void onPostExecute(User result) {
			if (result == null) {
				Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
						.show();
				setUserLogin(result);
				startResultActivity();
			}
		}
	}
}
