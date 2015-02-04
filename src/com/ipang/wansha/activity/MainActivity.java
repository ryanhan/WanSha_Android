package com.ipang.wansha.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.SectionsPagerAdapter;
import com.ipang.wansha.customview.PagerSlidingTabStrip;
import com.ipang.wansha.utils.CheckUpdateAsyncTask;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class MainActivity extends FragmentActivity {

	private SharedPreferences pref;
	private PagerSlidingTabStrip tabs;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private ArrayList<String> tabTitle;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle toggle;
	private boolean drawerOpen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		setViews();
	}

	private void init() {
		pref = this.getSharedPreferences(Const.APPINFO, Context.MODE_PRIVATE);
		tabTitle = new ArrayList<String>();
		tabTitle.add(getString(R.string.title_section1));
		tabTitle.add(getString(R.string.title_section2));
		tabTitle.add(getString(R.string.title_section3));
	}

	private void setViews() {

		if (Utility.isWifiConnected(MainActivity.this)) {
			Toast.makeText(this, "Wifi已连接", Toast.LENGTH_SHORT).show();
			CheckUpdateAsyncTask checkUpdateAysncTask = new CheckUpdateAsyncTask(
					MainActivity.this, false);
			checkUpdateAysncTask.execute();
		}

		drawerOpen = false;

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setShouldExpand(true);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), tabTitle);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
		tabs.setViewPager(mViewPager);

		tabs.setIndicatorColor(getResources().getColor(R.color.wansha_blue));
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
				drawerOpen = false;
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				drawerOpen = true;
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (drawerOpen) {
				drawerLayout.closeDrawers();
			} else {
				Dialog alertDialog = new AlertDialog.Builder(MainActivity.this)
						.setTitle("退出提示")
						.setMessage("确定退出玩啥？")
						.setNegativeButton("取消", null)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										MainActivity.this.finish();
									}

								}).create();
				alertDialog.show();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
