package com.ipang.wansha.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.CityListAdapter;
import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.impl.CityDaoImpl;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;

public class CityListActivity extends Activity {

	private ActionBar actionBar;
	private int countryId;
	private String countryName;
	private CityDao cityDao;
	private List<City> cities;
	private CityListAdapter adapter;

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
		countryName = bundle.getString(Const.COUNTRYNAME);
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(countryName);
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setListView() {
		cityDao = new CityDaoImpl();
		cities = new ArrayList<City>();
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		
		int height = (int) (metric.widthPixels - 2 * getResources().getDimension(R.dimen.activity_horizontal_margin) - getResources().getDimension(R.dimen.city_gridview_horizontal_space))/2;
		
		adapter = new CityListAdapter(this, cities, height);
		
		GridView cityGridView = (GridView) findViewById(R.id.city_grid_view);
		cityGridView.setAdapter(adapter);
		cityGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(CityListActivity.this,
						ProductListActivity.class);
				intent.putExtra(Const.CITYID, cities.get(position).getCityId());
				intent.putExtra(Const.CITYNAME, cities.get(position)
						.getCityName());
				intent.putExtra(Const.COUNTRYNAME, countryName);
				startActivity(intent);
			}
		});

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
		}
	}

}
