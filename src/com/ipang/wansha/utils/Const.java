package com.ipang.wansha.utils;

import com.ipang.wansha.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class Const {
	
	public static final String COUNTRYID = "countryid";
	public static final String COUNTRYNAME = "countryname";
	public static final String CITYID = "cityid";
	public static final String CITYNAME = "cityname";
	public static final String COUNTRY = "country";
	public static final String PRODUCTID = "productid";
	public static final String PRODUCTNAME = "productname";
	public static final String CURRENCY = "currency";
	public static final String APPINFO = "appinfo";
	public static final String USERINFO = "userinfo";
	public static final String USERID = "userid";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String HASLOGIN = "haslogin";
	public static final String FIRST = "first";
	public static final String JSESSIONID = "JSESSIONID";
	public static final String ADULTNUMBER = "adultnumber";
	public static final String CHILDNUMBER = "childnumber";
	public static final String TRAVELDATE = "traveldate";
	public static final String TOTALPRICE = "totalprice";

	public static final int LOGIN_REQUEST = 1;
	public static final int CHANGE_PASSWORD = 2;

	public static final String PACKAGENAME = "com.ipang.wansha";
	public static final String SERVERNAME = "http://www.iwansha.cn";
    public static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
	public static final String SORTTYPE = "sorttype";
	
	public static final int CONNECT_TIMEOUT = 3000;
	public static final int READ_TIMEOUT = 5000;
	
	public static final int PRODUCT_PER_PAGE = 10;
	public static final int CITY_PER_PAGE = 10;
	
	public static final String FIR_IM_URL = "http://fir.im/api/v2/app/version/";
	public static final String FIR_IM_ID = "54c88d12adc9a1a24a0000b2";
	public static final String DOWNLOAD_DIRECTORY = "Download";

	
	public static final DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.color.light_grey)
			.showImageForEmptyUri(R.drawable.no_image).cacheInMemory()
			.cacheOnDisc().build();

}
