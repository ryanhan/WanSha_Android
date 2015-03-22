package com.ipang.wansha.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.GuideCityListAdapter;
import com.ipang.wansha.adapter.OptionMenuAdapter;
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
	private DownloadProgressReceiver receiver;
	private OptionMenuAdapter optionAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_guide_country);
		getBundle();
		setActionBar();
		setViews();
		registerReceiver();
	}

	private void registerReceiver() {
		receiver = new DownloadProgressReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Const.DOWNLOADRECEIVER);
		registerReceiver(receiver, intentFilter);
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
		allCities.setCityName(actionBarTitle
				+ getResources().getString(R.string.all_city_products));
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

					String title = Utility.splitChnEng(actionBarTitle
							+ getResources().getString(
									R.string.all_city_products))[0];

					intent.putExtra(Const.ACTIONBARTITLE, title);
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

		guideCityList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				final String[] options = new String[] { getResources()
						.getString(R.string.delete_item) };
				optionAdapter = new OptionMenuAdapter(MyGuideCityActivity.this,
						options);

				if (id != 0) {
					final int index = (int) id;
					Dialog alertDialog = new AlertDialog.Builder(
							MyGuideCityActivity.this)
							.setTitle(
									getResources().getString(
											R.string.select_option))
							.setAdapter(optionAdapter, new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									offlineDao.deleteOfflineCity(
											MyGuideCityActivity.this,
											guideCities.get(index).getCityId());
									loadCityList();
								}
							}).create();
					alertDialog.show();

					return true;
				}

				return false;
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
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onRefresh() {
		loadCityList();
		guideCityList.stopRefresh();
		guideCityList.setRefreshTime("刚刚");
	}

	@Override
	public void onLoadMore() {
	}

	private class DownloadProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String command = intent.getStringExtra(Const.COMMAND);
			if (command.equals(Const.DOWNLOADCOMPLETE)) {
				loadCityList();
			}
		}
	}

	private void loadCityList() {
		guideCities.clear();
		guideCities.addAll(offlineDao.getCitiesByCountry(
				MyGuideCityActivity.this, countryId));
		City allCities = new City();
		allCities.setCityName(actionBarTitle
				+ getResources().getString(R.string.all_city_products));
		guideCities.add(0, allCities);
		adapter.notifyDataSetChanged();
	}

}
