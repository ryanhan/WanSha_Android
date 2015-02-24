package com.ipang.wansha.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.ProductListAdapter;
import com.ipang.wansha.customview.XListView;
import com.ipang.wansha.customview.XListView.IXListViewListener;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.enums.SortType;
import com.ipang.wansha.fragment.SortListFragment;
import com.ipang.wansha.fragment.SortListFragment.OnSortTypeChangedListener;
import com.ipang.wansha.model.Download;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.DatabaseUtility;
import com.ipang.wansha.utils.Utility;

public class ProductListActivity extends FragmentActivity implements
		OnSortTypeChangedListener, IXListViewListener {

	private int cityId;
	private ActionBar actionBar;
	private String actionBarTitle;
	private ProductDao productDao;
	private ProductListAdapter adapter;
	private List<Product> products;
	private XListView productListView;
	private SortListFragment fragment;
	private SortType sortType;
	private ImageView loadingImage;
	private AnimationDrawable animationDrawable;
	private LinearLayout loadingLayout;
	private LinearLayout productLayout;
	private int offset;
	private boolean isRefreshing;
	private boolean isLoadingMore;
	private boolean didLoadAll;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_list);
		getBundle();
		setActionBar();
		setListView();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		cityId = bundle.getInt(Const.CITYID);
		actionBarTitle = bundle.getString(Const.ACTIONBARTITLE);
		sortType = SortType.DEFAULT;
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
		productDao = new ProductDaoImpl();
		products = new ArrayList<Product>();

		loadingImage = (ImageView) findViewById(R.id.image_loading);
		loadingImage.setBackgroundResource(R.anim.progress_animation);
		animationDrawable = (AnimationDrawable) loadingImage.getBackground();

		loadingLayout = (LinearLayout) findViewById(R.id.layout_loading);
		productLayout = (LinearLayout) findViewById(R.id.layout_product_list);
		productLayout.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();

		offset = 0;
		isRefreshing = false;
		isLoadingMore = false;
		didLoadAll = false;

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		int height = (int) ((metric.widthPixels - 2 * getResources()
				.getDimension(R.dimen.activity_horizontal_margin)) * 3 / 5);

		adapter = new ProductListAdapter(this, products, height);
		productListView = (XListView) findViewById(R.id.product_list_view);
		productListView.setAdapter(adapter);
		productListView.setPullRefreshEnable(true);
		productListView.setPullLoadEnable(true);
		productListView.setXListViewListener(this);
		productListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int index = (int) id;
				Intent intent = new Intent();
				intent.setClass(ProductListActivity.this,
						ProductDetailActivity.class);
				intent.putExtra(Const.PRODUCTID, products.get(index)
						.getProductId());
				intent.putExtra(Const.GETMETHOD, Const.ONLINE);
				intent.putExtra(Const.ACTIONBARTITLE, Utility
						.splitChnEng(products.get(index).getProductName())[0]);
				startActivity(intent);
			}
		});
		ProductListAsyncTask asyncTask = new ProductListAsyncTask(true);
		asyncTask.execute(offset, Const.PRODUCT_PER_PAGE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_product_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case R.id.action_sort:
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			if (fragment == null) {
				fragment = new SortListFragment();
				Bundle args = new Bundle();
				args.putInt(Const.SORTTYPE, sortType.getIndex());
				fragment.setArguments(args);
				transaction.add(R.id.sort_fragment_container, fragment)
						.commit();
			} else {
				transaction.remove(fragment).commit();
				fragment = null;
			}
		case R.id.download:
			DownloadProductsAsyncTask asyncTask = new DownloadProductsAsyncTask();
			asyncTask.execute();
		}

		return super.onOptionsItemSelected(item);
	}

	private class ProductListAsyncTask extends
			AsyncTask<Integer, Integer, List<Product>> {

		private boolean isRefresh;

		public ProductListAsyncTask(boolean isRefresh) {
			this.isRefresh = isRefresh;
		}

		@Override
		protected List<Product> doInBackground(Integer... params) {
			List<Product> productListSegment = null;
			try {

				productListSegment = productDao.getProductList(cityId,
						params[0], params[1]);
				if (productListSegment == null) {
					didLoadAll = true;
					return null;
				} else if (productListSegment.size() < Const.PRODUCT_PER_PAGE) {
					didLoadAll = true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return productListSegment;
		}

		@Override
		protected void onPostExecute(List<Product> result) {

			if (isRefresh) {
				products.clear();
			}
			if (result != null) {
				products.addAll(result);
			}
			offset = products.size();
			adapter.notifyDataSetChanged();
			if (didLoadAll) {
				productListView.setPullLoadEnable(false);
			}
			if (isRefresh) {
				stopRefresh();
			} else {
				stopLoadMore();
			}
		}
	}

	private class DownloadProductsAsyncTask extends
			AsyncTask<Void, Integer, List<Download>> {

		@Override
		protected List<Download> doInBackground(Void... params) {
			try {
				List<Download> downloads = new ArrayList<Download>();
				List<Product> products = productDao.getProductList(cityId);
				for (Product product: products){
					Download download = new Download();
					download.setProductId(product.getProductId());
					download.setProductName(product.getProductName());
					if (product.getProductImages() != null && product.getProductImages().size() > 0){
						download.setProductImage(product.getProductImages().get(0));
					}
					downloads.add(download);
				}
				return downloads;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Download> result) {
			if (result != null){
				DatabaseUtility.StartDownloadProducts(ProductListActivity.this, result);
			}
			else {
				Toast.makeText(ProductListActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (fragment == null) {
				this.finish();
			}
			finishFragment();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void finishFragment() {
		if (fragment != null) {
			getSupportFragmentManager().beginTransaction().remove(fragment)
					.commit();
			fragment = null;
		}
	}

	@Override
	public void onSortTypeChanged(SortType sortType) {
		this.sortType = sortType;
		finishFragment();
	}

	@Override
	public void onRefresh() {
		if (!isRefreshing) {
			isRefreshing = true;
			didLoadAll = false;
			productListView.setPullLoadEnable(true);
			productLayout.setVisibility(View.INVISIBLE);
			loadingLayout.setVisibility(View.VISIBLE);
			animationDrawable.start();
			offset = 0;
			ProductListAsyncTask asyncTask = new ProductListAsyncTask(true);
			asyncTask.execute(offset, Const.PRODUCT_PER_PAGE);
		}
	}

	@Override
	public void onLoadMore() {
		if (!isLoadingMore && !didLoadAll) {
			isLoadingMore = true;
			ProductListAsyncTask asyncTask = new ProductListAsyncTask(false);
			asyncTask.execute(offset, Const.PRODUCT_PER_PAGE);
		}
	}

	private void stopRefresh() {
		isRefreshing = false;
		productListView.stopRefresh();
		productListView.setRefreshTime("刚刚");
		loadingLayout.setVisibility(View.INVISIBLE);
		productLayout.setVisibility(View.VISIBLE);
		animationDrawable.stop();
	}

	private void stopLoadMore() {
		isLoadingMore = false;
		productListView.stopLoadMore();
	}
}
