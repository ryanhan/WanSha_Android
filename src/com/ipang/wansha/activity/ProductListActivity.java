package com.ipang.wansha.activity;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ipang.wansha.R;
import com.ipang.wansha.adapter.ProductListAdapter;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;

public class ProductListActivity extends SherlockListActivity {

	private String cityId;
	private String cityName;
	private ActionBar actionBar;
	private ProductDao productDao;
	private ProductListAdapter adapter;
	private List<Product> products;

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
	}

	private void setActionBar() {
		actionBar = this.getSupportActionBar();
		actionBar.setTitle(cityName);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void setListView() {
		productDao = new ProductDaoImpl();
		try {
			products = productDao.getProductList(cityId);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		adapter = new ProductListAdapter(this, products);
		setListAdapter(adapter);
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
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Intent intent = new Intent();
		intent.setClass(this, ProductDetailActivity.class);
		intent.putExtra(Const.PRODUCTID, products.get(position).getProductId());
		startActivity(intent);

	}

}
