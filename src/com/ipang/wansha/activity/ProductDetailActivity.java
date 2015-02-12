package com.ipang.wansha.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.ProductDetailImagePagerAdapter;
import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.CityDaoImpl;
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
	private CityDao cityDao;
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
		int size = (int) getResources()
				.getDimension(R.dimen.viewpager_dot_size);

		for (int i = 0; i < imageNumber; i++) {
			ImageView imageView = new ImageView(this);

			LayoutParams param = new LayoutParams(size, size);
			param.rightMargin = size;
			imageView.setLayoutParams(param);

			dots[i] = imageView;
			dots[i].setBackgroundResource(i == 0 ? R.drawable.dot_selected
					: R.drawable.dot);

			group.addView(dots[i]);
		}
	}

	private void setMainInfo() {
		TextView title = (TextView) findViewById(R.id.product_detail_title);
		TextView english = (TextView) findViewById(R.id.product_detail_english);
		TextView location = (TextView) findViewById(R.id.location);
		TextView price = (TextView) findViewById(R.id.product_price);

		String names[] = Utility.splitChnEng(product.getProductName());
		title.setText(names[0]);
		if (names[1].equals("")) {
			english.setVisibility(View.GONE);
		} else {
			english.setText(names[1]);
		}
		location.setText(Utility.splitChnEng(cityName)[0] + ", "
				+ Utility.splitChnEng(countryName)[0]);

		if (product.getProductType() == Product.FREESIGHT) {
			price.setVisibility(View.GONE);
		} else {
			String priceText = product.getCurrency().getSymbol()
					+ (int) product.getLowestPrice() + " "
					+ getResources().getString(R.string.from);
			SpannableString span = new SpannableString(priceText);
			span.setSpan(new AbsoluteSizeSpan((int) getResources()
					.getDimension(R.dimen.product_detail_price_small_font)), 0,
					1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			span.setSpan(new AbsoluteSizeSpan((int) getResources()
					.getDimension(R.dimen.product_detail_price_small_font)),
					priceText.length() - 1, priceText.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			span.setSpan(
					new ForegroundColorSpan(getResources().getColor(
							R.color.dark_grey)), priceText.length() - 1,
					priceText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			span.setSpan(new StyleSpan(Typeface.NORMAL),
					priceText.length() - 1, priceText.length(),
					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			price.setText(span);
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
			SaveProductAsyncTask asyncTask = new SaveProductAsyncTask(product,
					ProductDetailActivity.this);
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
