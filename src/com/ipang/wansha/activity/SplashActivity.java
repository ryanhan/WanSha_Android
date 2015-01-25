package com.ipang.wansha.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockActivity;
import com.ipang.wansha.R;
import com.ipang.wansha.adapter.LaunchImagePagerAdapter;
import com.ipang.wansha.utils.Const;

public class SplashActivity extends SherlockActivity {

	private final int SPLASH_DISPLAY_LENGHT = 1000;
	private SharedPreferences pref;
	private ImageView[] dots;
	private Button startButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = this.getSharedPreferences(Const.APPINFO, Context.MODE_PRIVATE);
		boolean first = pref.getBoolean(Const.FIRST, true);
		if (first) {

			setContentView(R.layout.activity_first_launch);
			ViewPager viewPager = (ViewPager) findViewById(R.id.pager_first_launch);
			startButton = (Button) findViewById(R.id.button_start);
			startButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Editor editor = pref.edit();
					editor.putBoolean(Const.FIRST, false);
					editor.commit();
					
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			final String[] launchImages = new String[] { "splash", "launch1",
					"launch2" };
			viewPager
					.setAdapter(new LaunchImagePagerAdapter(this, launchImages));
			viewPager.setCurrentItem(0);

			setDotBar(launchImages.length);

			viewPager
					.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
						@Override
						public void onPageSelected(int position) {
							for (int i = 0; i < launchImages.length; i++) {
								dots[i].setBackgroundResource(i == position ? R.drawable.dot
										: R.drawable.dot_selected_2);
							}
							startButton
									.setVisibility(position == launchImages.length - 1 ? View.VISIBLE
											: View.INVISIBLE);
						}
					});

		} else {

			setContentView(R.layout.activity_splash);
			ImageView image = (ImageView) findViewById(R.id.splash_image);
			image.setBackgroundResource(R.drawable.splash);

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}

			}, SPLASH_DISPLAY_LENGHT);
		}
	}

	private void setDotBar(int number) {
		ViewGroup group = (ViewGroup) findViewById(R.id.fragment_launch_viewGroup);
		dots = new ImageView[number];

		for (int i = 0; i < number; i++) {
			ImageView imageView = new ImageView(this);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(30,
					30);
			if (i != number - 1)
				param.rightMargin = 50;

			imageView.setLayoutParams(param);

			dots[i] = imageView;
			dots[i].setBackgroundResource(i == 0 ? R.drawable.dot
					: R.drawable.dot_selected_2);

			group.addView(dots[i]);
		}
	}

}
