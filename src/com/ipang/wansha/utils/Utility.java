package com.ipang.wansha.utils;

import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ipang.wansha.R;

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
	
	public static String FormatDate(Date date){
		return DateFormat.getDateInstance(DateFormat.FULL).format(date);
	}
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
