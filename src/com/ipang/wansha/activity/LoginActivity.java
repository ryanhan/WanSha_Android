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

public class LoginActivity extends Activity {

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
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.login));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {
		username = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);

		Button loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
				loginAsyncTask.execute(username.getText().toString(), password.getText().toString());
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
		editor.putInt(Const.USERID, user.getUserId());
		editor.putString(Const.USERNAME, user.getUserName());
		editor.putString(Const.PASSWORD, user.getPassword());
		editor.putString(Const.JSESSIONID, user.getJSessionId());
		editor.putBoolean(Const.HASLOGIN, true);
		editor.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.bottom_down);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			overridePendingTransition(R.anim.fade_in, R.anim.bottom_down);
		}
		return super.onKeyDown(keyCode, event);
	}

	private class LoginAsyncTask extends AsyncTask<String, Integer, User> {

		@Override
		protected User doInBackground(String... params) {
			User user = null;
			try {
				user = userDao.login(params[0], params[1]);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return user;
		}

		@Override
		protected void onPostExecute(User result) {
			if (result == null) {
				Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT)
						.show();
				setUserLogin(result);
				Intent intent = new Intent();
				intent.putExtra(Const.HASLOGIN, true);
				setResult(RESULT_OK, intent);
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.fade_in, R.anim.bottom_down);
			}
		}
	}
}
