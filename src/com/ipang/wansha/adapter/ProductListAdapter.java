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
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ProductListAdapter extends ArrayAdapter<Product> {

	private Context context;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public final class ViewHolder {
		public ImageView productPreviewImage;
		public TextView productNameTextView;
		public TextView rankingCountTextView;
		public TextView fromPriceTextView;
		public ImageView[] rankingImage;
	}

	public ProductListAdapter(Context context, List<Product> objects,
			ImageLoader imageLoader) {
		super(context, 0, objects);
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.imageLoader = imageLoader;
		options = Const.options;
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

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;
		viewHolder.productNameTextView.setText(getItem(position)
				.getProductName());
		viewHolder.rankingCountTextView.setText("("
				+ getItem(position).getReviewCount() + ")");
		viewHolder.fromPriceTextView.setText(getItem(position).getCurrency()
				.getSymbol() + " " + getItem(position).getPrice());

		if (getItem(position).getProductImages() == null
				|| getItem(position).getProductImages().size() == 0) {
			viewHolder.productPreviewImage.setImageResource(R.drawable.missing);
		} else {

			imageLoader.displayImage(Const.SERVERNAME + "/rest/image/"
					+ getItem(position).getProductImages().get(0),
					viewHolder.productPreviewImage, options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(
									context, R.anim.fade_in);
							viewHolder.productPreviewImage.setAnimation(anim);
							anim.start();
						}
					});

		}

		float ranking = (float) getItem(position).getReviewTotalRanking()
				/ getItem(position).getReviewCount();

		Utility.drawRankingStar(viewHolder.rankingImage, ranking);

		return convertView;
	}

}
