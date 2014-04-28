package com.ipang.wansha.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.ipang.wansha.R;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.dao.impl.UserDaoImpl;

public class RegisterActivity extends SherlockActivity {

	private ActionBar actionBar;
	private EditText username;
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
		actionBar = this.getSupportActionBar();
		actionBar.setTitle("Login");
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void setViews() {
		username = (EditText) findViewById(R.id.register_username);
		Button registerButton = (Button) findViewById(R.id.button_register);
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(username.getText())){
					Toast.makeText(RegisterActivity.this, "用户名不可为空", Toast.LENGTH_SHORT).show();
				}
				else{
					RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask();
					registerAsyncTask.execute(username.getText().toString());
				}
			}
		});
		
	}
	
	private class RegisterAsyncTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				return userDao.register(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == null) {
				Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
				RegisterActivity.this.finish();
			}
		}
	}

}
