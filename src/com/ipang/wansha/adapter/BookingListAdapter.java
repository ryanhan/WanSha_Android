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
import com.ipang.wansha.enums.Currency;
import com.ipang.wansha.model.Booking;
import com.ipang.wansha.utils.Const;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class BookingListAdapter extends ArrayAdapter<Booking> {

	private Context context;
	private LayoutInflater mInflater;
	private int height;

	public final class ViewHolder {
		public ImageView productImage;
		public TextView productNameTextView;
		public TextView orderNumberTextView;
		public TextView orderTimeTextView;
		public TextView travelDateTextView;
		public TextView travelMemberTextView;
		public TextView totalPriceTextView;
		public TextView orderStatus;
		public ProgressBar imageLoadingProgress;
	}

	public BookingListAdapter(Context context, List<Booking> objects, int height) {
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
			convertView = mInflater.inflate(R.layout.adapter_listview_bookings,
					null);

			holder.productImage = (ImageView) convertView
					.findViewById(R.id.product_image);

			FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
					height, height);
			holder.productImage.setLayoutParams(param);

			holder.productNameTextView = (TextView) convertView
					.findViewById(R.id.product_name);
			holder.orderNumberTextView = (TextView) convertView
					.findViewById(R.id.order_number);
			holder.travelDateTextView = (TextView) convertView
					.findViewById(R.id.travel_date);
			holder.orderTimeTextView = (TextView) convertView
					.findViewById(R.id.order_date);
			holder.travelMemberTextView = (TextView) convertView
					.findViewById(R.id.travel_member);
			holder.totalPriceTextView = (TextView) convertView
					.findViewById(R.id.total_price);
			holder.orderStatus = (TextView) convertView
					.findViewById(R.id.order_status);

			holder.imageLoadingProgress = (ProgressBar) convertView
					.findViewById(R.id.progress_image_loading);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;

		viewHolder.productNameTextView.setText(getItem(position)
				.getProductName());
		viewHolder.orderNumberTextView.setText(context
				.getString(R.string.order_number)
				+ ": "
				+ getItem(position).getBookingId());
		viewHolder.travelDateTextView
				.setText(getItem(position).getTravelDate());
		viewHolder.orderTimeTextView.setText(context
				.getString(R.string.order_time)
				+ ": "
				+ getItem(position).getOrderDate());
		viewHolder.travelMemberTextView.setText(context.getResources()
				.getString(R.string.adult)
				+ " "
				+ getItem(position).getAdultNumber()
				+ "/"
				+ context.getResources().getString(R.string.child)
				+ " "
				+ getItem(position).getChildNumber()
				+ "/"
				+ context.getResources().getString(R.string.infant)
				+ " "
				+ getItem(position).getInfantNumber());

		viewHolder.totalPriceTextView.setText(Currency.CHINESEYUAN.getSymbol()
				+ getItem(position).getTotal());
		viewHolder.orderStatus.setText(getItem(position).getOrderStatusText());

		if (getItem(position).getProductImage() == null) {
			viewHolder.productImage.setImageResource(R.drawable.no_image);
		} else {

			ImageLoader.getInstance().displayImage(
					getItem(position).getProductImage(),
					viewHolder.productImage, Const.options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							Animation anim = AnimationUtils.loadAnimation(
									context, R.anim.fade_in);
							viewHolder.productImage.setAnimation(anim);
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
