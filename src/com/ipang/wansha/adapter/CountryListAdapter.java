package com.ipang.wansha.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.model.Country;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class CountryListAdapter extends ArrayAdapter<Country> {

	private Context context;
	private LayoutInflater mInflater;
	private int height;

	public final class ViewHolder {
		public ImageView countryImage;
		public TextView countryNameTextView;
		public TextView countryEnglishTextView;
		public TextView productNumberTextView;
		public ProgressBar imageLoadingProgress;
	}

	public CountryListAdapter(Context context, List<Country> countries,
			int height) {
		super(context, 0, countries);
		this.context = context;
		this.height = height;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.adapter_listview_countries, null);

			holder.countryImage = (ImageView) convertView
					.findViewById(R.id.country_list_image);

			FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT, height);
			holder.countryImage.setLayoutParams(param);

			holder.countryNameTextView = (TextView) convertView
					.findViewById(R.id.country_list_name);
			holder.countryEnglishTextView = (TextView) convertView
					.findViewById(R.id.country_list_english);
			holder.productNumberTextView = (TextView) convertView
					.findViewById(R.id.country_list_product_number);
			holder.imageLoadingProgress = (ProgressBar) convertView
					.findViewById(R.id.progress_country_image_loading);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;

		String names[] = Utility
				.splitChnEng(getItem(position).getCountryName());

		viewHolder.countryNameTextView.setText(names[0]);
		viewHolder.countryEnglishTextView.setText(names[1]);
		
		viewHolder.productNumberTextView.setText(getItem(position)
				.getCountryProductCount()
				+ context.getResources().getString(R.string.products_number));

		ImageLoader.getInstance().displayImage(
				getItem(position).getCountryImage(), viewHolder.countryImage,
				Const.options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						Animation anim = AnimationUtils.loadAnimation(context,
								R.anim.fade_in);
						viewHolder.countryImage.setAnimation(anim);
						anim.start();
						viewHolder.imageLoadingProgress
								.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						viewHolder.imageLoadingProgress
								.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						viewHolder.imageLoadingProgress
								.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						viewHolder.imageLoadingProgress
								.setVisibility(View.VISIBLE);
					}

				});

		return convertView;
	}

}
