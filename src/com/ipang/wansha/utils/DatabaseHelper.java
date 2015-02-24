package com.ipang.wansha.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;

	public static final String DATABASENAME = "wansha_db";
	public static final String OFFLINEGUIDE = "offline_guide";
	public static final String ID = "id";
	public static final String PRODUCTID = "product_id";
	public static final String PRODUCTNAME = "product_name";
	public static final String CITYID = "city_id";
	public static final String CITYNAME = "city_name";
	public static final String COUNTRYID = "country_id";
	public static final String COUNTRYNAME = "country_name";
	public static final String PRODUCTTYPE = "product_type";
	public static final String DETAIL = "detail";
	public static final String EXPENSEDESCR = "expense_descr";
	public static final String INSTRUCTION = "instruction";
	public static final String ORDERDESCR = "order_descr";
	public static final String BRIEF = "brief";
	public static final String LASTMODIFIED = "last_modified";

	public static final String LOCALIMAGE = "local_image";
	public static final String FILENAME = "file_name";
	public static final String IMAGEURL = "image_url";
	public static final String SEQ = "seq";

	public static final String DOWNLOADLIST = "download_list";
	public static final String PREVIEW = "preview";
	public static final String STATUS = "status";
	public static final String FILESIZE = "file_size";
	
	private Context context;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DatabaseHelper(Context context, String name) {
		this(context, name, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Toast.makeText(context, "create a database", Toast.LENGTH_SHORT).show();
		// create offline guide table
		db.execSQL("create table if not exists " + OFFLINEGUIDE + " (" + ID
				+ " integer primary key, " + PRODUCTID + " integer, "
				+ PRODUCTNAME + " text, " + CITYID + " integer, " + CITYNAME
				+ " text, " + COUNTRYID + " integer, " + COUNTRYNAME
				+ " text, " + PRODUCTTYPE + " integer, " + DETAIL + " text, "
				+ EXPENSEDESCR + " text, " + INSTRUCTION + " text, "
				+ ORDERDESCR + " text, " + BRIEF + " text, " + LASTMODIFIED
				+ " integer)");
		// create local image table
		db.execSQL("create table if not exists " + LOCALIMAGE + " (" + ID
				+ " integer primary key, " + PRODUCTID + " integer, "
				+ FILENAME + " text, " + IMAGEURL + " text, " + SEQ
				+ " integer)");

		// create download list table
		db.execSQL("create table if not exists " + DOWNLOADLIST + " (" + ID
				+ " integer primary key, " + PRODUCTID + " integer, "
				+ PRODUCTNAME + " text, " + PREVIEW + " text, " +  FILESIZE + " integer, " + STATUS + " integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
