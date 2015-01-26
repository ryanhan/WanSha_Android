package com.ipang.wansha.activity;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.SectionsPagerAdapter;
import com.ipang.wansha.customview.PagerSlidingTabStrip;

public class MainActivity extends FragmentActivity {

	private PagerSlidingTabStrip tabs;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private ArrayList<String> tabTitle;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		setViews();
	}

	private void init() {
		tabTitle = new ArrayList<String>();
		tabTitle.add(getString(R.string.title_section1));
		tabTitle.add(getString(R.string.title_section2));
		tabTitle.add(getString(R.string.title_section3));
	}

	private void setViews() {

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setShouldExpand(true);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), tabTitle);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
		tabs.setViewPager(mViewPager);

		tabs.setIndicatorColor(getResources().getColor(
				R.color.wansha_blue));
		tabs.setIndicatorHeight(10);
		tabs.setUnderlineHeight(0);
		tabs.setDividerColor(getResources().getColor(R.color.white));
		tabs.setTextSize(getResources().getDimensionPixelSize(
				R.dimen.tab_font_size));
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		toggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};

		drawerLayout.setDrawerListener(toggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (toggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		toggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		toggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

}
