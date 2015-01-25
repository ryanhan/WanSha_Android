package com.ipang.wansha.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Application;

import com.ipang.wansha.enums.Currency;
import com.ipang.wansha.enums.TimeUnit;
import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Product;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class UILApplication extends Application {

	public static ArrayList<City> cities;
	public static ArrayList<Product> products;

	
	@Override
	public void onCreate() {
		super.onCreate();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.threadPoolSize(3)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.memoryCacheSize(5 * 1024 * 1024) // 5 Mb
			.denyCacheImageMultipleSizesInMemory()
			.discCacheFileNameGenerator(new Md5FileNameGenerator())
			.build();
		ImageLoader.getInstance().init(config);
	}

}