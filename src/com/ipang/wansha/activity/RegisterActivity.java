package com.ipang.wansha.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActionBar;
import android.app.Activity;
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

public class RegisterActivity extends Activity {

	private ActionBar actionBar;
	private EditText username;
	private EditText password;
	private EditText confirm;
	private UserDao userDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		userDao = new UserDaoImpl();
		setActionBar();
		setViews();
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.register));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {
		username = (EditText) findViewById(R.id.register_username);
		password = (EditText) findViewById(R.id.register_password);
		confirm = (EditText) findViewById(R.id.register_confirm_password);
		Button registerButton = (Button) findViewById(R.id.button_register);
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(username.getText())){
					Toast.makeText(RegisterActivity.this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
				}
				else{
					Pattern pattern = Pattern.compile("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+",Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(username.getText());
					if (!matcher.matches()){
						Toast.makeText(RegisterActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
					}
					else if (TextUtils.isEmpty(password.getText())){
						Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
					}
					else if(password.getText().length() < 6){
						Toast.makeText(RegisterActivity.this, "请输入6位以上密码", Toast.LENGTH_SHORT).show();
					}
					else if (!password.getText().toString().equals(confirm.getText().toString())){
						Toast.makeText(RegisterActivity.this, "请保证两次密码输入一致", Toast.LENGTH_SHORT).show();
					}
					else {
						RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask();
						registerAsyncTask
								.execute(username.getText().toString(), password.getText().toString());
					}
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			RegisterActivity.this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class RegisterAsyncTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				return userDao.register(params[0], params[1]);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == false) {
				Toast.makeText(RegisterActivity.this, "注册失败",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegisterActivity.this, "注册成功",
						Toast.LENGTH_SHORT).show();
				RegisterActivity.this.finish();
			}
		}
	}

}
