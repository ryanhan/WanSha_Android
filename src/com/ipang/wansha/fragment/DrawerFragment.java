package com.ipang.wansha.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.activity.LoginActivity;
import com.ipang.wansha.activity.MyBookingActivity;
import com.ipang.wansha.activity.MyGuideCountryActivity;
import com.ipang.wansha.activity.SystemSettingActivity;
import com.ipang.wansha.activity.UserAdminActivity;
import com.ipang.wansha.utils.Const;

public class DrawerFragment extends Fragment {

	private SharedPreferences pref;
	private boolean hasLogin;
	private LinearLayout loginLayout;
	private LinearLayout userLayout;
	private TextView userNameText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = this.getActivity().getSharedPreferences(Const.USERINFO,
				Context.MODE_PRIVATE);
		// hasLogin = pref.getBoolean(Const.HASLOGIN, false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_drawer, null);

		userNameText = (TextView) view.findViewById(R.id.text_user_name);

		loginLayout = (LinearLayout) view.findViewById(R.id.layout_login);
		userLayout = (LinearLayout) view.findViewById(R.id.layout_user);
		LinearLayout orderLayout = (LinearLayout) view
				.findViewById(R.id.layout_all_order);
		LinearLayout guideBook = (LinearLayout) view
				.findViewById(R.id.layout_guide_book);
		LinearLayout discountLayout = (LinearLayout) view
				.findViewById(R.id.layout_discout);
		LinearLayout messageLayout = (LinearLayout) view
				.findViewById(R.id.layout_my_message);
		LinearLayout settingsLayout = (LinearLayout) view
				.findViewById(R.id.layout_settings);

		loginLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(DrawerFragment.this.getActivity(),
						LoginActivity.class);
				startActivityForResult(intent, Const.LOGIN_REQUEST);
				DrawerFragment.this.getActivity().overridePendingTransition(
						R.anim.bottom_up, R.anim.fade_out);
			}
		});

		userLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(DrawerFragment.this.getActivity(),
						UserAdminActivity.class);
				startActivity(intent);
			}
		});

		orderLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasLogin) {
					Intent intent = new Intent();
					intent.setClass(DrawerFragment.this.getActivity(),
							MyBookingActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(DrawerFragment.this.getActivity(),
							LoginActivity.class);
					startActivityForResult(intent, Const.LOGIN_REQUEST);
					DrawerFragment.this.getActivity()
							.overridePendingTransition(R.anim.bottom_up,
									R.anim.fade_out);
				}
			}
		});

		guideBook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(DrawerFragment.this.getActivity(),
						MyGuideCountryActivity.class);
				startActivity(intent);
			}
		});

		discountLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		messageLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			}
		});

		settingsLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(DrawerFragment.this.getActivity(),
						SystemSettingActivity.class);
				startActivity(intent);
			}
		});

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Const.LOGIN_REQUEST
				&& resultCode == Activity.RESULT_OK) {
			hasLogin = data.getBooleanExtra(Const.HASLOGIN, false);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		hasLogin = pref.getBoolean(Const.HASLOGIN, false);
		if (hasLogin) {
			userNameText.setText(pref.getString(Const.USERNAME, ""));
			loginLayout.setVisibility(View.GONE);
			userLayout.setVisibility(View.VISIBLE);
		} else {
			loginLayout.setVisibility(View.VISIBLE);
			userLayout.setVisibility(View.GONE);
		}
	}

}