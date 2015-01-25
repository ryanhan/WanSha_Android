package com.ipang.wansha.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ipang.wansha.utils.Const;

public class LaunchImagePagerAdapter extends PagerAdapter {

	private String[] images;
	private Context context;
	private LayoutInflater mInflater;

	public LaunchImagePagerAdapter(Context context, String[] images) {
		this.context = context;
		this.images = images;
		this.mInflater = LayoutInflater.from(context);
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
		return images.length;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		ImageView image = new ImageView(context);
		
		int imgId = context.getResources().getIdentifier(images[position], "drawable", Const.PACKAGENAME); 
		image.setBackgroundResource(imgId);
		
		((ViewPager) container).addView(image, 0);
		return image;
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
