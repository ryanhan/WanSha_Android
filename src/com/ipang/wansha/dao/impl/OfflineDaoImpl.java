package com.ipang.wansha.dao.impl;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.DatabaseHelper;

public class OfflineDaoImpl implements OfflineDao {

	@Override
	public void createDatabase(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
		dbHelper.close();
	}

	@Override
	public void insertProducts(List<Product> products, Context context) {

		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
		for (Product product : products) {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.PRODUCTID, product.getProductId());
			values.put(DatabaseHelper.PRODUCTNAME, product.getProductName());
			values.put(DatabaseHelper.CITYID, product.getCityId());
			values.put(DatabaseHelper.COUNTRYID, product.getCountryId());
			values.put(DatabaseHelper.PRODUCTTYPE, product.getProductType());
			values.put(DatabaseHelper.DETAIL, product.getDetail());
			values.put(DatabaseHelper.EXPENSEDESCR, product.getExpenseDescr());
			values.put(DatabaseHelper.INSTRUCTION, product.getInstruction());
			values.put(DatabaseHelper.ORDERDESCR, product.getOrderDescr());
			values.put(DatabaseHelper.BRIEF, product.getBrief());
			values.put(DatabaseHelper.LASTMODIFIED, product.getLastModified());
			sqliteDatabase.insert(DatabaseHelper.OFFLINEGUIDE, null, values);

			List<String> imageList = product.getProductImages();
			for (int i = 0; i < imageList.size(); i++) {
				ContentValues imageValues = new ContentValues();
				imageValues.put(DatabaseHelper.PRODUCTID,
						product.getProductId());
				imageValues.put(DatabaseHelper.FILENAME, product.getProductId()
						+ "_" + i);
				imageValues.put(DatabaseHelper.IMAGEURL, imageList.get(i));
				imageValues.put(DatabaseHelper.SEQ, i);
				sqliteDatabase.insert(DatabaseHelper.LOCALIMAGE, null,
						imageValues);
			}
		}

	}
}
