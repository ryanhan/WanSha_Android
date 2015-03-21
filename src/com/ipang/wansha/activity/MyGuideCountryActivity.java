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
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.GuideCountryListAdapter;
import com.ipang.wansha.adapter.OptionMenuAdapter;
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
	private DownloadProgressReceiver receiver;
	private OptionMenuAdapter optionAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_guide_country);
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

		if (guideCountries.size() > 0) {
			Country allCountries = new Country();
			allCountries.setCountryName("所有国家 ALL COUNTRIES");
			guideCountries.add(0, allCountries);
		}

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

		guideCountryList
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						
						final String[] options = new String[] { getResources().getString(R.string.delete_item)};
						optionAdapter = new OptionMenuAdapter(MyGuideCountryActivity.this, options);
						
						
						if (id != 0) {
							final int index = (int) id;
							Dialog alertDialog = new AlertDialog.Builder(
									MyGuideCountryActivity.this).setTitle(getResources().getString(R.string.select_option)).setAdapter(optionAdapter, new OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											offlineDao.deleteOfflineCountry(MyGuideCountryActivity.this, guideCountries.get(index).getCountryId());
											loadCountryList();
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
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onRefresh() {
		loadCountryList();
		guideCountryList.stopRefresh();
		guideCountryList.setRefreshTime("刚刚");
	}

	@Override
	public void onLoadMore() {
	}

	private class DownloadProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String command = intent.getStringExtra(Const.COMMAND);
			if (command.equals(Const.DOWNLOADCOMPLETE)) {
				loadCountryList();
			}
		}
	}
	
	private void loadCountryList(){
		guideCountries.clear();
		guideCountries.addAll(offlineDao.getAllCountries(MyGuideCountryActivity.this));
		if (guideCountries.size() > 0) {
			Country allCountries = new Country();
			allCountries.setCountryName("所有国家 ALL COUNTRIES");
			guideCountries.add(0, allCountries);
		}
		adapter.notifyDataSetChanged();
	}
}
