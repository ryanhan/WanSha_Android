package com.ipang.wansha.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ipang.wansha.R;
import com.ipang.wansha.adapter.ProductListAdapter;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.enums.SortType;
import com.ipang.wansha.fragment.SortListFragment;
import com.ipang.wansha.fragment.SortListFragment.OnSortTypeChangedListener;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProductListActivity extends SherlockFragmentActivity implements
		OnSortTypeChangedListener {

	private String cityId;
	private String cityName;
	private ActionBar actionBar;
	private ProductDao productDao;
	private ProductListAdapter adapter;
	private List<Product> products;
	private ListView productListView;
	private SortListFragment fragment;
	private SortType sortType;
	private ImageLoader imageLoader;

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
		cityId = bundle.getString(Const.CITYID);
		cityName = bundle.getString(Const.CITYNAME);
		sortType = SortType.DEFAULT;
		imageLoader = ImageLoader.getInstance();
	}

	private void setActionBar() {
		actionBar = this.getSupportActionBar();
		actionBar.setTitle(cityName);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void setListView() {
		productDao = new ProductDaoImpl();
		products = new ArrayList<Product>();
		adapter = new ProductListAdapter(this, products, imageLoader);
		productListView = (ListView) findViewById(R.id.product_list_view);
		productListView.setAdapter(adapter);
		productListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(ProductListActivity.this,
						ProductDetailActivity.class);
				intent.putExtra(Const.PRODUCTID, products.get(position)
						.getProductId());
				intent.putExtra(Const.PRODUCTNAME, products.get(position)
						.getProductName());
				startActivity(intent);
			}
		});
		ProductListAsyncTask asyncTask = new ProductListAsyncTask();
		asyncTask.execute(cityId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.activity_product_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ProductListActivity.this.finish();
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
		}

		return super.onOptionsItemSelected(item);
	}

	private class ProductListAsyncTask extends
			AsyncTask<String, Integer, List<Product>> {

		@Override
		protected List<Product> doInBackground(String... params) {
			try {
				products.clear();
				products.addAll(productDao.getProductList(params[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return products;
		}

		@Override
		protected void onPostExecute(List<Product> result) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (fragment == null){
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

}
