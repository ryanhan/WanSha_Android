package com.ipang.wansha.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ipang.wansha.R;
import com.ipang.wansha.adapter.SectionsPagerAdapter;

public class MainActivity extends SherlockFragmentActivity {

	private TabWidget mTabWidget;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private LayoutInflater layoutInflater;
	private ArrayList<String> tabTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		setViews();
	}

	private void init() {
		layoutInflater = LayoutInflater.from(this);
		tabTitle = new ArrayList<String>();
		tabTitle.add(getString(R.string.title_section1));
		tabTitle.add(getString(R.string.title_section2));
		tabTitle.add(getString(R.string.title_section3));
	}

	private void setViews() {
		setViewPager();
		setTabHost();
	}

	private void setViewPager() {

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), tabTitle);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						mTabWidget.setCurrentTab(position);
					}
				});

	}

	private void setTabHost() {
		this.getSupportActionBar().hide();
		mTabWidget = (TabWidget) findViewById(R.id.tab_widget);
		mTabWidget.setStripEnabled(false);
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			final int index = i;
			mTabWidget.addView(getTabItemView(i));
			mTabWidget.getChildAt(i).setBackgroundResource(
					R.drawable.selector_tab_background);
			mTabWidget.getChildAt(i).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(index);
				}
			});
		}
		mTabWidget.setCurrentTab(0);
	}

	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.tab_item_main, null);

		TextView textView = (TextView) view.findViewById(R.id.tab_text);
		textView.getPaint().setFakeBoldText(true);
		textView.setText(tabTitle.get(index));

		return view;
	}
}
