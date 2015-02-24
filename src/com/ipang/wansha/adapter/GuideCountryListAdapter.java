package com.ipang.wansha.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.model.Country;
import com.ipang.wansha.utils.Utility;

public class GuideCountryListAdapter extends ArrayAdapter<Country> {

	private Context context;
	private LayoutInflater mInflater;

	public final class ViewHolder {
		public TextView countryNameText;
		public TextView countryEnglishText;
	}

	public GuideCountryListAdapter(Context context, List<Country> objects) {
		super(context, 0, objects);
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.adapter_listview_guide_countries, null);

			holder.countryNameText = (TextView) convertView
					.findViewById(R.id.guide_country_name_text);
			holder.countryEnglishText = (TextView) convertView.findViewById(R.id.guide_country_english_text);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String[] names = Utility.splitChnEng(getItem(position).getCountryName());

		holder.countryNameText.setText(names[0]);
		holder.countryEnglishText.setText(names[1]);

		return convertView;
	}

}
