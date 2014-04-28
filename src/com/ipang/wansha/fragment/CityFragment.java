package com.ipang.wansha.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ipang.wansha.R;
import com.ipang.wansha.activity.ProductListActivity;
import com.ipang.wansha.activity.SearchActvity;
import com.ipang.wansha.adapter.RecommendImagePagerAdapter;
import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.impl.CityDaoImpl;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class CityFragment extends SherlockFragment {

	private ImageView[] dots;
	private CityDao cityDao;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context context;
	private View fragmentView;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		context = this.getSherlockActivity();
		cityDao = new CityDaoImpl();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_cities, null);
		imageLoader = ImageLoader.getInstance();
		options = Const.options;
		setSearchText();
		ViewPagerAsyncTask viewPagerTask = new ViewPagerAsyncTask();
		viewPagerTask.execute(fragmentView);
		CityListAsyncTask cityListTask = new CityListAsyncTask();
		cityListTask.execute(fragmentView);

		return fragmentView;
	}

	private void setSearchText() {
		EditText search = (EditText) fragmentView.findViewById(R.id.search_bar_text);
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, SearchActvity.class);
				startActivity(intent);
			}
		});
	}

	private void setCityList(List<City> cities) {

		LinearLayout layout = (LinearLayout) fragmentView
				.findViewById(R.id.fragment_city_layout);
		for (int i = 0; i < cities.size(); i++) {
			layout.addView(cityView(cities.get(i)));
		}

	}

	private View cityView(final City city) {
		// ImageView
		FrameLayout frameLayout = new FrameLayout(this.getSherlockActivity());
		LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		frameParams.bottomMargin = (int) getResources().getDimension(
				R.dimen.fragment_city_list_gap);
		frameLayout.setLayoutParams(frameParams);

		final ImageView imageView = new ImageView(this.getSherlockActivity());
		LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, (int) getResources()
						.getDimension(R.dimen.image_height));
		imageView.setLayoutParams(imageParams);
		imageView.setScaleType(ScaleType.CENTER_CROP);

		if (city.getPreviewImage() == null) {
			imageView.setImageResource(R.drawable.missing);
		} else {
			imageLoader.displayImage(
					Const.SERVERNAME + "/rest/image/" + city.getPreviewImage(),
					imageView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri, View view,
								Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(
									context, R.anim.fade_in);
							imageView.setAnimation(anim);
							anim.start();
						}
					});
		}

		frameLayout.addView(imageView);

		RelativeLayout relativeLayout = new RelativeLayout(
				this.getSherlockActivity());

		// TextView X个产品
		TextView productNumberText = new TextView(this.getSherlockActivity());
		RelativeLayout.LayoutParams productNumberParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		productNumberParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		productNumberParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		productNumberParams.leftMargin = Utility.dip2px(
				this.getSherlockActivity(), 5);
		productNumberParams.bottomMargin = Utility.dip2px(
				this.getSherlockActivity(), 3);
		productNumberText.setLayoutParams(productNumberParams);
		productNumberText.setTextColor(getResources().getColor(R.color.white));
		productNumberText.setBackgroundColor(getResources().getColor(
				R.color.sky_blue));
		productNumberText.setPadding(
				(int) getResources().getDimension(
						R.dimen.fragment_city_textview_horizontal_padding),
				(int) getResources().getDimension(
						R.dimen.fragment_city_textview_vertical_padding),
				(int) getResources().getDimension(
						R.dimen.fragment_city_textview_horizontal_padding),
				(int) getResources().getDimension(
						R.dimen.fragment_city_textview_vertical_padding));
		productNumberText.setText(city.getProductCount()
				+ getResources().getString(R.string.products_number));
		productNumberText.setId(1);
		relativeLayout.addView(productNumberText);

		// TextView 城市名
		TextView cityNameText = new TextView(this.getSherlockActivity());
		RelativeLayout.LayoutParams cityNameParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		cityNameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		cityNameParams.addRule(RelativeLayout.ABOVE, 1);
		cityNameParams.leftMargin = Utility.dip2px(this.getSherlockActivity(),
				5);
		cityNameParams.bottomMargin = Utility.dip2px(
				this.getSherlockActivity(), 3);
		cityNameText.setLayoutParams(cityNameParams);
		cityNameText.setBackgroundColor(getResources().getColor(R.color.white));
		cityNameText.setPadding(
				(int) getResources().getDimension(
						R.dimen.fragment_city_textview_horizontal_padding),
				(int) getResources().getDimension(
						R.dimen.fragment_city_textview_vertical_padding),
				(int) getResources().getDimension(
						R.dimen.fragment_city_textview_horizontal_padding),
				(int) getResources().getDimension(
						R.dimen.fragment_city_textview_vertical_padding));
		cityNameText.setText(city.getCityName());
		relativeLayout.addView(cityNameText);

		frameLayout.addView(relativeLayout);
		frameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(CityFragment.this.getSherlockActivity(),
						ProductListActivity.class);
				intent.putExtra(Const.CITYNAME, city.getCityName());
				intent.putExtra(Const.CITYID, city.getCityId());
				startActivity(intent);

			}
		});

		return frameLayout;
	}

	private void setRecommendViewPager(final List<City> cityList) {
		final ViewPager viewPager = (ViewPager) fragmentView
				.findViewById(R.id.pager_recommend);
		setDotBar(cityList.size());

		viewPager.setAdapter(new RecommendImagePagerAdapter(this
				.getSherlockActivity(), cityList, imageLoader));
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						for (int i = 0; i < cityList.size(); i++) {
							dots[i].setBackgroundResource(i == position ? R.drawable.dot_selected
									: R.drawable.dot);
						}
					}
				});
		viewPager.setCurrentItem(0);
	}

	private void setDotBar(int number) {
		ViewGroup group = (ViewGroup) fragmentView
				.findViewById(R.id.fragment_city_viewGroup);
		group.setVisibility(View.VISIBLE);
		dots = new ImageView[number];

		for (int i = 0; i < number; i++) {
			ImageView imageView = new ImageView(this.getSherlockActivity());
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(10,
					10);
			param.rightMargin = 15;
			imageView.setLayoutParams(param);

			dots[i] = imageView;
			dots[i].setBackgroundResource(i == 0 ? R.drawable.dot_selected
					: R.drawable.dot);

			group.addView(dots[i]);
		}
	}

	private class ViewPagerAsyncTask extends
			AsyncTask<View, Integer, List<City>> {

		@Override
		protected List<City> doInBackground(View... params) {
			List<City> cities = null;
			try {
				cities = cityDao.getRecommendCity();
			} catch (Exception e) {
				e.printStackTrace();
				cities = new ArrayList<City>();
			}
			return cities;
		}

		@Override
		protected void onPostExecute(List<City> result) {
			setRecommendViewPager(result);
		}
	}

	private class CityListAsyncTask extends
			AsyncTask<View, Integer, List<City>> {

		@Override
		protected List<City> doInBackground(View... params) {
			List<City> cities = null;
			try {
				cities = cityDao.getCityList();
			} catch (Exception e) {
				e.printStackTrace();
				cities = new ArrayList<City>();
			}
			return cities;
		}

		@Override
		protected void onPostExecute(List<City> result) {
			setCityList(result);
		}
	}

}
