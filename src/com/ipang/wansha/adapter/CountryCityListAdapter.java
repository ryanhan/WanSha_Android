package com.ipang.wansha.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Utility;

public class CountryCityListAdapter extends BaseExpandableListAdapter {

	private ArrayList<String> countries;
	private ArrayList<ArrayList<City>> cities;
	private Context context;
	private LayoutInflater mInflater;

	public final class ViewHolder {
		public TextView cityNameTextView;
		public TextView cityEngNameTextView;
		public TextView cityProductCountTextView;
	}

	public CountryCityListAdapter(ArrayList<String> countries,
			ArrayList<ArrayList<City>> cities, Context context) {
		this.countries = countries;
		this.cities = cities;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getGroupCount() {
		if (countries == null)
			return 0;
		return countries.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (cities == null || cities.get(groupPosition) == null)
			return 0;
		return cities.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (countries == null)
			return null;
		return countries.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		if (cities == null || cities.get(groupPosition) == null)
			return null;
		return cities.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		LayoutParams param = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		TextView countryText = new TextView(context);
		countryText.setLayoutParams(param);
		countryText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		countryText.setPadding(50, 20, 0, 20);
		countryText.setText(countries.get(groupPosition));
		countryText.setTextColor(context.getResources().getColor(R.color.wansha_blue));
		countryText.setTextSize(16);
		return countryText;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.adapter_country_city_child, null);
			holder.cityNameTextView = (TextView) convertView
					.findViewById(R.id.city_name);
			holder.cityEngNameTextView = (TextView) convertView
					.findViewById(R.id.city_eng_name);
			holder.cityProductCountTextView = (TextView) convertView
					.findViewById(R.id.city_product_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String[] cityName = Utility.splitChnEng(cities.get(groupPosition)
				.get(childPosition).getCityName());

		try {
			holder.cityNameTextView.setText(cityName[0]);
		} catch (Exception e) {
			holder.cityNameTextView.setText("");
		}
		try {
			holder.cityEngNameTextView.setText(cityName[1]);
		} catch (Exception e) {
			holder.cityEngNameTextView.setText("");
		}
		holder.cityProductCountTextView.setText(cities.get(groupPosition)
				.get(childPosition).getProductCount()
				+ "");

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
