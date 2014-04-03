package com.ipang.wansha.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.ipang.wansha.R;
import com.ipang.wansha.utils.Const;

public class LoginActivity extends SherlockActivity {

	private ActionBar actionBar;
	private SharedPreferences pref;
	private EditText username;
	private EditText password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		pref = this.getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
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
				if (validateLogin()) {
					setUserLogin();
					startResultActivity();
				}
			}
		});
	}

	private boolean validateLogin() {
		return true;
	}

	private void setUserLogin() {
		Editor editor = pref.edit();
		editor.putString(Const.USERNAME, username.getText().toString());
		editor.putString(Const.TOKEN, password.getText().toString());
		editor.putBoolean(Const.HASLOGIN, true);
		editor.commit();
	}

	private void startResultActivity() {
		Intent intent = new Intent();
		intent.putExtra(Const.HASLOGIN, true);
		setResult(RESULT_OK, intent);
		finish();
	}
}
