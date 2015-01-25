package com.ipang.wansha.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.enums.SortType;

public class SortTypeAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;
	private SortType sortType;
	private Context context;

	public SortTypeAdapter(Context context, String[] sortTypeList,
			SortType sortType) {
		super(context, 0, sortTypeList);
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.sortType = sortType;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_sort_text, null);
		}
		TextView sort = (TextView) convertView.findViewById(R.id.sort_type_text);
		sort.setText(getItem(position));
		if (position == sortType.getIndex()){
			sort.setTextColor(context.getResources().getColor(R.color.wansha_blue));
		}
		else {
			sort.setTextColor(context.getResources().getColor(R.color.dark_grey));
		}
		return convertView;
	}

	
}
