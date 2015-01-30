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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ProductListAdapter extends ArrayAdapter<Product> {

	private Context context;
	private LayoutInflater mInflater;
	private int height;

	public final class ViewHolder {
		public ImageView productPreviewImage;
		public TextView productNameTextView;
		public TextView rankingCountTextView;
		public TextView fromPriceTextView;
		public ImageView[] rankingImage;
		public ProgressBar imageLoadingProgress;
	}

	public ProductListAdapter(Context context, List<Product> objects, int height) {
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
			convertView = mInflater.inflate(R.layout.adapter_listview_products,
					null);

			holder.productPreviewImage = (ImageView) convertView
					.findViewById(R.id.image_product_preview);

			FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT, height);
			holder.productPreviewImage.setLayoutParams(param);

			holder.productNameTextView = (TextView) convertView
					.findViewById(R.id.product_name_text);
			holder.rankingCountTextView = (TextView) convertView
					.findViewById(R.id.ranking_count);
			holder.fromPriceTextView = (TextView) convertView
					.findViewById(R.id.from_price);
			holder.rankingImage = new ImageView[5];
			holder.rankingImage[0] = (ImageView) convertView
					.findViewById(R.id.product_list_rank1);
			holder.rankingImage[1] = (ImageView) convertView
					.findViewById(R.id.product_list_rank2);
			holder.rankingImage[2] = (ImageView) convertView
					.findViewById(R.id.product_list_rank3);
			holder.rankingImage[3] = (ImageView) convertView
					.findViewById(R.id.product_list_rank4);
			holder.rankingImage[4] = (ImageView) convertView
					.findViewById(R.id.product_list_rank5);

			holder.imageLoadingProgress = (ProgressBar) convertView
					.findViewById(R.id.progress_product_image_loading);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;

		viewHolder.productNameTextView.setText(getItem(position)
				.getProductName());
		viewHolder.rankingCountTextView.setText("没有评分");

		View from = convertView.findViewById(R.id.from);
		if (getItem(position).getLowestPrice() == 0) {
			from.setVisibility(View.INVISIBLE);
			viewHolder.fromPriceTextView.setText(context.getResources()
					.getString(R.string.free));

		} else {
			from.setVisibility(View.VISIBLE);
			viewHolder.fromPriceTextView.setText(getItem(position)
					.getCurrency().getSymbol()
					+ " "
					+ getItem(position).getLowestPrice());
		}

		if (getItem(position).getProductImages() == null
				|| getItem(position).getProductImages().size() == 0) {
			viewHolder.productPreviewImage
					.setImageResource(R.drawable.no_image);
		} else {

			ImageLoader.getInstance().displayImage(
					getItem(position).getProductImages().get(0),
					viewHolder.productPreviewImage, Const.options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(
									context, R.anim.fade_in);
							viewHolder.productPreviewImage.setAnimation(anim);
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

		float ranking = 0;

		Utility.drawRankingStar(viewHolder.rankingImage, ranking);

		return convertView;
	}

}
