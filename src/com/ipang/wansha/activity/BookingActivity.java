package com.ipang.wansha.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.ipang.wansha.R;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.model.Combo;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class BookingActivity extends FragmentActivity implements
		OnDateSetListener {

	private ActionBar actionBar;
	private SharedPreferences pref;
	private boolean hasLogin;
	private Product product;
	private int productId;
	private ProductDao productDao;
	private int totalPrice;
	private int adultPrice;
	private int childPrice;
	private int infantPrice;
	private String travelDate;
	private EditText adultNumber;
	private EditText childNumber;
	private EditText infantNumber;
	private TextView priceText;
	private TextView selectDateText;
	private List<Integer> adultCount;
	private List<Float> perAdult;
	private List<Integer> childCount;
	private List<Float> perChild;
	private List<Integer> infantCount;
	private List<Float> perInfant;

	private ImageView loadingImage;
	private AnimationDrawable animationDrawable;
	private LinearLayout loadingLayout;
	private LinearLayout bookingLayout;
	private ProgressBar imageLoadingProgress;
	private DatePickerDialog datePickerDialog;

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
		adultCount = new ArrayList<Integer>();
		perAdult = new ArrayList<Float>();
		childCount = new ArrayList<Integer>();
		perChild = new ArrayList<Float>();
		infantCount = new ArrayList<Integer>();
		perInfant = new ArrayList<Float>();
		Calendar calendar = Calendar.getInstance();
		datePickerDialog = DatePickerDialog.newInstance(this,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), false);
	}

	private void setActionBar() {
		actionBar = this.getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.book_now));
		actionBar.setDisplayUseLogoEnabled(false);
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

	private void setProductView() {
		
		final ImageView productImage = (ImageView) findViewById(R.id.booking_image);
		TextView productTitle = (TextView) findViewById(R.id.booking_product_title);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = (int) (metric.widthPixels * 2 / 5);
		int height = width * 4 / 5;

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width,
				height);
		productImage.setLayoutParams(params);

		imageLoadingProgress = (ProgressBar) findViewById(R.id.progress_image_loading);

		if (product.getProductImages() == null
				|| product.getProductImages().size() == 0) {
			productImage.setImageResource(R.drawable.no_image);
		} else {
			ImageLoader.getInstance().displayImage(
					product.getProductImages().get(0), productImage,
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

		selectDateText = (TextView) findViewById(R.id.select_date);

		LinearLayout selectDateLayout = (LinearLayout) findViewById(R.id.layout_select_date);
		selectDateLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				datePickerDialog.setVibrate(false);
				datePickerDialog.setYearRange(1985, 2028);
				datePickerDialog.setCloseOnSingleTapDay(false);
				datePickerDialog.show(
						BookingActivity.this.getSupportFragmentManager(),
						"datePickerDialog");
			}
		});

		List<Combo> combos = product.getCombos();

		for (Combo combo : combos) {
			switch (combo.getType()){
			case Const.CHILD: // 儿童
				if (childCount.size() == 0) {
					childCount.add(combo.getTo());
					perChild.add(combo.getPrice());
				} else {
					boolean flag = false;
					for (int i = 0; i < childCount.size(); i++) {
						if (childCount.get(i) > combo.getFrom()) {
							childCount.add(i, combo.getTo());
							perChild.add(i, combo.getPrice());
							flag = true;
							break;
						}
					}
					if (!flag) {
						childCount.add(combo.getTo());
						perChild.add(combo.getPrice());
					}
				}
				break;
			case Const.ADULT: // 成人
				if (adultCount.size() == 0) {
					adultCount.add(combo.getTo());
					perAdult.add(combo.getPrice());
				} else {
					boolean flag = false;
					for (int i = 0; i < adultCount.size(); i++) {
						if (adultCount.get(i) > combo.getFrom()) {
							adultCount.add(i, combo.getTo());
							perAdult.add(i, combo.getPrice());
							flag = true;
							break;
						}
					}
					if (!flag) {
						adultCount.add(combo.getTo());
						perAdult.add(combo.getPrice());
					}
				}
				break;
			case Const.INFANT: // 婴儿
				if (infantCount.size() == 0) {
					infantCount.add(combo.getTo());
					perInfant.add(combo.getPrice());
				} else {
					boolean flag = false;
					for (int i = 0; i < infantCount.size(); i++) {
						if (infantCount.get(i) > combo.getFrom()) {
							infantCount.add(i, combo.getTo());
							perInfant.add(i, combo.getPrice());
							flag = true;
							break;
						}
					}
					if (!flag) {
						infantCount.add(combo.getTo());
						perInfant.add(combo.getPrice());
					}
				}
				break;
			}
		}

		if (adultCount.size() > 0) {
			final TextView adultPriceText = (TextView) findViewById(R.id.per_adult_price);
			adultPriceText.setText(product.getCurrency().getSymbol()
					+ getUnitPrice(1, adultCount, perAdult));
			TextView adultOriginPriceText = (TextView) findViewById(R.id.per_adult_origin_price);
			adultOriginPriceText.setText(getResources().getString(
					R.string.origin_price)
					+ " "
					+ product.getCurrency().getSymbol()
					+ getUnitPrice(0, adultCount, perAdult));
			adultOriginPriceText.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG);
			adultNumber = (EditText) findViewById(R.id.member_adult_text);
			final TextView adultPlus = (TextView) findViewById(R.id.member_adult_plus);
			final TextView adultMinus = (TextView) findViewById(R.id.member_adult_minus);

			adultNumber.setOnFocusChangeListener(new NumberChangeListener());

			adultNumber.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					int number;
					try {
						number = Integer.parseInt(s.toString());
					} catch (Exception e) {
						number = 0;
					}
					int unit = 0;
					if (number > 0) {
						unit = getUnitPrice(number, adultCount, perAdult);
						if (unit == -1) {
							Toast.makeText(BookingActivity.this, BookingActivity.this.getResources().getString(R.string.exceed_max),
									Toast.LENGTH_SHORT).show();
							unit = 0;
							adultNumber.setText(""
									+ adultCount.get(adultCount.size() - 1));
							adultNumber.setSelection(adultNumber.getText()
									.length());
							adultPriceText.setText(product.getCurrency()
									.getSymbol()
									+ getUnitPrice(1, adultCount, perAdult));
						} else {
							adultPriceText.setText(product.getCurrency()
									.getSymbol() + unit);
						}
					} else {
						adultPriceText.setText(product.getCurrency()
								.getSymbol()
								+ getUnitPrice(1, adultCount, perAdult));
					}
					adultPrice = (int) (number * unit);
					totalPrice = adultPrice + childPrice + infantPrice;
					priceText.setText("￥" + totalPrice);
				}
			});

			adultPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int number;
					try {
						number = Integer.parseInt(adultNumber.getText()
								.toString());
					} catch (Exception e) {
						number = 0;
					}
					if (number < adultCount.get(adultCount.size() - 1)) {
						adultNumber.setText(number + 1 + "");
					}
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
		} else {
			RelativeLayout adultLayout = (RelativeLayout) findViewById(R.id.layout_adult);
			adultLayout.setVisibility(View.GONE);
			View adultDivider = (View) findViewById(R.id.divider_adult);
			adultDivider.setVisibility(View.GONE);
		}

		if (childCount.size() > 0) {
			final TextView childPriceText = (TextView) findViewById(R.id.per_child_price);
			childPriceText.setText(product.getCurrency().getSymbol()
					+ getUnitPrice(1, childCount, perChild));
			TextView childOriginPriceText = (TextView) findViewById(R.id.per_child_origin_price);
			childOriginPriceText.setText(getResources().getString(
					R.string.origin_price)
					+ " "
					+ product.getCurrency().getSymbol()
					+ getUnitPrice(0, childCount, perChild));
			childOriginPriceText.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG);
			childNumber = (EditText) findViewById(R.id.member_child_text);
			final TextView childPlus = (TextView) findViewById(R.id.member_child_plus);
			final TextView childMinus = (TextView) findViewById(R.id.member_child_minus);

			childNumber.setOnFocusChangeListener(new NumberChangeListener());

			childNumber.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					int number;
					try {
						number = Integer.parseInt(s.toString());
					} catch (Exception e) {
						number = 0;
					}
					int unit = 0;
					if (number > 0) {
						unit = getUnitPrice(number, childCount, perChild);
						if (unit == -1) {
							Toast.makeText(BookingActivity.this, BookingActivity.this.getResources().getString(R.string.exceed_max),
									Toast.LENGTH_SHORT).show();
							unit = 0;
							childNumber.setText(""
									+ adultCount.get(adultCount.size() - 1));
							childNumber.setSelection(childNumber.getText()
									.length());
							childPriceText.setText(product.getCurrency()
									.getSymbol()
									+ getUnitPrice(1, adultCount, perAdult));
						} else {
							childPriceText.setText(product.getCurrency()
									.getSymbol() + unit);
						}
					} else {
						childPriceText.setText(product.getCurrency()
								.getSymbol()
								+ getUnitPrice(1, adultCount, perAdult));
					}
					childPrice = (int) (number * unit);
					totalPrice = adultPrice + childPrice + infantPrice;
					priceText.setText("￥" + totalPrice);
				}
			});

			childPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int number;
					try {
						number = Integer.parseInt(childNumber.getText()
								.toString());

					} catch (Exception e) {
						number = 0;
					}
					if (number < childCount.get(childCount.size() - 1)) {
						childNumber.setText(number + 1 + "");
					}
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
		} else {
			RelativeLayout childLayout = (RelativeLayout) findViewById(R.id.layout_child);
			childLayout.setVisibility(View.GONE);
			View childDivider = (View) findViewById(R.id.divider_child);
			childDivider.setVisibility(View.GONE);
		}
		
		if (infantCount.size() > 0) {
			final TextView infantPriceText = (TextView) findViewById(R.id.per_infant_price);
			infantPriceText.setText(product.getCurrency().getSymbol()
					+ getUnitPrice(1, infantCount, perInfant));
			TextView infantOriginPriceText = (TextView) findViewById(R.id.per_infant_origin_price);
			infantOriginPriceText.setText(getResources().getString(
					R.string.origin_price)
					+ " "
					+ product.getCurrency().getSymbol()
					+ getUnitPrice(0, infantCount, perInfant));
			infantOriginPriceText.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG);
			infantNumber = (EditText) findViewById(R.id.member_infant_text);
			final TextView infantPlus = (TextView) findViewById(R.id.member_infant_plus);
			final TextView infantMinus = (TextView) findViewById(R.id.member_infant_minus);

			infantNumber.setOnFocusChangeListener(new NumberChangeListener());

			infantNumber.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					int number;
					try {
						number = Integer.parseInt(s.toString());
					} catch (Exception e) {
						number = 0;
					}
					int unit = 0;
					if (number > 0) {
						unit = getUnitPrice(number, infantCount, perInfant);
						if (unit == -1) {
							Toast.makeText(BookingActivity.this, BookingActivity.this.getResources().getString(R.string.exceed_max),
									Toast.LENGTH_SHORT).show();
							unit = 0;
							infantNumber.setText(""
									+ infantCount.get(infantCount.size() - 1));
							infantNumber.setSelection(infantNumber.getText()
									.length());
							infantPriceText.setText(product.getCurrency()
									.getSymbol()
									+ getUnitPrice(1, infantCount, perInfant));
						} else {
							infantPriceText.setText(product.getCurrency()
									.getSymbol() + unit);
						}
					} else {
						infantPriceText.setText(product.getCurrency()
								.getSymbol()
								+ getUnitPrice(1, infantCount, perInfant));
					}
					infantPrice = (int) (number * unit);
					totalPrice = adultPrice + childPrice + infantPrice;
					priceText.setText("￥" + totalPrice);
				}
			});

			infantPlus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int number;
					try {
						number = Integer.parseInt(infantNumber.getText()
								.toString());
					} catch (Exception e) {
						number = 0;
					}
					if (number < infantCount.get(infantCount.size() - 1)) {
						infantNumber.setText(number + 1 + "");
					}
				}
			});

			infantMinus.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						int number = Integer.parseInt(infantNumber.getText()
								.toString());
						if (number > 0) {
							infantNumber.setText(number - 1 + "");
						}
					} catch (Exception e) {
						infantNumber.setText("0");
					}
				}
			});
		} else {
			RelativeLayout infantLayout = (RelativeLayout) findViewById(R.id.layout_infant);
			infantLayout.setVisibility(View.GONE);
			View infantDivider = (View) findViewById(R.id.divider_adult);
			infantDivider.setVisibility(View.GONE);
		}
		

		priceText = (TextView) findViewById(R.id.total_price);
		priceText.setText("￥" + totalPrice);

		Button book = (Button) findViewById(R.id.book_now);
		book.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int adultNumberSumbit;
				int childNumberSumbit;
				int infantNumberSumbit;
				try {
					adultNumberSumbit = Integer.parseInt(adultNumber.getText()
							.toString());
				} catch (Exception e) {
					e.printStackTrace();
					adultNumberSumbit = 0;
				}
				try {
					childNumberSumbit = Integer.parseInt(childNumber.getText()
							.toString());
				} catch (Exception e) {
					e.printStackTrace();
					childNumberSumbit = 0;
				}
				try {
					infantNumberSumbit = Integer.parseInt(infantNumber.getText()
							.toString());
				} catch (Exception e) {
					e.printStackTrace();
					infantNumberSumbit = 0;
				}
				if (travelDate == null) {
					Toast.makeText(
							BookingActivity.this,
							getResources().getString(
									R.string.please_select_date),
							Toast.LENGTH_SHORT).show();
				} else if (adultNumberSumbit + childNumberSumbit == 0) {
					Toast.makeText(
							BookingActivity.this,
							getResources().getString(
									R.string.please_add_traveller),
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					intent.setClass(BookingActivity.this,
							BookingContactActivity.class);
					intent.putExtra(Const.PRODUCTID, product.getProductId());
					intent.putExtra(Const.ADULTNUMBER, adultNumberSumbit);
					intent.putExtra(Const.CHILDNUMBER, childNumberSumbit);
					intent.putExtra(Const.INFANTNUMBER, infantNumberSumbit);
					intent.putExtra(Const.TRAVELDATE, travelDate);
					intent.putExtra(Const.TOTALPRICE, totalPrice);
					intent.putExtra(Const.CURRENCY, product.getCurrency()
							.getSymbol());
					startActivity(intent);
				}
			}
		});

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
			if (hasFocus) {
				if (v instanceof EditText) {
					EditText text = (EditText) v;
					text.setText("");
				}
			}
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
			if (result != null) {
				loadingLayout.setVisibility(View.INVISIBLE);
				bookingLayout.setVisibility(View.VISIBLE);
				animationDrawable.stop();
				setProductView();
			}
		}
	}

	private int getUnitPrice(int number, List<Integer> countList,
			List<Float> priceList) {
		float unit = 0;
		boolean flag = false;
		for (int i = 0; i < countList.size(); i++) {
			if (countList.get(i) >= number) {
				unit = priceList.get(i);
				flag = true;
				break;
			}
		}
		if (!flag) {
			return -1;
		}
		return (int) unit;
	}

	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year,
			int month, int day) {
		month++;
		travelDate = year + "-" + (month < 10 ? "0" + month : month) + "-"
				+ (day < 10 ? "0" + day : day);
		selectDateText.setTextColor(getResources().getColor(R.color.black));
		selectDateText.setText(travelDate);
	}

}
