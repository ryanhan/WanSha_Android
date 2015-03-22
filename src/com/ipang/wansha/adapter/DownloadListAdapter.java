package com.ipang.wansha.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
		public ImageView deleteImage;
	}

	public interface DownloadListListener {
		public void delete(int position);
	}

	private DownloadListListener listener;

	public void setDownloadListListener(DownloadListListener listener) {
		this.listener = listener;
	}

	public DownloadListAdapter(Context context, List<Download> objects,
			int height) {
		super(context, 0, objects);
		this.context = context;
		this.height = height;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

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
			holder.productNameText = (TextView) convertView
					.findViewById(R.id.text_download_product_name);
			holder.productEnglishText = (TextView) convertView
					.findViewById(R.id.text_download_product_english);
			holder.downloadedText = (TextView) convertView
					.findViewById(R.id.text_downloaded);
			holder.downloadProgress = (NumberProgressBar) convertView
					.findViewById(R.id.progress_download_product);
			holder.deleteImage = (ImageView) convertView
					.findViewById(R.id.delete_download);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (holder.productImage.getTag() == null
				|| !holder.productImage.getTag().equals(
						getItem(position).getProductImage())) {
			ImageLoader.getInstance().displayImage(
					getItem(position).getProductImage(), holder.productImage,
					Const.options);
			holder.productImage.setTag(getItem(position).getProductImage());
		}

		String[] names = Utility
				.splitChnEng(getItem(position).getProductName());
		holder.productNameText.setText(names[0]);
		holder.productEnglishText.setText(names[1]);

		int size = getItem(position).getDownloadedSize();
		int totalSize = getItem(position).getFileSize();
		holder.downloadedText.setTextColor(context.getResources().getColor(
				R.color.black));

		final int status = getItem(position).getStatus();

		if (status == Download.NOTSTARTED) {
			holder.downloadedText.setText(context.getResources().getString(
					R.string.wait_download));
			holder.downloadProgress.setProgress(0);
		} else if (status == Download.STARTED) {
			if (totalSize == 0) {
				holder.downloadedText.setText(context.getResources().getString(
						R.string.is_downloading));
				holder.downloadProgress.setProgress(0);
			} else {
				DecimalFormat format = new DecimalFormat("0.00");
				String sizeToM = format.format((float) size / 1000 / 1000);
				String totalToM = format
						.format((float) totalSize / 1000 / 1000);
				holder.downloadedText
						.setText(sizeToM + "M / " + totalToM + "M");
				holder.downloadProgress.setProgress(size * 100 / totalSize);
			}
		} else if (status == Download.ISSTOPPING) {
			holder.downloadedText.setText(context.getResources().getString(
					R.string.is_stopping));
			holder.downloadedText.setTextColor(context.getResources().getColor(
					R.color.red));
			holder.downloadProgress.setProgress(0);
		} else if (status == Download.STOPPED) {
			holder.downloadedText.setText(context.getResources().getString(
					R.string.stop_download));
			holder.downloadedText.setTextColor(context.getResources().getColor(
					R.color.red));
			holder.downloadProgress.setProgress(0);
		} else if (status == Download.ERROR) {
			holder.downloadedText.setText(context.getResources().getString(
					R.string.download_error));
			holder.downloadedText.setTextColor(context.getResources().getColor(
					R.color.red));
			holder.downloadProgress.setProgress(0);
		}

		holder.deleteImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.delete(position);
			}
		});

		return convertView;
	}
}
