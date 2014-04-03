package com.ipang.wansha.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ipang.wansha.fragment.CityFragment;
import com.ipang.wansha.fragment.MyFragment;
import com.ipang.wansha.fragment.SpecialFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	ArrayList<Fragment> mFragment;
	ArrayList<String> tabTitle;

	public SectionsPagerAdapter(FragmentManager fm, ArrayList<String> tabTitle) {
		super(fm);
		mFragment = new ArrayList<Fragment>();
		mFragment.add(new CityFragment());
		mFragment.add(new SpecialFragment());
		mFragment.add(new MyFragment());
		this.tabTitle = tabTitle;
	}

	@Override
	public Fragment getItem(int position) {
		return mFragment.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabTitle.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFragment.size();
	}
}
