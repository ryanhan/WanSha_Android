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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class RecommendListAdapter extends ArrayAdapter<Product> {

	private Context context;
	private LayoutInflater mInflater;
	private int height;

	public final class ViewHolder {
		public ImageView recommendProductImage;
		public TextView recommendNameTextView;
		public TextView recommendEnglishTextView;
		public ProgressBar imageLoadingProgress;
	}

	public RecommendListAdapter(Context context, List<Product> objects,
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
					R.layout.adapter_listview_recommend, null);

			holder.recommendProductImage = (ImageView) convertView
					.findViewById(R.id.recommend_list_image);
			FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT, height);
			holder.recommendProductImage.setLayoutParams(param);

			holder.recommendNameTextView = (TextView) convertView
					.findViewById(R.id.recommend_list_name);

			holder.recommendEnglishTextView = (TextView) convertView
					.findViewById(R.id.recommend_list_english);

			holder.imageLoadingProgress = (ProgressBar) convertView
					.findViewById(R.id.progress_recommend_image_loading);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;
		
		String[] names = Utility.splitChnEng(getItem(position).getProductName());

		viewHolder.recommendNameTextView.setText(names[0]);
		viewHolder.recommendEnglishTextView.setText(names[1]);

		if (getItem(position).getProductImages() == null
				|| getItem(position).getProductImages().size() == 0) {
			viewHolder.recommendProductImage
					.setImageResource(R.drawable.no_image);
		} else {

			ImageLoader.getInstance().displayImage(
					getItem(position).getProductImages().get(0),
					viewHolder.recommendProductImage, Const.options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(
									context, R.anim.fade_in);
							viewHolder.recommendProductImage.setAnimation(anim);
							anim.start();
							viewHolder.imageLoadingProgress
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							viewHolder.imageLoadingProgress
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							viewHolder.imageLoadingProgress
									.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							viewHolder.imageLoadingProgress
									.setVisibility(View.VISIBLE);
						}
					});

		}

		return convertView;
	}

}
