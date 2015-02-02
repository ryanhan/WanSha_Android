package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ipang.wansha.R;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.dao.impl.UserDaoImpl;
import com.ipang.wansha.exception.UserException;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;

public class ChangePasswordActivity extends Activity {

	private ActionBar actionBar;
	private SharedPreferences pref;
	private String userName;
	private UserDao userDao;
	private EditText currentPassword;
	private EditText newPassword;
	private EditText confirmPassword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		pref = this.getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		userName = pref.getString(Const.USERNAME, null);
		userDao = new UserDaoImpl();
		setActionBar();
		setViews();
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(
				R.string.user_change_password));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {
		currentPassword = (EditText) findViewById(R.id.current_password);
		newPassword = (EditText) findViewById(R.id.new_password);
		confirmPassword = (EditText) findViewById(R.id.confirm_new_password);
		Button confirmChange = (Button) findViewById(R.id.button_change_password);

		confirmChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(currentPassword.getText())) {
					Toast.makeText(ChangePasswordActivity.this, "请输入当前密码",
							Toast.LENGTH_SHORT).show();
				} else if (TextUtils.isEmpty(newPassword.getText())) {
					Toast.makeText(ChangePasswordActivity.this, "请输入新密码",
							Toast.LENGTH_SHORT).show();
				} else if (newPassword.getText().length() < 6) {
					Toast.makeText(ChangePasswordActivity.this, "请输入6位以上新密码",
							Toast.LENGTH_SHORT).show();
				} else if (!newPassword.getText().toString()
						.equals(confirmPassword.getText().toString())) {
					Toast.makeText(ChangePasswordActivity.this, "请保证两次密码输入一致",
							Toast.LENGTH_SHORT).show();
				} else {
					ChangePasswordAsyncTask changePasswordAsyncTask = new ChangePasswordAsyncTask();
					changePasswordAsyncTask.execute(currentPassword.getText()
							.toString(), newPassword.getText().toString());
				}
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

	private class ChangePasswordAsyncTask extends
			AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			User user = null;
			try {
				user = userDao.checkLoginStatus(userName, params[0],
						Const.JSESSIONID);
			} catch (UserException e) {
				e.printStackTrace();
				if (e.getExceptionCause() == UserException.LOGIN_FAILED) {
					Editor editor = pref.edit();
					editor.clear();
					editor.putBoolean(Const.HASLOGIN, false);
					editor.commit();
					Intent intent = new Intent();
					intent.setClass(ChangePasswordActivity.this,
							LoginActivity.class);
					startActivityForResult(intent, Const.LOGIN_REQUEST);
					ChangePasswordActivity.this.overridePendingTransition(
							R.anim.bottom_up, R.anim.fade_out);
				} else {
					return false;
				}
			}
			if (user == null)
				return false;

			try {
				userDao.changePassword(params[0], params[1],
						user.getJSessionId());
			} catch (UserException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(ChangePasswordActivity.this, "修改密码成功",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				ChangePasswordActivity.this.finish();
			} else {
				Toast.makeText(ChangePasswordActivity.this, "修改密码失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
