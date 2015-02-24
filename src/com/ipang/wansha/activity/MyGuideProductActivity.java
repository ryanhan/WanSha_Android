package com.ipang.wansha.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.GuideProductListAdapter;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_guide_product);
		getBundle();
		setActionBar();
		setViews();
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
		actionBar.setTitle(getResources().getString(R.string.guide_book));
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
