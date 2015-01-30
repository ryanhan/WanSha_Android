package com.ipang.wansha.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class BookingActivity extends Activity {

	private ActionBar actionBar;
	private SharedPreferences pref;
	private boolean hasLogin;
	private Product product;
	private int productId;
	private ProductDao productDao;
	private int totalPrice;
	private int adultPrice;
	private int childPrice;
	private TextView priceText;
	
	private ScrollView scrollView;
	private ImageView loadingImage;
	private AnimationDrawable animationDrawable;
	private LinearLayout loadingLayout;
	private LinearLayout bookingLayout;
	private ProgressBar imageLoadingProgress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);
		getBundle();
		setActionBar();
		getProduct();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		productId = bundle.getInt(Const.PRODUCTID);
		pref = getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		hasLogin = pref.getBoolean(Const.HASLOGIN, false);
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.book_now));
		actionBar.setDisplayUseLogoEnabled(false);
	}

	private void setProductView() {
		
		final ImageView productImage = (ImageView) findViewById(R.id.booking_image);
		TextView productTitle = (TextView) findViewById(R.id.booking_product_title);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = (int) (metric.widthPixels * 2 / 5);
		int height = width * 4 / 5;
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
		productImage.setLayoutParams(params);
		
		imageLoadingProgress = (ProgressBar) findViewById(R.id.progress_image_loading);
		
		if (product.getProductImages() == null || product.getProductImages().size() == 0) {
			productImage.setImageResource(R.drawable.no_image);
		} else {
			ImageLoader.getInstance().displayImage(product.getProductImages().get(0), productImage,
					Const.options, new SimpleImageLoadingListener() {

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							if (loadedImage != null) {
								Animation anim = AnimationUtils.loadAnimation(
										BookingActivity.this, R.anim.fade_in);
								productImage.setAnimation(anim);
								anim.start();
								imageLoadingProgress.setVisibility(View.GONE);
							}
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							imageLoadingProgress.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							imageLoadingProgress.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							imageLoadingProgress.setVisibility(View.VISIBLE);
						}
					});
		}
		
		productTitle.setText(Utility.splitChnEng(product.getProductName())[0]);

		LinearLayout selectDateLayout = (LinearLayout) findViewById(R.id.layout_select_date);
		selectDateLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		TextView adultPriceText = (TextView) findViewById(R.id.per_adult_price);
		TextView adultOriginPriceText = (TextView) findViewById(R.id.per_adult_origin_price);
		TextView childPriceText = (TextView) findViewById(R.id.per_child_price);
		TextView childOriginPriceText = (TextView) findViewById(R.id.per_child_origin_price);

		adultOriginPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		childOriginPriceText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

		final EditText adultNumber = (EditText) findViewById(R.id.member_adult_text);
		final EditText childNumber = (EditText) findViewById(R.id.member_child_text);

		adultNumber.setOnFocusChangeListener(new NumberChangeListener());
		childNumber.setOnFocusChangeListener(new NumberChangeListener());

		adultNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				int number;
				try {
					number = Integer.parseInt(s.toString());
				} catch (Exception e) {
					number = 0;
				}
				adultPrice = number * 10000;
				totalPrice = adultPrice + childPrice;
				priceText.setText("￥" + totalPrice);
			}
		});

		childNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				int number;
				try {
					number = Integer.parseInt(s.toString());
				} catch (Exception e) {
					number = 0;
				}
				childPrice = number * 1000;
				totalPrice = adultPrice + childPrice;
				priceText.setText("￥" + totalPrice);
			}
		});

		TextView adultPlus = (TextView) findViewById(R.id.member_adult_plus);
		TextView adultMinus = (TextView) findViewById(R.id.member_adult_minus);
		TextView childPlus = (TextView) findViewById(R.id.member_child_plus);
		TextView childMinus = (TextView) findViewById(R.id.member_child_minus);

		adultPlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int number;
				try {
					number = Integer.parseInt(adultNumber.getText().toString());

				} catch (Exception e) {
					number = 0;
				}
				adultNumber.setText(number + 1 + "");
			}
		});

		adultMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					int number = Integer.parseInt(adultNumber.getText()
							.toString());
					if (number > 0) {
						adultNumber.setText(number - 1 + "");
					}
				} catch (Exception e) {
					adultNumber.setText("0");
				}
			}
		});

		childPlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int number;
				try {
					number = Integer.parseInt(childNumber.getText().toString());

				} catch (Exception e) {
					number = 0;
				}
				childNumber.setText(number + 1 + "");
			}
		});

		childMinus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					int number = Integer.parseInt(childNumber.getText()
							.toString());
					if (number > 0) {
						childNumber.setText(number - 1 + "");
					}
				} catch (Exception e) {
					childNumber.setText("0");
				}
			}
		});

		priceText = (TextView) findViewById(R.id.total_price);
		priceText.setText("￥" + totalPrice);
	}

	private void getProduct() {
		productDao = new ProductDaoImpl();

		loadingImage = (ImageView) findViewById(R.id.image_loading);
		loadingImage.setBackgroundResource(R.anim.progress_animation);
		animationDrawable = (AnimationDrawable) loadingImage.getBackground();

		loadingLayout = (LinearLayout) findViewById(R.id.layout_loading);
		bookingLayout = (LinearLayout) findViewById(R.id.layout_booking);
		bookingLayout.setVisibility(View.INVISIBLE);
		loadingLayout.setVisibility(View.VISIBLE);
		animationDrawable.start();
		
		ProductAsyncTask productAsyncTask = new ProductAsyncTask();
		productAsyncTask.execute(productId);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			BookingActivity.this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class NumberChangeListener implements OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus) {
				if (v instanceof EditText) {
					EditText text = (EditText) v;
					if (text.getText().toString() == null
							|| text.getText().toString().equals("")) {
						text.setText("0");
					}
				}
			}
		}
	}

	private class ProductAsyncTask extends AsyncTask<Integer, Integer, Product> {

		@Override
		protected Product doInBackground(Integer... params) {
			try {
				product = productDao.getProductDetail(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return product;
		}

		@Override
		protected void onPostExecute(Product result) {
			loadingLayout.setVisibility(View.INVISIBLE);
			bookingLayout.setVisibility(View.VISIBLE);
			animationDrawable.stop();
			setProductView();
		}
	}

}
