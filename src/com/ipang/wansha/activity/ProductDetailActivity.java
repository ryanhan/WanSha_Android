package com.ipang.wansha.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ipang.wansha.R;
import com.ipang.wansha.adapter.ProductDetailImagePagerAdapter;
import com.ipang.wansha.adapter.ReviewListShortAdapter;
import com.ipang.wansha.dao.ReviewDao;
import com.ipang.wansha.dao.impl.ReviewDaoImpl;
import com.ipang.wansha.model.Language;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class ProductDetailActivity extends SherlockActivity implements
		OnPageChangeListener {

	private ActionBar actionBar;
	private Product product;
	private ViewPager viewPager;
	private ImageView[] dotImageViews;
	private int imageNumber;
	private ReviewListShortAdapter adapter;
	private ReviewDao reviewDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_detail);
		getBundle();
		setViews();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		product = (Product) bundle.getSerializable(Const.PRODUCTID);
	}

	private void setViews() {
		setActionBar();
		initViewPager();
		setMainInfo();
		setOverviewAndHighlight();
		setReviews();
		Button bookButton = (Button) findViewById(R.id.book_now);
		bookButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ProductDetailActivity.this, BookingActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(Const.PRODUCTID, product);
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});
	}

	private void setActionBar() {
		actionBar = this.getSupportActionBar();
		actionBar.setTitle(product.getProductName());
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.product_detail_viewpager);
		ViewGroup group = (ViewGroup) findViewById(R.id.product_detail_viewGroup);
		ArrayList<String> productImages = product.getProductImages();
		imageNumber = productImages.size();
		ArrayList<View> productImageView = new ArrayList<View>();

		for (int i = 0; i < imageNumber; i++) {
			ImageView img = new ImageView(this);
			int resID = getResources().getIdentifier(productImages.get(i),
					"drawable", Const.PACKAGENAME);
			img.setImageResource(resID);
			img.setScaleType(ScaleType.CENTER_CROP);
			productImageView.add(img);
		}

		dotImageViews = new ImageView[imageNumber];

		for (int i = 0; i < imageNumber; i++) {
			ImageView imageView = new ImageView(this);
			LayoutParams param = new LayoutParams(10, 10);
			param.rightMargin = 15;
			imageView.setLayoutParams(param);

			dotImageViews[i] = imageView;

			if (i == 0) {
				dotImageViews[i].setBackgroundResource(R.drawable.dot_selected);
			} else {
				dotImageViews[i].setBackgroundResource(R.drawable.dot);
			}

			group.addView(dotImageViews[i]);
		}

		viewPager.setAdapter(new ProductDetailImagePagerAdapter(
				productImageView));
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(imageNumber * 100);

	}

	private void setMainInfo() {
		TextView title = (TextView) findViewById(R.id.product_detail_title);
		TextView location = (TextView) findViewById(R.id.location);
		TextView language = (TextView) findViewById(R.id.language);
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
		location.setText(product.getCityName() + ", " + product.getCountryName());

		ArrayList<Language> lans = product.getSupportLanguage();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < lans.size(); i++) {
			int resID = getResources().getIdentifier(
					lans.get(i).toString(), "string", Const.PACKAGENAME);
			sb.append(getResources().getString(resID));
			if (i < lans.size() - 1) {
				sb.append(", ");
			}
		}
		language.setText(sb.toString());

		int resID = getResources().getIdentifier(
				product.getTimeUnit().toString(), "string", Const.PACKAGENAME);
		String timeUnit = getResources().getString(resID);
		duration.setText(product.getDuration() + timeUnit);
		Utility.drawRankingStar(rankImages, (float) product.getRatingScore()
				/ product.getRatingCount());
		rankcount.setText("(" + product.getRatingCount() + ")");
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

		float ranking = (float) product.getRatingScore()
				/ product.getRatingCount();
		reviewRankCount.setText("(" + product.getRatingCount() + ")");
		Utility.drawRankingStar(reviewRankImages, ranking);

		rankingScore.setText(new DecimalFormat(".00").format(ranking) + " / 5");

		for (int i = 0; i < 5; i++) {
			bars[i].setMax(product.getRatingCount());
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
		ListView reviewList = (ListView) findViewById(R.id.product_detail_review_list);
		reviewDao = new ReviewDaoImpl();
		adapter = new ReviewListShortAdapter(this,
				reviewDao.getReviewList(product.getProductId()));
		reviewList.setAdapter(adapter);
		Utility.setListViewHeightBasedOnChildren(reviewList);
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
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {

		int currentPage = arg0 % imageNumber;
		// Set round dot color for selected picture
		for (int i = 0; i < imageNumber; i++) {
			dotImageViews[i].setBackgroundResource(R.drawable.dot_selected);
			if (currentPage != i) {
				dotImageViews[i].setBackgroundResource(R.drawable.dot);
			}
		}

	}

}
