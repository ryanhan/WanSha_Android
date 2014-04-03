package com.ipang.wansha.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import com.ipang.wansha.activity.ProductListActivity;
import com.ipang.wansha.utils.Const;

public class RecommendImagePagerAdapter extends PagerAdapter {
	private List<View> views = null;
	private ArrayList<String> cityNames;
	private ArrayList<String> cityIds;
	private Context context;

	public RecommendImagePagerAdapter(Context context, List<View> views,
			ArrayList<String> recommendCityIds, ArrayList<String> recommendCityNames) {
		this.context = context;
		this.views = views;
		this.cityNames = recommendCityNames;
		this.cityIds = recommendCityIds;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position % views.size()));
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		final int itemId = position % views.size();
		View view = views.get(itemId);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ProductListActivity.class);
				intent.putExtra(Const.CITYNAME, cityNames.get(itemId));
				intent.putExtra(Const.CITYID, cityIds.get(itemId));
				context.startActivity(intent);
			}
		});

		((ViewPager) container).addView(view, 0);
		return view;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

}
