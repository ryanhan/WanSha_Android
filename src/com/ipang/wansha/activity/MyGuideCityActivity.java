package com.ipang.wansha.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.GuideCityListAdapter;
import com.ipang.wansha.customview.XListView;
import com.ipang.wansha.customview.XListView.IXListViewListener;
import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class MyGuideCityActivity extends Activity implements IXListViewListener {

	private int countryId;
	private String actionBarTitle;
	private ActionBar actionBar;
	private List<City> guideCities;
	private XListView guideCityList;
	private OfflineDao offlineDao;
	private GuideCityListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_guide_country);
		getBundle();
		setActionBar();
		setViews();
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

	private void setViews() {
		offlineDao = new OfflineDaoImpl();
		guideCities = offlineDao.getCitiesByCountry(this, countryId);

		City allCities = new City();
		allCities.setCityName("所有城市 ALL CITIES");
		guideCities.add(0, allCities);

		guideCityList = (XListView) findViewById(R.id.list_my_guide_country);
		adapter = new GuideCityListAdapter(this, guideCities);
		guideCityList.setAdapter(adapter);
		guideCityList.setPullRefreshEnable(true);
		guideCityList.setPullLoadEnable(false);
		guideCityList.setXListViewListener(this);
		guideCityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (id == 0) {
					Intent intent = new Intent();
					intent.setClass(MyGuideCityActivity.this,
							MyGuideProductActivity.class);
					intent.putExtra(Const.GUIDELISTTYPE, Const.COUNTRYID);
					intent.putExtra(Const.COUNTRYID, countryId);
					intent.putExtra(Const.ACTIONBARTITLE, "所有城市");
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(MyGuideCityActivity.this,
							MyGuideProductActivity.class);
					intent.putExtra(Const.GUIDELISTTYPE, Const.CITYID);
					intent.putExtra(Const.CITYID, guideCities.get((int) id)
							.getCityId());
					String cityName = Utility.splitChnEng(guideCities.get(
							(int) id).getCityName())[0];
					intent.putExtra(Const.ACTIONBARTITLE, cityName);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

}
