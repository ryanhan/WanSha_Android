package com.ipang.wansha.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.activity.ProductListActivity;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class RecommendImagePagerAdapter extends PagerAdapter {

	private List<City> cityList;
	private Context context;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public RecommendImagePagerAdapter(Context context, List<City> cityList,
			ImageLoader imageLoader) {
		this.context = context;
		this.cityList = cityList;
		this.mInflater = LayoutInflater.from(context);
		this.imageLoader = imageLoader;
		this.options = Const.options;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public int getCount() {
		return cityList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View imageLayout = mInflater.inflate(R.layout.view_pager_recommend,
				container, false);
		final ImageView imageView = (ImageView) imageLayout
				.findViewById(R.id.recommend_image);
		TextView cityNameText = (TextView) imageLayout
				.findViewById(R.id.recommend_city_name);
		TextView productNumberText = (TextView) imageLayout
				.findViewById(R.id.recommend_product_number);
		String imageUri = Const.SERVERNAME + "/rest/image/"
				+ cityList.get(position).getPreviewImage();

		cityNameText.setText(cityList.get(position).getCityName());
		productNumberText.setText(cityList.get(position).getProductCount()
				+ context.getResources().getString(R.string.products_number));

		imageLoader.displayImage(imageUri, imageView, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						Animation anim = AnimationUtils.loadAnimation(context,
								R.anim.fade_in);
						imageView.setAnimation(anim);
						anim.start();
					}
				});

		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ProductListActivity.class);
				intent.putExtra(Const.CITYNAME, cityList.get(position).getCityName());
				intent.putExtra(Const.CITYID, cityList.get(position).getCityId());
				context.startActivity(intent);
			}
		});

		container.addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
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
