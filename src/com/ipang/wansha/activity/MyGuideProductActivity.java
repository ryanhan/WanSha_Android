package com.ipang.wansha.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.GuideProductListAdapter;
import com.ipang.wansha.adapter.OptionMenuAdapter;
import com.ipang.wansha.customview.XListView;
import com.ipang.wansha.customview.XListView.IXListViewListener;
import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class MyGuideProductActivity extends Activity implements
		IXListViewListener {

	private String type;
	private int id;
	private String actionBarTitle;
	private ActionBar actionBar;
	private List<Product> guideProducts;
	private XListView guideProductList;
	private OfflineDao offlineDao;
	private GuideProductListAdapter adapter;
	private DownloadProgressReceiver receiver;
	private OptionMenuAdapter optionAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_guide_product);
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
		type = bundle.getString(Const.GUIDELISTTYPE);
		id = bundle.getInt(type);
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

		if (type.equals(Const.ALL)) {
			guideProducts = offlineDao.getAllProducts(this);
		} else if (type.equals(Const.COUNTRYID)) {
			guideProducts = offlineDao.getProductsByCountry(this, id);
		} else if (type.equals(Const.CITYID)) {
			guideProducts = offlineDao.getProductsByCity(this, id);
		}

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int height = metric.widthPixels / 5;

		guideProductList = (XListView) findViewById(R.id.list_my_guide_product);
		adapter = new GuideProductListAdapter(this, guideProducts, height);
		guideProductList.setAdapter(adapter);
		guideProductList.setPullRefreshEnable(true);
		guideProductList.setPullLoadEnable(false);
		guideProductList.setXListViewListener(this);
		guideProductList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int index = (int) id;
				Intent intent = new Intent();
				intent.setClass(MyGuideProductActivity.this,
						ProductDetailActivity.class);
				intent.putExtra(Const.PRODUCTID, guideProducts.get(index)
						.getProductId());
				intent.putExtra(Const.GETMETHOD, Const.OFFLINE);
				intent.putExtra(Const.ACTIONBARTITLE,
						Utility.splitChnEng(guideProducts.get(index)
								.getProductName())[0]);
				startActivity(intent);
			}
		});
		
		guideProductList
		.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				
				final String[] options = new String[] { getResources().getString(R.string.delete_item)};
				optionAdapter = new OptionMenuAdapter(MyGuideProductActivity.this, options);
				
				
				if (id != 0) {
					final int index = (int) id;
					Dialog alertDialog = new AlertDialog.Builder(
							MyGuideProductActivity.this).setTitle(getResources().getString(R.string.select_option)).setAdapter(optionAdapter, new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									offlineDao.deleteOfflineCity(MyGuideProductActivity.this, guideProducts.get(index).getProductId());
									loadProductList();
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
	public void onRefresh() {
		loadProductList();
		guideProductList.stopRefresh();
		guideProductList.setRefreshTime("刚刚");
	}

	@Override
	public void onLoadMore() {
	}

	private class DownloadProgressReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String command = intent.getStringExtra(Const.COMMAND);
			if (command.equals(Const.DOWNLOADCOMPLETE)) {
				loadProductList();
			}
		}
	}
	
	private void loadProductList(){
		guideProducts.clear();
		if (type.equals(Const.ALL)) {
			guideProducts.addAll(offlineDao.getAllProducts(this));
		} else if (type.equals(Const.COUNTRYID)) {
			guideProducts.addAll(offlineDao.getProductsByCountry(this, id));
		} else if (type.equals(Const.CITYID)) {
			guideProducts.addAll(offlineDao.getProductsByCity(this, id));
		}
		adapter.notifyDataSetChanged();
	}

}
