package com.ipang.wansha.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.activity.CityListActivity;
import com.ipang.wansha.activity.ProductListActivity;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class CityListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<City> cities;
	private int height;
	private String countryName;

	public final class ViewHolder {
		public ImageView cityImageLeft;
		public TextView cityNameTextViewLeft;
		public TextView productNumberTextViewLeft;
		public ProgressBar imageLoadingProgressLeft;
		public FrameLayout layoutLeft;

		public ImageView cityImageRight;
		public TextView cityNameTextViewRight;
		public TextView productNumberTextViewRight;
		public ProgressBar imageLoadingProgressRight;
		public FrameLayout layoutRight;
	}

	public CityListAdapter(Context context, List<City> cities, int height,
			String countryName) {
		this.cities = cities;
		this.context = context;
		this.height = height;
		this.countryName = countryName;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_listview_cities,
					null);

			holder.cityImageLeft = (ImageView) convertView
					.findViewById(R.id.city_list_image_left);
			FrameLayout.LayoutParams paramLeft = new FrameLayout.LayoutParams(
					height, height);
			holder.cityImageLeft.setLayoutParams(paramLeft);
			holder.cityNameTextViewLeft = (TextView) convertView
					.findViewById(R.id.city_list_name_left);
			holder.productNumberTextViewLeft = (TextView) convertView
					.findViewById(R.id.city_list_product_number_left);
			holder.imageLoadingProgressLeft = (ProgressBar) convertView
					.findViewById(R.id.progress_city_image_loading_left);
			holder.layoutLeft = (FrameLayout) convertView
					.findViewById(R.id.layout_city_list_left);

			holder.cityImageRight = (ImageView) convertView
					.findViewById(R.id.city_list_image_right);
			FrameLayout.LayoutParams paramRight = new FrameLayout.LayoutParams(
					height, height);
			holder.cityImageRight.setLayoutParams(paramRight);
			holder.cityNameTextViewRight = (TextView) convertView
					.findViewById(R.id.city_list_name_right);
			holder.productNumberTextViewRight = (TextView) convertView
					.findViewById(R.id.city_list_product_number_right);
			holder.imageLoadingProgressRight = (ProgressBar) convertView
					.findViewById(R.id.progress_city_image_loading_right);
			holder.layoutRight = (FrameLayout) convertView
					.findViewById(R.id.layout_city_list_right);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;
		viewHolder.cityNameTextViewLeft.setText(Utility.splitChnEng(getItem(
				position * 2).getCityName())[0]);
		viewHolder.productNumberTextViewLeft.setText(getItem(position * 2)
				.getProductCount()
				+ context.getResources().getString(R.string.products_number));

		ImageLoader.getInstance().displayImage(
				getItem(position * 2).getCityImage(), viewHolder.cityImageLeft,
				Const.options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						Animation anim = AnimationUtils.loadAnimation(context,
								R.anim.fade_in);
						viewHolder.cityImageLeft.setAnimation(anim);
						anim.start();
						viewHolder.imageLoadingProgressLeft
								.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						viewHolder.imageLoadingProgressLeft
								.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						viewHolder.imageLoadingProgressLeft
								.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						viewHolder.imageLoadingProgressLeft
								.setVisibility(View.VISIBLE);
					}
				});

		viewHolder.layoutLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ProductListActivity.class);
				intent.putExtra(Const.CITYID, cities.get(position * 2)
						.getCityId());
				intent.putExtra(Const.CITYNAME, cities.get(position * 2)
						.getCityName());
				intent.putExtra(Const.COUNTRYNAME, countryName);
				context.startActivity(intent);
			}
		});

		if (position * 2 + 1 < cities.size()) {

			viewHolder.layoutRight.setVisibility(View.VISIBLE);

			viewHolder.cityNameTextViewRight.setText(Utility
					.splitChnEng(getItem(position * 2 + 1).getCityName())[0]);
			viewHolder.productNumberTextViewRight.setText(getItem(
					position * 2 + 1).getProductCount()
					+ context.getResources()
							.getString(R.string.products_number));

			ImageLoader.getInstance().displayImage(
					getItem(position * 2 + 1).getCityImage(),
					viewHolder.cityImageRight, Const.options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(
									context, R.anim.fade_in);
							viewHolder.cityImageRight.setAnimation(anim);
							anim.start();
							viewHolder.imageLoadingProgressRight
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							viewHolder.imageLoadingProgressRight
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							viewHolder.imageLoadingProgressRight
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							viewHolder.imageLoadingProgressRight
									.setVisibility(View.VISIBLE);
						}
					});

			viewHolder.layoutRight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(context, ProductListActivity.class);
					intent.putExtra(Const.CITYID, cities.get(position * 2 + 1)
							.getCityId());
					intent.putExtra(Const.CITYNAME, cities
							.get(position * 2 + 1).getCityName());
					intent.putExtra(Const.COUNTRYNAME, countryName);
					context.startActivity(intent);
				}
			});

		} else {
			viewHolder.layoutRight.setVisibility(View.GONE);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		if (cities.size() % 2 == 0)
			return cities.size() / 2;
		else
			return cities.size() / 2 + 1;
	}

	@Override
	public City getItem(int position) {
		return cities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
