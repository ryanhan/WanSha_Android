package com.ipang.wansha.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ipang.wansha.R;
import com.ipang.wansha.adapter.ProductDetailImagePagerAdapter;
import com.ipang.wansha.adapter.ReviewListShortAdapter;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.ReviewDao;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.dao.impl.ReviewDaoImpl;
import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.model.Review;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProductDetailActivity extends SherlockActivity {

	private ActionBar actionBar;
	private Product product;
	private City city;
	private ImageView[] dots;
	private ReviewListShortAdapter adapter;
	private ProductDao productDao;
	private ReviewDao reviewDao;
	private float avgScore;
	private SharedPreferences pref;
	private boolean hasLogin;
	private List<Review> reviews;
	private ListView reviewList;
	private String productId;
	private String productName;
	private ScrollView scrollView;
	private ImageLoader imageLoader;

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
		productId = bundle.getString(Const.PRODUCTID);
		productName = bundle.getString(Const.PRODUCTNAME);
		productDao = new ProductDaoImpl();
		pref = getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		hasLogin = pref.getBoolean(Const.HASLOGIN, false);
		imageLoader = ImageLoader.getInstance();
	}

	private void getProduct() {
		ProductAsyncTask productAsyncTask = new ProductAsyncTask();
		productAsyncTask.execute(productId);
	}

	private void getCity() {
		CityAsyncTask cityAsyncTask = new CityAsyncTask();
		cityAsyncTask.execute(productId);
	}

	private void setProductView() {
		scrollView = (ScrollView) findViewById(R.id.product_detail_scroll);
		initViewPager(product.getProductImages());
		setMainInfo();
		setOverviewAndHighlight();
		setReviews();
		Button bookButton = (Button) findViewById(R.id.book_now);
		bookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasLogin) {
					Intent intent = new Intent();
					intent.setClass(ProductDetailActivity.this,
							BookingActivity.class);
					intent.putExtra(Const.PRODUCTID, product.getProductId());
					intent.putExtra(Const.CURRENCY, product.getCurrency()
							.getIndex());
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(ProductDetailActivity.this,
							LoginActivity.class);
					startActivityForResult(intent, Const.LOGIN_REQUEST);
				}

			}
		});
	}

	private void setActionBar() {
		actionBar = this.getSupportActionBar();
		actionBar.setTitle(productName);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void initViewPager(final List<String> productImages) {

		ViewPager viewPager = (ViewPager) findViewById(R.id.product_detail_viewpager);
		setDotBar(productImages.size());

		viewPager.setAdapter(new ProductDetailImagePagerAdapter(this,
				productImages, imageLoader));
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						for (int i = 0; i < productImages.size(); i++) {
							dots[i].setBackgroundResource(i == position ? R.drawable.dot_selected
									: R.drawable.dot);
						}
					}
				});
		viewPager.setCurrentItem(0);

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
		TextView duration = (TextView) findViewById(R.id.duration);
		TextView rankcount = (TextView) findViewById(R.id.product_detail_ranking_count);
		TextView price = (TextView) findViewById(R.id.product_detail_from_price);

		ImageView[] rankImages = new ImageView[5];
		rankImages[0] = (ImageView) findViewById(R.id.product_detail_rank1);
		rankImages[1] = (ImageView) findViewById(R.id.product_detail_rank2);
		rankImages[2] = (ImageView) findViewById(R.id.product_detail_rank3);
		rankImages[3] = (ImageView) findViewById(R.id.product_detail_rank4);
		rankImages[4] = (ImageView) findViewById(R.id.product_detail_rank5);

		title.setText(product.getProductName());
		location.setText(city.getCityName() + ", " + city.getInCountry());

		int resID = getResources().getIdentifier(
				product.getTimeUnit().toString(), "string", Const.PACKAGENAME);
		String timeUnit = getResources().getString(resID);
		duration.setText(product.getDuration() + timeUnit);
		Utility.drawRankingStar(rankImages, avgScore);
		rankcount.setText("(" + product.getReviewCount() + ")");
		price.setText(product.getCurrency().getSymbol() + " "
				+ new DecimalFormat(".00").format(product.getPrice()));
	}

	private void setOverviewAndHighlight() {
		TextView overview = (TextView) findViewById(R.id.product_detail_overview);
		TextView highlight = (TextView) findViewById(R.id.product_detail_highlight);
		overview.setText(product.getOverview());
		highlight.setText(product.getHighlight());
	}

	private void setReviews() {
		setRankingDistribution();
		setReviewList();
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

		reviewRankCount.setText("(" + product.getReviewCount() + ")");
		Utility.drawRankingStar(reviewRankImages, avgScore);

		rankingScore
				.setText(new DecimalFormat(".00").format(avgScore) + " / 5");

		for (int i = 0; i < 5; i++) {
			bars[i].setMax(product.getReviewCount());
			bars[i].setProgress(product.getStarCount(i));
			if (product.getStarCount(i) > 0)
				bars[i].setProgressDrawable(getResources().getDrawable(
						R.drawable.progress_bg));
			else
				bars[i].setProgressDrawable(getResources().getDrawable(
						R.drawable.progress_empty_bg));

			starCounts[i].setText(product.getStarCount(i) + "");
		}

	}

	private void setReviewList() {
		reviewList = (ListView) findViewById(R.id.product_detail_review_list);
		reviewDao = new ReviewDaoImpl();
		reviews = new ArrayList<Review>();
		adapter = new ReviewListShortAdapter(this, reviews);
		reviewList.setAdapter(adapter);
		Utility.setListViewHeightBasedOnChildren(reviewList);
		ReviewListAsyncTask reviewAsyncTask = new ReviewListAsyncTask();
		reviewAsyncTask.execute(product.getProductId());

		TextView moreReview = (TextView) findViewById(R.id.more_review);
		moreReview.setClickable(true);
		moreReview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getSupportMenuInflater().inflate(R.menu.activity_product_detail, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ProductDetailActivity.this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Const.LOGIN_REQUEST
				&& resultCode == Activity.RESULT_OK) {
			hasLogin = data.getBooleanExtra(Const.HASLOGIN, false);
		}
	}

	private class ReviewListAsyncTask extends
			AsyncTask<String, Integer, List<Review>> {

		@Override
		protected List<Review> doInBackground(String... params) {
			try {
				reviews.clear();
				reviews.addAll(reviewDao.getReviewList(params[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return reviews;
		}

		@Override
		protected void onPostExecute(List<Review> result) {
			adapter.notifyDataSetChanged();
			Utility.setListViewHeightBasedOnChildren(reviewList);
			scrollView.scrollTo(0, 0);
		}
	}

	private class ProductAsyncTask extends AsyncTask<String, Integer, Product> {

		@Override
		protected Product doInBackground(String... params) {
			try {
				product = productDao.getProductDetail(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return product;
		}

		@Override
		protected void onPostExecute(Product result) {
			if (product.getReviewCount() != 0) {
				avgScore = (float) product.getReviewTotalRanking()
						/ product.getReviewCount();
			} else
				avgScore = 0;
			getCity();
		}
	}

	private class CityAsyncTask extends AsyncTask<String, Integer, City> {

		@Override
		protected City doInBackground(String... params) {
			try {
				city = productDao.getCityOfProduct(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return city;
		}

		@Override
		protected void onPostExecute(City result) {
			setProductView();
		}
	}

}
