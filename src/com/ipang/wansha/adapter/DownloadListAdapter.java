package com.ipang.wansha.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.ipang.wansha.R;
import com.ipang.wansha.model.Download;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class DownloadListAdapter extends ArrayAdapter<Download> {

	private Context context;
	private LayoutInflater mInflater;
	private int height;

	public final class ViewHolder {
		public ImageView productImage;
		public TextView productNameText;
		public TextView productEnglishText;
		public TextView downloadedText;
		public NumberProgressBar downloadProgress;
	}

	public DownloadListAdapter(Context context, List<Download> objects,
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
					R.layout.adapter_listview_downloads, null);

			holder.productImage = (ImageView) convertView
					.findViewById(R.id.image_download_product);

			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
					height, height * 6 / 5);
			holder.productImage.setLayoutParams(param);
			holder.productImage.setTag(getItem(position).getProductImage());
			holder.productNameText = (TextView) convertView
					.findViewById(R.id.text_download_product_name);
			holder.productEnglishText = (TextView) convertView
					.findViewById(R.id.text_download_product_english);
			holder.downloadedText = (TextView) convertView
					.findViewById(R.id.text_downloaded);
			holder.downloadProgress = (NumberProgressBar) convertView
					.findViewById(R.id.progress_download_product);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewHolder viewHolder = holder;

		String[] names = Utility
				.splitChnEng(getItem(position).getProductName());

		ImageLoader.getInstance().loadImage(
				getItem(position).getProductImage(), Const.options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						if (imageUri.equals(viewHolder.productImage.getTag())) {
							viewHolder.productImage.setImageBitmap(loadedImage);
						}
					}
				});

		viewHolder.productNameText.setText(names[0]);
		viewHolder.productEnglishText.setText(names[1]);

		int size = getItem(position).getDownloadedSize();
		int totalSize = getItem(position).getFileSize();
		if (getItem(position).getStatus() == Download.NOTSTARTED) {
			viewHolder.downloadedText.setText(context.getResources().getString(
					R.string.wait_download));
			viewHolder.downloadProgress.setProgress(0);
		} else if (getItem(position).getStatus() == Download.STARTED) {
			if (totalSize == 0) {
				viewHolder.downloadedText.setText(context.getResources().getString(
						R.string.is_downloading));
				viewHolder.downloadProgress.setProgress(0);
			} else {
				DecimalFormat format = new DecimalFormat("0.00");
				String sizeToM = format.format((float) size / 1000 / 1000);
				String totalToM = format
						.format((float) totalSize / 1000 / 1000);
				viewHolder.downloadedText.setText(sizeToM + "M / " + totalToM
						+ "M");
				viewHolder.downloadProgress.setProgress(size * 100 / totalSize);
			}
		}
		return convertView;
	}
}
