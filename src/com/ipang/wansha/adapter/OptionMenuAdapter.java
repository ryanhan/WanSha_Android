package com.ipang.wansha.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ipang.wansha.R;

public class OptionMenuAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;

	public final class ViewHolder {
		public TextView optionText;
	}

	public OptionMenuAdapter(Context context, String[] objects) {
		super(context, 0, objects);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_option_menu, null);

			holder.optionText = (TextView) convertView
					.findViewById(R.id.text_option_menu);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.optionText.setText(getItem(position));

		return convertView;
	}

}
