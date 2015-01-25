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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.model.Booking;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class BookingListAdapter extends ArrayAdapter<Booking> {

	private Context context;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public final class ViewHolder {
		public RelativeLayout layout;
		public ImageView productPreviewImage;
		public TextView bookingNameTextView;
		public TextView cityNameTextView;
		public TextView bookingInfoTextView;
		public TextView productOverviewTextView;
		public TextView priceTextView;

	}

	public BookingListAdapter(Context context, List<Booking> objects,
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
			convertView = mInflater.inflate(R.layout.adapter_listview_bookings,
					null);
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.booking_layout);
			holder.productPreviewImage = (ImageView) convertView
					.findViewById(R.id.booking_product_image);
			holder.bookingNameTextView = (TextView) convertView
					.findViewById(R.id.booking_product_name);
			holder.cityNameTextView = (TextView) convertView
					.findViewById(R.id.booking_city_name);
			holder.bookingInfoTextView = (TextView) convertView
					.findViewById(R.id.booking_info);
			holder.productOverviewTextView = (TextView) convertView
					.findViewById(R.id.booking_overview);
			holder.priceTextView = (TextView) convertView
					.findViewById(R.id.booking_price);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;

		imageLoader.displayImage(
				Const.SERVERNAME + "/rest/image/"
						+ getItem(position).getProductImage(),
				viewHolder.productPreviewImage, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						Animation anim = AnimationUtils.loadAnimation(context,
								R.anim.fade_in);
						viewHolder.productPreviewImage.setAnimation(anim);
						anim.start();
					}
				});

		viewHolder.bookingNameTextView.setText(getItem(position)
				.getProductName());
		viewHolder.cityNameTextView.setText(getItem(position).getCity() + ", "
				+ getItem(position).getCountry());
		String bookingInfo = context.getString(R.string.leave) + ": "
				+ Utility.FormatFullDate(getItem(position).getEventTime())
				+ ", " + getItem(position).getMemberNo()
				+ context.getString(R.string.person);
		viewHolder.bookingInfoTextView.setText(bookingInfo);
		viewHolder.productOverviewTextView.setText(getItem(position)
				.getProductOverview());
		viewHolder.priceTextView.setText("￥ "
				+ getItem(position).getTotalPrice());

		if (position % 2 == 1) {
			viewHolder.layout.setBackgroundColor(context.getResources()
					.getColor(R.color.listview_background));
		} else {
			viewHolder.layout.setBackgroundColor(context.getResources()
					.getColor(R.color.activity_background));
		}

		return convertView;
	}

}
