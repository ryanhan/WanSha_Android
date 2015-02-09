package com.ipang.wansha.activity;

import java.text.DecimalFormat;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.ProductDetailImagePagerAdapter;
import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.CityDaoImpl;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.exception.CityException;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.SaveProductAsyncTask;
import com.ipang.wansha.utils.Utility;

public class ProductDetailActivity extends Activity {

	private ActionBar actionBar;
	private String actionBarTitle;
	private Product product;
	private ImageView[] dots;
	private ProductDao productDao;
	private OfflineDao offlineDao;
	private CityDao cityDao;
	private float avgScore;
	private SharedPreferences pref;
	private boolean hasLogin;
	private int productId;
	private String cityName;
	private String countryName;
	private ImageView loadingImage;
	private AnimationDrawable animationDrawable;
	private LinearLayout loadingLayout;
	private LinearLayout productLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
		getBundle();
		setActionBar();
		getProduct();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		productId = bundle.getInt(Const.PRODUCTID);
		actionBarTitle = bundle.getString(Const.ACTIONBARTITLE);
		pref = getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		hasLogin = pref.getBoolean(Const.HASLOGIN, false);
	}

	private void getProduct() {
		productDao = new ProductDaoImpl();
		cityDao = new CityDaoImpl();
		offlineDao = new OfflineDaoImpl();

		loadingImage = (ImageView) findViewById(R.id.image_loading);
		loadingImage.setBackgroundResource(R.anim.progress_animation);
		animationDrawable = (AnimationDrawable) loadingImage.getBackground();

		loadingLayout = (LinearLayout) findViewById(R.id.layout_loading);
		productLayout = (LinearLayout) findViewById(R.id.layout_product_detail);
		productLayout.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();

		ProductAsyncTask productAsyncTask = new ProductAsyncTask();
		productAsyncTask.execute(productId);
	}

	private void setProductView() {
		if (product == null) {
			ProductAsyncTask productAsyncTask = new ProductAsyncTask();
			productAsyncTask.execute(productId);
		}
		initViewPager(product.getProductImages());
		setMainInfo();
		setDetailContent();
		setReviews();
		Button bookButton = (Button) findViewById(R.id.book_now);
		if (product.getProductType() == 1) {
			bookButton.setVisibility(View.VISIBLE);
		}
		if (product.getProductType() == 2) {
			bookButton.setVisibility(View.GONE);
		}

		bookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasLogin) {
					Intent intent = new Intent();
					intent.setClass(ProductDetailActivity.this,
							BookingActivity.class);
					intent.putExtra(Const.PRODUCTID, product.getProductId());
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(ProductDetailActivity.this,
							LoginActivity.class);
					startActivityForResult(intent, Const.LOGIN_REQUEST);
					overridePendingTransition(R.anim.bottom_up, R.anim.fade_out);
				}

			}
		});
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(actionBarTitle);
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void initViewPager(List<String> productImages) {

		FrameLayout layout = (FrameLayout) findViewById(R.id.product_detail_image_layout);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int height = (int) (metric.widthPixels * 3 / 5);

		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, height);
		layout.setLayoutParams(param);

		ViewPager viewPager = (ViewPager) findViewById(R.id.product_detail_viewpager);

		final int imageCount;

		if (productImages == null || productImages.size() == 0) {
			imageCount = 1;
		} else {
			productImages.remove(0);
			imageCount = productImages.size();
		}

		setDotBar(imageCount);

		viewPager.setAdapter(new ProductDetailImagePagerAdapter(this,
				productImages));
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						for (int i = 0; i < imageCount; i++) {
							dots[i].setBackgroundResource(i == (position % imageCount) ? R.drawable.dot_selected
									: R.drawable.dot);
						}
					}
				});
		viewPager.setCurrentItem(imageCount * 100);

	}

	private void setDotBar(int imageNumber) {

		ViewGroup group = (ViewGroup) findViewById(R.id.product_detail_viewGroup);
		dots = new ImageView[imageNumber];

		for (int i = 0; i < imageNumber; i++) {
			ImageView imageView = new ImageView(this);
			LayoutParams param = new LayoutParams(10, 10);
			param.rightMargin = 15;
			imageView.setLayoutParams(param);

			dots[i] = imageView;
			dots[i].setBackgroundResource(i == 0 ? R.drawable.dot_selected
					: R.drawable.dot);

			group.addView(dots[i]);
		}
	}

	private void setMainInfo() {
		TextView title = (TextView) findViewById(R.id.product_detail_title);
		TextView location = (TextView) findViewById(R.id.location);
		TextView rankcount = (TextView) findViewById(R.id.product_detail_ranking_count);
		TextView from = (TextView) findViewById(R.id.product_detail_from);
		TextView price = (TextView) findViewById(R.id.product_detail_from_price);

		ImageView[] rankImages = new ImageView[5];
		rankImages[0] = (ImageView) findViewById(R.id.product_detail_rank1);
		rankImages[1] = (ImageView) findViewById(R.id.product_detail_rank2);
		rankImages[2] = (ImageView) findViewById(R.id.product_detail_rank3);
		rankImages[3] = (ImageView) findViewById(R.id.product_detail_rank4);
		rankImages[4] = (ImageView) findViewById(R.id.product_detail_rank5);

		title.setText(product.getProductName());
		location.setText(Utility.splitChnEng(cityName)[0] + ", "
				+ Utility.splitChnEng(countryName)[0]);

		// int resID = getResources().getIdentifier(
		// product.getTimeUnit().toString(), "string", Const.PACKAGENAME);
		// String timeUnit = getResources().getString(resID);
		// duration.setText(product.getDuration() + timeUnit);
		Utility.drawRankingStar(rankImages, avgScore);
		rankcount.setText("(0)");

		if (product.getLowestPrice() == 0) {
			from.setVisibility(View.INVISIBLE);
			price.setText(getResources().getString(R.string.free));
		} else {
			price.setText(product.getCurrency().getSymbol() + " "
					+ new DecimalFormat(".00").format(product.getLowestPrice()));
		}
	}

	private void setDetailContent() {

		TextView detailTitle = (TextView) findViewById(R.id.product_detail_detail_title);
		TextView detailContent = (TextView) findViewById(R.id.product_detail_detail);
		TextView expenseContent = (TextView) findViewById(R.id.product_detail_expenseDescr);
		TextView instructionTitle = (TextView) findViewById(R.id.product_detail_instruction_title);
		TextView instructionContent = (TextView) findViewById(R.id.product_detail_instruction);
		LinearLayout orderLayout = (LinearLayout) findViewById(R.id.product_detail_orderDescr_layout);
		TextView orderContent = (TextView) findViewById(R.id.product_detail_orderDescr);

		if (product.getDetail() != null)
			detailContent.setText(product.getDetail());

		if (product.getExpenseDescr() != null)
			expenseContent.setText(product.getExpenseDescr());

		if (product.getInstruction() != null)
			instructionContent.setText(product.getInstruction());

		if (product.getProductType() == 1) { // 收费
			detailTitle.setText(getResources().getString(
					R.string.detail_product));
			instructionTitle.setText(getResources().getString(
					R.string.instruction_product));
			if (product.getOrderDescr() != null)
				orderContent.setText(product.getOrderDescr());
		} else if (product.getProductType() == 2) { // 免费
			detailTitle
					.setText(getResources().getString(R.string.detail_sight));
			instructionTitle.setText(getResources().getString(
					R.string.instruction_sight));
			orderLayout.setVisibility(View.GONE);
		}

	}

	private void setReviews() {
		setRankingDistribution();
		// setReviewList();
	}

	private void setRankingDistribution() {
		TextView reviewRankCount = (TextView) findViewById(R.id.product_detail_reivew_ranking_count);
		TextView rankingScore = (TextView) findViewById(R.id.product_detail_ranking_score);

		ImageView[] reviewRankImages = new ImageView[5];
		reviewRankImages[0] = (ImageView) findViewById(R.id.product_detail_review_rank1);
		reviewRankImages[1] = (ImageView) findViewById(R.id.product_detail_review_rank2);
		reviewRankImages[2] = (ImageView) findViewById(R.id.product_detail_review_rank3);
		reviewRankImages[3] = (ImageView) findViewById(R.id.product_detail_review_rank4);
		reviewRankImages[4] = (ImageView) findViewById(R.id.product_detail_review_rank5);

		ProgressBar[] bars = new ProgressBar[5];
		bars[0] = (ProgressBar) findViewById(R.id.five_star_bar);
		bars[1] = (ProgressBar) findViewById(R.id.four_star_bar);
		bars[2] = (ProgressBar) findViewById(R.id.three_star_bar);
		bars[3] = (ProgressBar) findViewById(R.id.two_star_bar);
		bars[4] = (ProgressBar) findViewById(R.id.one_star_bar);

		TextView[] starCounts = new TextView[5];
		starCounts[0] = (TextView) findViewById(R.id.five_star_count);
		starCounts[1] = (TextView) findViewById(R.id.four_star_count);
		starCounts[2] = (TextView) findViewById(R.id.three_star_count);
		starCounts[3] = (TextView) findViewById(R.id.two_star_count);
		starCounts[4] = (TextView) findViewById(R.id.one_star_count);

		reviewRankCount.setText("(0)");
		Utility.drawRankingStar(reviewRankImages, avgScore);

		rankingScore
				.setText(new DecimalFormat(".00").format(avgScore) + " / 5");

		for (int i = 0; i < 5; i++) {
			bars[i].setMax(0);
			bars[i].setProgress(0);
			// if (product.getStarCount(i) > 0)
			// bars[i].setProgressDrawable(getResources().getDrawable(
			// R.drawable.progress_bg));
			// else
			bars[i].setProgressDrawable(getResources().getDrawable(
					R.drawable.progress_empty_bg));

			starCounts[i].setText("0");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.activity_product_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ProductDetailActivity.this.finish();
			break;
		case R.id.download:
			product.setCountryName(countryName);
			product.setCityName(cityName);
			SaveProductAsyncTask asyncTask = new SaveProductAsyncTask(product, ProductDetailActivity.this);
			asyncTask.execute();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Const.LOGIN_REQUEST
				&& resultCode == Activity.RESULT_OK) {
			hasLogin = data.getBooleanExtra(Const.HASLOGIN, false);
		}
	}

	private class ProductAsyncTask extends AsyncTask<Integer, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {
			try {
				product = productDao.getProductDetail(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			if (product == null) {
				return false;
			} else {
				try {
					String names[] = cityDao.getCountryAndCity(
							product.getCountryId(), product.getCityId());
					if (names == null) {
						return false;
					} else {
						countryName = names[0];
						cityName = names[1];
						return true;
					}
				} catch (CityException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				avgScore = 0;
				setProductView();
				productLayout.setVisibility(View.VISIBLE);
			} else {
				productLayout.setVisibility(View.INVISIBLE);
			}
			loadingLayout.setVisibility(View.INVISIBLE);
			animationDrawable.stop();
		}
	}
}
