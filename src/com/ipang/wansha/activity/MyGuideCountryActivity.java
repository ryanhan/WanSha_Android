package com.ipang.wansha.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.GuideCountryListAdapter;
import com.ipang.wansha.customview.XListView;
import com.ipang.wansha.customview.XListView.IXListViewListener;
import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.model.Country;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class MyGuideCountryActivity extends Activity implements
		IXListViewListener {

	private ActionBar actionBar;
	private List<Country> guideCountries;
	private XListView guideCountryList;
	private OfflineDao offlineDao;
	private GuideCountryListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_guide_country);
		setActionBar();
		setViews();
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.guide_book));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setViews() {
		offlineDao = new OfflineDaoImpl();
		guideCountries = offlineDao.getAllCountries(this);

		Country allCountries = new Country();
		allCountries.setCountryName("所有国家 ALL COUNTRIES");
		guideCountries.add(0, allCountries);

		guideCountryList = (XListView) findViewById(R.id.list_my_guide_country);
		adapter = new GuideCountryListAdapter(this, guideCountries);
		guideCountryList.setAdapter(adapter);
		guideCountryList.setPullRefreshEnable(true);
		guideCountryList.setPullLoadEnable(false);
		guideCountryList.setXListViewListener(this);
		guideCountryList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (id == 0) {
					Intent intent = new Intent();
					intent.setClass(MyGuideCountryActivity.this,
							MyGuideProductActivity.class);
					intent.putExtra(Const.GUIDELISTTYPE, Const.ALL);
					intent.putExtra(Const.ALL, 1);
					intent.putExtra(Const.ACTIONBARTITLE, "所有国家");
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(MyGuideCountryActivity.this,
							MyGuideCityActivity.class);
					intent.putExtra(Const.COUNTRYID,
							guideCountries.get((int) id).getCountryId());
					String countryName = Utility.splitChnEng(guideCountries
							.get((int) id).getCountryName())[0];
					intent.putExtra(Const.ACTIONBARTITLE, countryName);
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
		case R.id.download_list:
			Intent intent = new Intent();
			intent.setClass(MyGuideCountryActivity.this,
					DownloadListActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_my_guide_list, menu);
		return super.onCreateOptionsMenu(menu);
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
