package com.ipang.wansha.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ipang.wansha.R;

public class TimeSpinnerAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;

	public TimeSpinnerAdapter(Context context, List<String> Object) {
		super(context, 0, Object);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.adapter_spinner_time, null);
		}
		TextView time = (TextView) convertView
				.findViewById(R.id.spinner_time_text);
		time.setPadding(0, 0, 0, 0);
		time.setText(getItem(position));
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.adapter_spinner_time, null);
		}
		TextView time = (TextView) convertView
				.findViewById(R.id.spinner_time_text);
		time.setText(getItem(position));
		return convertView;
	}

}
