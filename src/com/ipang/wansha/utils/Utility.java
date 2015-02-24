package com.ipang.wansha.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ipang.wansha.R;
import com.ipang.wansha.activity.LoginActivity;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.dao.impl.UserDaoImpl;
import com.ipang.wansha.exception.UserException;
import com.ipang.wansha.model.User;

public class Utility {

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static void drawRankingStar(ImageView[] images, float ranking) {
		int full_score = images.length;
		int star_full_number = (int) ranking;
		int star_empty_number = (int) (full_score - ranking);
		float remaining = ranking - star_full_number;

		if (remaining > 0 && remaining < 0.25) {
			star_empty_number++;
		} else if (remaining >= 0.25 && remaining < 0.75) {
			images[star_full_number].setImageResource(R.drawable.star_half);
		} else if (remaining >= 0.75 && remaining < 1) {
			star_full_number++;
		}

		for (int i = 0; i < star_full_number; i++) {
			images[i].setImageResource(R.drawable.star_full);
		}

		for (int i = full_score - 1; i >= full_score - star_empty_number; i--) {
			images[i].setImageResource(R.drawable.star_empty);
		}
	}

	public static String FormatFullDate(Date date) {
		return DateFormat.getDateInstance(DateFormat.FULL).format(date);
	}

	public static String FormatDateTime(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static Date ParseString(String dateStr) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.parse(dateStr);
	}

	public static float sp2Pixel(Context context, int sp) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return sp * scaledDensity;
	}

	public static String formatText(String text) {
		text = text.replaceAll("<img src=\"/", "<img src=\"" + Const.SERVERNAME
				+ "/");
		String converted = Html.fromHtml(text).toString().replaceAll("\t", "")
				.replaceAll("\n\n", "\n").replaceAll("\n\n\n+", "\n\n");
		return converted;
	}

	public static String[] splitChnEng(String text) {
		int index = text.indexOf(' ');
		String[] res = new String[2];
		if (index == -1) {
			res[0] = text;
			res[1] = "";
			return res;
		}
		res[0] = text.substring(0, index);
		if (index == text.length() - 1) {
			res[1] = "";
		} else {
			res[1] = text.substring(index + 1);
		}
		return res;
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static User getUserLoginStatus(String userName, String password,
			String jSessionId, Activity activity, SharedPreferences pref) {
		UserDao userDao = new UserDaoImpl();
		User user = null;
		try {
			user = userDao.checkLoginStatus(userName, password, jSessionId);
		} catch (UserException e) {
			e.printStackTrace();
			if (e.getExceptionCause() == UserException.LOGIN_FAILED) {
				Editor editor = pref.edit();
				editor.clear();
				editor.putBoolean(Const.HASLOGIN, false);
				editor.commit();
				Intent intent = new Intent();
				intent.setClass(activity, LoginActivity.class);
				activity.startActivityForResult(intent, Const.LOGIN_REQUEST);
				activity.overridePendingTransition(R.anim.bottom_up,
						R.anim.fade_out);
			}
		}

		if (user != null) {
			Editor editor = pref.edit();
			editor.putString(Const.JSESSIONID, user.getJSessionId());
			editor.commit();
		}
		
		return user;
	}
}
