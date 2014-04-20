package com.ipang.wansha.fragment;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ipang.wansha.R;
import com.ipang.wansha.activity.ProductListActivity;
import com.ipang.wansha.adapter.RecommendImagePagerAdapter;
import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.impl.CityDaoImpl;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class CityFragment extends SherlockFragment implements
		OnPageChangeListener {

	private ViewPager recommendPager;
	private ImageView[] imageViews;
	private CityDao cityDao;
	private TextView recommendProductText;
	private TextView recommendCityNameText;
	private ArrayList<String> recommendCityIds;
	private ArrayList<String> recommendCityNames;
	private ArrayList<String> recommendProductNumber;
	private int recommendCityNumber;
	private boolean isContinue = true;
	private AtomicInteger what = new AtomicInteger(0);
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		context = this.getSherlockActivity();
		cityDao = new CityDaoImpl();
		// setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cities, null);
		recommendProductText = (TextView) view
				.findViewById(R.id.recommend_product_number);
		recommendCityNameText = (TextView) view
				.findViewById(R.id.recommend_city_name);
		initViewPager(view);
		setCityList(view);

		return view;
	}

	private void setCityList(View view) {

		try {
			List<City> cities = cityDao.getCityList();
			LinearLayout layout = (LinearLayout) view
					.findViewById(R.id.fragment_city_layout);
			for (int i = 0; i < cities.size(); i++) {
				layout.addView(cityView(cities.get(i)));
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		ImageView imageView = new ImageView(this.getSherlockActivity());
		LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, (int) getResources()
						.getDimension(R.dimen.image_height));
		imageView.setLayoutParams(imageParams);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		if (city.getPreviewImage() != null) {
			int resID = getResources().getIdentifier(city.getPreviewImage(),
					"drawable", Const.PACKAGENAME);
			imageView.setImageResource(resID);
		} else {
			imageView.setImageResource(R.drawable.missing);
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
		;
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

	private void initViewPager(View view) {
		recommendPager = (ViewPager) view.findViewById(R.id.pager_recommend);
		ViewGroup group = (ViewGroup) view
				.findViewById(R.id.fragment_city_viewGroup);

		List<City> recommendCities = null;
		try {
			recommendCities = cityDao.getRecommendCity();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recommendCityNumber = recommendCities.size();
		ArrayList<View> recommendImages = new ArrayList<View>();
		recommendCityIds = new ArrayList<String>();
		recommendCityNames = new ArrayList<String>();
		recommendProductNumber = new ArrayList<String>();

		for (int i = 0; i < recommendCityNumber; i++) {
			ImageView img = new ImageView(this.getSherlockActivity());
			if (recommendCities.get(i).getPreviewImage() != null) {
				int resID = getResources().getIdentifier(
						recommendCities.get(i).getPreviewImage(), "drawable",
						Const.PACKAGENAME);
				img.setImageResource(resID);
			} else {
				img.setImageResource(R.drawable.missing);
			}
			img.setScaleType(ScaleType.CENTER_CROP);
			recommendImages.add(img);
			recommendCityIds.add(recommendCities.get(i).getCityId());
			recommendCityNames.add(recommendCities.get(i).getCityName());
			recommendProductNumber.add(recommendCities.get(i).getProductCount()
					+ getResources().getString(R.string.products_number));
		}

		// Set Label(Name and Product Number) for First Recommend City
		recommendCityNameText.setText(recommendCityNames.get(0));
		recommendProductText.setText(recommendProductNumber.get(0));

		imageViews = new ImageView[recommendCityNumber];

		for (int i = 0; i < recommendCityNumber; i++) {
			ImageView imageView = new ImageView(this.getSherlockActivity());
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(10,
					10);
			param.rightMargin = 15;
			imageView.setLayoutParams(param);

			imageViews[i] = imageView;

			if (i == 0) {
				imageViews[i].setBackgroundResource(R.drawable.dot_selected);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.dot);
			}

			group.addView(imageViews[i]);
		}

		recommendPager.setAdapter(new RecommendImagePagerAdapter(this
				.getSherlockActivity(), recommendImages, recommendCityIds,
				recommendCityNames));
		recommendPager.setOnPageChangeListener(this);
		recommendPager.setCurrentItem(recommendCityNumber * 100);

		recommendPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					isContinue = false;
					break;
				case MotionEvent.ACTION_UP:
					isContinue = true;
					break;
				default:
					isContinue = true;
					break;
				}
				return false;
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (isContinue) {
						viewHandler.sendEmptyMessage(what.get());
						whatOption();
					}
				}
			}

		}).start();
	}

	private void whatOption() {
		what.incrementAndGet();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}
	}

	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			recommendPager.setCurrentItem(msg.what);
			super.handleMessage(msg);
		}

	};

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		what.getAndSet(arg0);
		int currentPage = arg0 % recommendCityNumber;

		// Set Label(Name and Product Number) for Recommend City
		recommendCityNameText.setText(recommendCityNames.get(currentPage));
		recommendProductText.setText(recommendProductNumber.get(currentPage));

		// Set round dot color for selected picture
		for (int i = 0; i < recommendCityNumber; i++) {
			imageViews[i].setBackgroundResource(R.drawable.dot_selected);
			if (currentPage != i) {
				imageViews[i].setBackgroundResource(R.drawable.dot);
			}
		}

	}

}
