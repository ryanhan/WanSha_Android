package com.ipang.wansha.utils;

import com.ipang.wansha.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class Const {
	public static final String CITYID = "cityid";
	public static final String CITYNAME = "cityname";
	public static final String COUNTRY = "country";
	public static final String PRODUCTID = "productid";
	public static final String PRODUCTNAME = "productname";
	public static final String CURRENCY = "currency";
	public static final String USERINFO = "userinfo";
	public static final String USERID = "userid";
	public static final String USERNAME = "username";
	public static final String TOKEN = "token";
	public static final String HASLOGIN = "haslogin";
	public static final int LOGIN_REQUEST = 1;
	public static final String PACKAGENAME = "com.ipang.wansha";
	public static final String SERVERNAME = "http://10.0.2.2:8080/WanShaServer";
    public static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
	public static final String SORTTYPE = "sorttype";
	
	public static final DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.missing)
			.showImageForEmptyUri(R.drawable.missing).cacheInMemory()
			.cacheOnDisc().build();


}
