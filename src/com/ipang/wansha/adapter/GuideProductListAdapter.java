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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.CountryListAdapter.ViewHolder;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class GuideProductListAdapter extends ArrayAdapter<Product> {

	private Context context;
	private LayoutInflater mInflater;
	private int height;

	public final class ViewHolder {
		public ImageView productImage;
		public TextView productNameText;
		public TextView productEnglishText;
		public TextView productLocationText;
	}

	public GuideProductListAdapter(Context context, List<Product> objects,
			int height) {
		super(context, 0, objects);
		this.context = context;
		this.height = height;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.adapter_listview_guide_products, null);
			holder.productImage = (ImageView) convertView
					.findViewById(R.id.image_guide_product);

			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					height, height * 6 / 5);
			holder.productImage.setLayoutParams(param);
			holder.productNameText = (TextView) convertView
					.findViewById(R.id.text_guide_product_name);
			holder.productEnglishText = (TextView) convertView
					.findViewById(R.id.text_guide_product_english);
			holder.productLocationText = (TextView) convertView
					.findViewById(R.id.text_guide_product_location);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;

		ImageLoader.getInstance().displayImage(
				getItem(position).getProductImages().get(0),
				viewHolder.productImage, Const.options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						Animation anim = AnimationUtils.loadAnimation(context,
								R.anim.fade_in);
						viewHolder.productImage.setAnimation(anim);
						anim.start();
					}
				});
		String[] names = Utility
				.splitChnEng(getItem(position).getProductName());
		viewHolder.productNameText.setText(names[0]);
		viewHolder.productEnglishText.setText(names[1]);
		viewHolder.productLocationText.setText(Utility.splitChnEng(getItem(
				position).getCityName())[0]
				+ ", "
				+ Utility.splitChnEng(getItem(position).getCountryName())[0]);
		return convertView;
	}

}
