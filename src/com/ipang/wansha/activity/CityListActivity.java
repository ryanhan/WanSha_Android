package com.ipang.wansha.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.CityListAdapter;
import com.ipang.wansha.customview.XListView;
import com.ipang.wansha.customview.XListView.IXListViewListener;
import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.impl.CityDaoImpl;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;

public class CityListActivity extends Activity implements IXListViewListener {

	private ActionBar actionBar;
	private String actionBarTitle;
	private int countryId;
	private CityDao cityDao;
	private List<City> cities;
	private CityListAdapter adapter;
	private ImageView loadingImage;
	private AnimationDrawable animationDrawable;
	private LinearLayout loadingLayout;
	private LinearLayout cityLayout;
	private XListView cityListView;
	private boolean isRefreshing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_list);
		getBundle();
		setActionBar();
		setListView();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		countryId = bundle.getInt(Const.COUNTRYID);
		actionBarTitle = bundle.getString(Const.ACTIONBARTITLE);
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(actionBarTitle);
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setListView() {
		cityDao = new CityDaoImpl();
		cities = new ArrayList<City>();

		loadingImage = (ImageView) findViewById(R.id.image_loading);
		loadingImage.setBackgroundResource(R.anim.progress_animation);
		animationDrawable = (AnimationDrawable) loadingImage.getBackground();

		loadingLayout = (LinearLayout) findViewById(R.id.layout_loading);
		cityLayout = (LinearLayout) findViewById(R.id.layout_city_list);
		cityLayout.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();

		isRefreshing = false;

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		int height = (int) (metric.widthPixels
				- 2
				* getResources().getDimension(
						R.dimen.activity_horizontal_margin) - getResources()
				.getDimension(R.dimen.city_list_horizontal_space)) / 2;

		adapter = new CityListAdapter(this, cities, height);

		cityListView = (XListView) findViewById(R.id.city_list_view);
		cityListView.setAdapter(adapter);
		cityListView.setPullRefreshEnable(true);
		cityListView.setPullLoadEnable(false);
		cityListView.setXListViewListener(this);

		CityListAsyncTask cityListAsyncTask = new CityListAsyncTask();
		cityListAsyncTask.execute(countryId);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			CityListActivity.this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_city_list, menu);
		return true;
	}

	private class CityListAsyncTask extends
			AsyncTask<Integer, Integer, List<City>> {

		@Override
		protected List<City> doInBackground(Integer... params) {

			try {
				cities.clear();
				cities.addAll(cityDao.getCityList(params[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return cities;
		}

		@Override
		protected void onPostExecute(List<City> result) {
			adapter.notifyDataSetChanged();
			loadingLayout.setVisibility(View.INVISIBLE);
			cityLayout.setVisibility(View.VISIBLE);
			animationDrawable.stop();
			stopRefresh();
		}
	}

	@Override
	public void onRefresh() {
		if (!isRefreshing) {
			isRefreshing = true;
			cityLayout.setVisibility(View.INVISIBLE);
			loadingLayout.setVisibility(View.VISIBLE);
			animationDrawable.start();
			CityListAsyncTask cityListAsyncTask = new CityListAsyncTask();
			cityListAsyncTask.execute(countryId);
		}
	}

	@Override
	public void onLoadMore() {
	}

	private void stopRefresh() {
		isRefreshing = false;
		cityListView.stopRefresh();
		cityListView.setRefreshTime("刚刚");
		loadingLayout.setVisibility(View.INVISIBLE);
		cityLayout.setVisibility(View.VISIBLE);
		animationDrawable.stop();
	}
}
