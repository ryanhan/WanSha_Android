package com.ipang.wansha.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ipang.wansha.R;
import com.ipang.wansha.utils.Const;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ProductDetailImagePagerAdapter extends PagerAdapter {

	private List<String> imageList;
	private Context context;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public ProductDetailImagePagerAdapter(Context context,
			List<String> imageList, ImageLoader imageLoader) {
		this.context = context;
		this.imageList = imageList;
		this.mInflater = LayoutInflater.from(context);
		this.imageLoader = imageLoader;
		this.options = Const.options;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public int getCount() {
		return imageList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View imageLayout = mInflater.inflate(R.layout.view_pager_product_image,
				container, false);
		final ImageView imageView = (ImageView) imageLayout
				.findViewById(R.id.product_detail_image);
		String imageUri = Const.SERVERNAME + "/rest/image/"
				+ imageList.get(position);

		imageLoader.displayImage(imageUri, imageView, options,
				new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						if (loadedImage != null) {
							Animation anim = AnimationUtils.loadAnimation(
									context, R.anim.fade_in);
							imageView.setAnimation(anim);
							anim.start();
						}
					}
				});
		container.addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

}
