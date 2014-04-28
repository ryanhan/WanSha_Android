package com.ipang.wansha.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.ipang.wansha.R;
import com.ipang.wansha.adapter.TimeSpinnerAdapter;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.impl.ProductDaoImpl;
import com.ipang.wansha.enums.Currency;
import com.ipang.wansha.model.TripHour;
import com.ipang.wansha.utils.Const;

public class BookingActivity extends SherlockFragmentActivity implements
		CalendarDatePickerDialog.OnDateSetListener {

	private Button dateSelector;
	private ProductDao productDao;
	private String productId;
	private List<TripHour> tripHours;
	private Currency currency;
	private List<String> time;
	private Button minusButton;
	private Button plusButton;
	private EditText memberText;
	private TextView priceText;
	private int member;
	private int selectedButtonIndex;
	private float unitPrice;
	private SharedPreferences pref;
	private Spinner spinner;
	private TimeSpinnerAdapter spinnerAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);
		getBundle();
		setViews();
	}

	private void getBundle() {
		Bundle bundle = getIntent().getExtras();
		productId = bundle.getString(Const.PRODUCTID);
		currency = Currency.fromIndex(bundle.getInt(Const.CURRENCY));
		pref = getSharedPreferences(Const.USERINFO, Context.MODE_PRIVATE);
		productDao = new ProductDaoImpl();
		time = new ArrayList<String>();
		time.add(getResources().getString(R.string.first_select_date));
		unitPrice = 0;
		member = 1;
		selectedButtonIndex = -1;
	}

	private void setViews() {
		dateSelector = (Button) findViewById(R.id.select_date_button);
		dateSelector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getSupportFragmentManager();
				DateTime now = DateTime.now();
				CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
						.newInstance(BookingActivity.this, now.getYear(),
								now.getMonthOfYear() - 1, now.getDayOfMonth());
				calendarDatePickerDialog.show(fm, Const.FRAG_TAG_DATE_PICKER);
			}
		});

		Button bookingButton = (Button) findViewById(R.id.confirm_booking);
		bookingButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (time == null || time.size() == 0
						|| selectedButtonIndex == -1) {
					Toast.makeText(BookingActivity.this, "请选择时间",
							Toast.LENGTH_SHORT).show();
				} else if (memberText.getText() == null
						|| memberText.getText().toString().equals("")) {
					Toast.makeText(BookingActivity.this, "请选择人数",
							Toast.LENGTH_SHORT).show();
				} else {
					Date startTime = tripHours.get(selectedButtonIndex)
							.getStartTime();
					int memberNo = Integer.parseInt(memberText.getText()
							.toString());
					BookingConfirmAsyncTask bookingAsyncTask = new BookingConfirmAsyncTask(
							pref.getString(Const.USERID, null), productId,
							startTime, memberNo);
					bookingAsyncTask.execute();
				}
			}
		});

		spinner = (Spinner) findViewById(R.id.spinner_activity_time);
		spinnerAdapter = new TimeSpinnerAdapter(this, time);
		spinner.setAdapter(spinnerAdapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (tripHours != null) {
					unitPrice = tripHours.get(position).getPrice();
					priceText.setText(currency.getSymbol() + " " + unitPrice
							* member);
					selectedButtonIndex = position;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		minusButton = (Button) findViewById(R.id.member_minus);
		minusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (member > 1)
					member--;
				priceText.setText(currency.getSymbol() + " " + unitPrice
						* member);
				memberText.setText(member + "");
			}
		});

		plusButton = (Button) findViewById(R.id.member_plus);
		plusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				member++;
				priceText.setText(currency.getSymbol() + " " + unitPrice
						* member);
				memberText.setText(member + "");
			}
		});

		memberText = (EditText) findViewById(R.id.booking_member);
		memberText.setText(member + "");
		memberText.addTextChangedListener(new TextWatcher() {

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
				if (s.toString() == null || s.toString().equals("")) {
					member = 1;
				} else {
					member = Integer.parseInt(s.toString());
				}
				priceText.setText(currency.getSymbol() + " " + unitPrice
						* member);
			}
		});
		priceText = (TextView) findViewById(R.id.booking_price);
		priceText.setText(currency.getSymbol() + " 0");
	}

	@Override
	public void onDateSet(CalendarDatePickerDialog dialog, int year,
			int monthOfYear, int dayOfMonth) {
		String dateStr = year + "";
		if (monthOfYear + 1 < 10)
			dateStr += "-0" + (monthOfYear + 1);
		else
			dateStr += "-" + (monthOfYear + 1);
		if (dayOfMonth < 10)
			dateStr += "-0" + dayOfMonth;
		else
			dateStr += "-" + dayOfMonth;

		dateSelector.setText(dateStr);
		if (!spinner.isClickable()) {
			spinner.setClickable(true);
		}
		GetActivityAsyncTask getActivityAsyncTask = new GetActivityAsyncTask();
		getActivityAsyncTask.execute(dateStr);
	}

	private void refreshActivity() {

		selectedButtonIndex = -1;
		
		if (tripHours == null || tripHours.size() == 0) {
			time.clear();
			time.add(getResources().getString(R.string.time_not_found));
			System.out.println(time.get(0));
			spinnerAdapter.notifyDataSetChanged();
			spinner.setClickable(false);
			unitPrice = 0;
			priceText.setText(currency.getSymbol() + " 0");
		}

		else {
			DateFormat df = new SimpleDateFormat("HH:mm");
			time.clear();
			for (int i = 0; i < tripHours.size(); i++) {
				time.add(df.format(tripHours.get(i).getStartTime()));
			}
			spinnerAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		CalendarDatePickerDialog calendarDatePickerDialog = (CalendarDatePickerDialog) getSupportFragmentManager()
				.findFragmentByTag(Const.FRAG_TAG_DATE_PICKER);
		if (calendarDatePickerDialog != null) {
			calendarDatePickerDialog.setOnDateSetListener(this);
		}
	}

	private class GetActivityAsyncTask extends
			AsyncTask<String, Integer, List<TripHour>> {

		@Override
		protected List<TripHour> doInBackground(String... params) {
			try {
				tripHours = productDao.getTripTimeInDate(productId, params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				tripHours = null;
			}
			return tripHours;
		}

		@Override
		protected void onPostExecute(List<TripHour> result) {
			refreshActivity();
		}
	}

	private class BookingConfirmAsyncTask extends
			AsyncTask<String, Integer, Boolean> {

		private String userId;
		private String productId;
		private Date startTime;
		private int memberNo;

		public BookingConfirmAsyncTask(String userId, String productId,
				Date startTime, int memberNo) {
			this.userId = userId;
			this.productId = productId;
			this.startTime = startTime;
			this.memberNo = memberNo;
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				return productDao.bookTrip(userId, productId, startTime,
						memberNo);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(BookingActivity.this, "预订成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(BookingActivity.this, "预订失败", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
