package com.ipang.wansha.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class CityListAdapter extends ArrayAdapter<City> {

	private Context context;
	private LayoutInflater mInflater;

	public final class ViewHolder {
		public ImageView cityImage;
		public TextView cityNameTextView;
		public TextView productNumberTextView;
	}

	public CityListAdapter(Context context, List<City> cities) {
		super(context, 0, cities);
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_listview_cities,
					null);

			holder.cityImage = (ImageView) convertView
					.findViewById(R.id.city_list_image);
			holder.cityNameTextView = (TextView) convertView
					.findViewById(R.id.city_list_name);
			holder.productNumberTextView = (TextView) convertView
					.findViewById(R.id.city_list_product_number);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;
		viewHolder.cityNameTextView.setText(getItem(position).getCityName());
		viewHolder.productNumberTextView.setText(getItem(position)
				.getProductCount()
				+ context.getResources().getString(R.string.products_number));

		// if (getItem(position).getCityImage() == null) {
		// viewHolder.cityImage.setImageResource(R.drawable.loading);
		// } else {
		ImageLoader.getInstance().displayImage(
				getItem(position).getCityImage(), viewHolder.cityImage,
				Const.options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						Animation anim = AnimationUtils.loadAnimation(context,
								R.anim.fade_in);
						viewHolder.cityImage.setAnimation(anim);
						anim.start();
					}
				});

		// }

		return convertView;
	}

}
