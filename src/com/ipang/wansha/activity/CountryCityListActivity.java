package com.ipang.wansha.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ExpandableListView.OnChildClickListener;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.CountryCityListAdapter;
import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.impl.CityDaoImpl;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class CountryCityListActivity extends Activity {

	private ActionBar actionBar;
	private CountryCityListAdapter adapter;
	private ArrayList<String> countries;
	private ArrayList<ArrayList<City>> cities;
	private ExpandableListView countryCityList;
	private CityDao cityDao;
	private ImageView loadingImage;
	private AnimationDrawable animationDrawable;
	private LinearLayout loadingLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_city_list);
		setActionBar();
		setListView();
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.all_city_list));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setListView() {
		cityDao = new CityDaoImpl();
		countryCityList = (ExpandableListView) findViewById(R.id.country_expandable_list);

		loadingImage = (ImageView) findViewById(R.id.image_loading);
		loadingImage.setBackgroundResource(R.anim.progress_animation);
		animationDrawable = (AnimationDrawable) loadingImage.getBackground();

		loadingLayout = (LinearLayout) findViewById(R.id.layout_loading);
		countryCityList.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();

		countries = new ArrayList<String>();
		cities = new ArrayList<ArrayList<City>>();
		adapter = new CountryCityListAdapter(countries, cities, this);
		countryCityList.setAdapter(adapter);

		countryCityList.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {

				Intent intent = new Intent();
				intent.setClass(CountryCityListActivity.this,
						ProductListActivity.class);
				intent.putExtra(Const.CITYID,
						cities.get(groupPosition).get(childPosition)
								.getCityId());
				intent.putExtra(
						Const.ACTIONBARTITLE,
						Utility.splitChnEng(cities.get(groupPosition)
								.get(childPosition).getCityName())[0]);
				startActivity(intent);
				return true;
			}
		});

		CountryCityListAsyncTask countryListTask = new CountryCityListAsyncTask();
		countryListTask.execute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			CountryCityListActivity.this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class CountryCityListAsyncTask extends
			AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {

			try {
				countries.clear();
				cities.clear();
				HashMap<String, List<City>> map = cityDao.getCountryCityMap();
				Iterator<String> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					String country = iter.next();
					countries.add(country);
					List<City> city = map.get(country);
					cities.add((ArrayList<City>) city);
				}

				System.out.println(cities.size());
				return cities.size();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer groupCount) {
			adapter.notifyDataSetChanged();
			for (int i = 0; i < groupCount; i++) {
				countryCityList.expandGroup(i);
			}
			loadingLayout.setVisibility(View.INVISIBLE);
			countryCityList.setVisibility(View.VISIBLE);
			animationDrawable.stop();
		}
	}

}
