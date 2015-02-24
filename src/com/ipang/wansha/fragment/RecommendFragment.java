package com.ipang.wansha.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.activity.ProductDetailActivity;
import com.ipang.wansha.adapter.RecommendListAdapter;
import com.ipang.wansha.customview.XListView;
import com.ipang.wansha.customview.XListView.IXListViewListener;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.exception.ProductException;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class RecommendFragment extends Fragment implements IXListViewListener {

	private ProductDao productDao;
	private Context context;
	private View fragmentView;
	private List<Product> products;
	private RecommendListAdapter adapter;
	private XListView recommendListView;
	private ImageView loadingImage;
	private boolean hasRefreshed;
	private AnimationDrawable animationDrawable;
	private LinearLayout loadingLayout;
	private LinearLayout recommendLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		hasRefreshed = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_recommend, null);
		setViews();
		return fragmentView;
	}

	private void setViews() {
		productDao = new ProductDaoImpl();
		products = new ArrayList<Product>();

		loadingImage = (ImageView) fragmentView
				.findViewById(R.id.image_loading);
		loadingImage.setBackgroundResource(R.anim.progress_animation);
		animationDrawable = (AnimationDrawable) loadingImage.getBackground();

		loadingLayout = (LinearLayout) fragmentView
				.findViewById(R.id.layout_loading);
		recommendLayout = (LinearLayout) fragmentView
				.findViewById(R.id.layout_recommend);
		recommendLayout.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();

		DisplayMetrics metric = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		int height = (int) ((metric.widthPixels - 2 * getResources()
				.getDimension(R.dimen.activity_horizontal_margin)) * 3 / 5);

		adapter = new RecommendListAdapter(this.getActivity(), products, height);
		recommendListView = (XListView) fragmentView
				.findViewById(R.id.recommend_product_list);
		recommendListView.setAdapter(adapter);
		recommendListView.setPullRefreshEnable(true);
		recommendListView.setPullLoadEnable(false);
		recommendListView.setXListViewListener(this);

		recommendListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int index = (int) id;
				Intent intent = new Intent();
				intent.setClass(RecommendFragment.this.getActivity(),
						ProductDetailActivity.class);
				intent.putExtra(Const.PRODUCTID, products.get(index)
						.getProductId());
				intent.putExtra(Const.GETMETHOD, Const.ONLINE);
				intent.putExtra(Const.ACTIONBARTITLE, Utility
						.splitChnEng(products.get(index).getProductName())[0]);

				startActivity(intent);
			}
		});

		RecommendProductsAsyncTask recommendProductsAsyncTask = new RecommendProductsAsyncTask();
		recommendProductsAsyncTask.execute();
	}

	private void addHeaderButton() {
		hasRefreshed = true;
		LinearLayout layout = new LinearLayout(this.getActivity());
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(layoutParams);

		TextView titleText = new TextView(this.getActivity());
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		textParams.leftMargin = (int) this.getResources().getDimension(
				R.dimen.activity_horizontal_margin);
		titleText.setLayoutParams(textParams);
		titleText.setText(getResources().getString(R.string.wansha_recommend));
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
				.getDimension(R.dimen.wansha_recommend_text_size));

		layout.addView(titleText);
		recommendListView.addHeaderView(layout);
	}

	private class RecommendProductsAsyncTask extends
			AsyncTask<Void, Integer, List<Product>> {

		@Override
		protected List<Product> doInBackground(Void... params) {
			List<Product> tempCountryList = null;

			try {
				tempCountryList = productDao.getRecommendProductList();
			} catch (ProductException e) {
				e.printStackTrace();
			}

			return tempCountryList;
		}

		@Override
		protected void onPostExecute(List<Product> result) {

			if (result != null) {
				products.clear();
				products.addAll(result);
				adapter.notifyDataSetChanged();
				if (!hasRefreshed) {
					addHeaderButton();
				}
			}
			stopRefresh();
		}

	}

	@Override
	public void onRefresh() {
		recommendLayout.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();
		RecommendProductsAsyncTask recommendProductsAsyncTask = new RecommendProductsAsyncTask();
		recommendProductsAsyncTask.execute();
	}

	@Override
	public void onLoadMore() {
	}

	private void stopRefresh() {
		recommendListView.stopRefresh();
		recommendListView.setRefreshTime("刚刚");
		loadingLayout.setVisibility(View.INVISIBLE);
		recommendLayout.setVisibility(View.VISIBLE);
		animationDrawable.stop();
	}

}