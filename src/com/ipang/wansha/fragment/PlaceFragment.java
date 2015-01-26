package com.ipang.wansha.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.ipang.wansha.R;
import com.ipang.wansha.activity.CityListActivity;
import com.ipang.wansha.activity.CountryCityListActivity;
import com.ipang.wansha.adapter.CountryListAdapter;
import com.ipang.wansha.customview.XListView;
import com.ipang.wansha.customview.XListView.IXListViewListener;
import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.impl.CityDaoImpl;
import com.ipang.wansha.model.Country;
import com.ipang.wansha.utils.Const;

public class PlaceFragment extends Fragment implements IXListViewListener {

	private CityDao cityDao;
	private Context context;
	private View fragmentView;
	private List<Country> countries;
	private CountryListAdapter adapter;
	private XListView countryListView;
	private boolean refresh;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		refresh = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_place, null);
		setViews();
		return fragmentView;
	}

	private void setViews() {
		cityDao = new CityDaoImpl();
		countries = new ArrayList<Country>();

		DisplayMetrics metric = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		int height = (int) ((metric.widthPixels - 2 * getResources()
				.getDimension(R.dimen.activity_horizontal_margin)) * 3 / 5);

		adapter = new CountryListAdapter(this.getActivity(), countries, height);
		countryListView = (XListView) fragmentView
				.findViewById(R.id.listview_place);
		countryListView.setAdapter(adapter);
		countryListView.setPullLoadEnable(false);
		countryListView.setXListViewListener(this);

		countryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent();
				intent.setClass(PlaceFragment.this.getActivity(),
						CityListActivity.class);
				intent.putExtra(Const.COUNTRYID, countries.get(position - 1)
						.getCountryId());
				intent.putExtra(Const.COUNTRYNAME, countries.get(position - 1)
						.getCountryName());
				startActivity(intent);
			}
		});

		CountryListAsyncTask countryListAsyncTask = new CountryListAsyncTask();
		countryListAsyncTask.execute();
	}

	private void addFooterButton() {
		Button button = new Button(this.getActivity());
		AbsListView.LayoutParams buttonParams = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.WRAP_CONTENT);
		button.setLayoutParams(buttonParams);
		button.setPadding(
				0,
				(int) getResources().getDimension(
						R.dimen.button_padding_vertical),
				0,
				(int) getResources().getDimension(
						R.dimen.button_padding_vertical));
		button.setText(getResources().getString(R.string.show_all_country));
		button.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.button_bg));
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PlaceFragment.this.getActivity(),
						CountryCityListActivity.class);
				startActivity(intent);
			}
		});

		countryListView.addFooterView(button);
	}

	private class CountryListAsyncTask extends
			AsyncTask<Void, Integer, List<Country>> {

		@Override
		protected List<Country> doInBackground(Void... params) {

			try {
				List<Country> tempCountryList = cityDao.getCountryList();
				countries.clear();
				countries.addAll(tempCountryList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return countries;
		}

		@Override
		protected void onPostExecute(List<Country> result) {
			adapter.notifyDataSetChanged();
			if (!refresh)
				addFooterButton();
			stopRefresh();
		}
	}

	@Override
	public void onRefresh() {
		refresh = true;
		CountryListAsyncTask countryListAsyncTask = new CountryListAsyncTask();
		countryListAsyncTask.execute();
	}

	@Override
	public void onLoadMore() {
	}

	private void stopRefresh() {
		countryListView.stopRefresh();
		countryListView.setRefreshTime("刚刚");
	}
}
