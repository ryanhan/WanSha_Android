package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ipang.wansha.R;
import com.ipang.wansha.utils.Const;

public class AddContactActivity extends Activity {

	private ActionBar actionBar;
	private EditText contactName;
	private EditText contactPinyin;
	private EditText contactPassport;
	private EditText contactMobile;
	private String name;
	private String pinyin;
	private String passport;
	private String mobile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		setActionBar();
		getBundle();
		setViews();
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.book_now));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		boolean isNull = bundle.getBoolean(Const.ISNULL);
		if (!isNull) {
			name = bundle.getString(Const.TRAVELLERNAME);
			pinyin = bundle.getString(Const.TRAVELLERPINYIN);
			passport = bundle.getString(Const.TRAVELLERPASSPORT);
			mobile = bundle.getString(Const.TRAVELLERMOBILE);
		}
	}

	private void setViews() {
		contactName = (EditText) findViewById(R.id.contact_name);
		contactPinyin = (EditText) findViewById(R.id.contact_pinyin);
		contactPassport = (EditText) findViewById(R.id.contact_passport);
		contactMobile = (EditText) findViewById(R.id.contact_mobile);

		if (name != null) {
			contactName.setText(name);
		}
		if (pinyin != null) {
			contactPinyin.setText(pinyin);
		}
		if (passport != null) {
			contactPassport.setText(passport);
		}
		if (mobile != null) {
			contactMobile.setText(mobile);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_add_contact, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AddContactActivity.this.finish();
			break;
		case R.id.finish:
			Intent intent = new Intent();
			if (contactName.getText().toString() == null
					|| contactName.getText().toString().equals("")) {
				Toast.makeText(
						AddContactActivity.this,
						getResources().getString(
								R.string.please_input_traveller_name),
						Toast.LENGTH_SHORT).show();
			} else {
				intent.putExtra(Const.TRAVELLERNAME, contactName.getText()
						.toString());
				if (contactPinyin.getText().toString() != null) {
					intent.putExtra(Const.TRAVELLERPINYIN, contactPinyin
							.getText().toString());
				}
				if (contactPassport.getText().toString() != null) {
					intent.putExtra(Const.TRAVELLERPASSPORT, contactPassport
							.getText().toString());
				}
				if (contactMobile.getText().toString() != null) {
					intent.putExtra(Const.TRAVELLERMOBILE, contactMobile
							.getText().toString());
				}
				setResult(RESULT_OK, intent);
				AddContactActivity.this.finish();
			}
		}

		return super.onOptionsItemSelected(item);
	}
}
