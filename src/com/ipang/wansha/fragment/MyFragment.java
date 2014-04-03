package com.ipang.wansha.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ipang.wansha.R;
import com.ipang.wansha.activity.LoginActivity;
import com.ipang.wansha.utils.Const;

public class MyFragment extends SherlockFragment {

	private SharedPreferences pref;
	private boolean hasLogin;
	private TextView loginUserName;
	private ImageView loginIcon;
	private ImageView photoPreview;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		pref = this.getSherlockActivity().getSharedPreferences(Const.USERINFO,
				Context.MODE_PRIVATE);
		hasLogin = pref.getBoolean(Const.HASLOGIN, false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my, null);
		loginUserName = (TextView) view.findViewById(R.id.login_username);
		loginIcon = (ImageView) view.findViewById(R.id.login_icon);
		photoPreview = (ImageView) view.findViewById(R.id.photo_preview);
		RelativeLayout layout = (RelativeLayout) view
				.findViewById(R.id.login_layout);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println(hasLogin);
				if (hasLogin) {
					hasLogin = false;
					Editor editor = pref.edit();
					editor.putBoolean(Const.HASLOGIN, false);
					editor.remove(Const.USERNAME);
					editor.remove(Const.TOKEN);
					editor.commit();
					loginUserName.setText(getResources().getString(R.string.login));
					loginIcon.setVisibility(View.VISIBLE);
					photoPreview.setVisibility(View.GONE);
				} else {
					Intent intent = new Intent();
					intent.setClass(MyFragment.this.getSherlockActivity(),
							LoginActivity.class);
					startActivityForResult(intent, Const.LOGIN_REQUEST);
				}
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (hasLogin) {
			loginUserName.setText("USERNAME");
			loginIcon.setVisibility(View.GONE);
			photoPreview.setVisibility(View.VISIBLE);
		} else {
			loginUserName.setText(getResources().getString(R.string.login));
			loginIcon.setVisibility(View.VISIBLE);
			photoPreview.setVisibility(View.GONE);
		}
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);  
		if (requestCode == Const.LOGIN_REQUEST && resultCode == Activity.RESULT_OK) {
			hasLogin = data.getBooleanExtra(Const.HASLOGIN, false);
			System.out.println("MyFragment");
		}
	}
}