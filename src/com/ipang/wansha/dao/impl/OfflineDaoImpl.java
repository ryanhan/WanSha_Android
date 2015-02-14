package com.ipang.wansha.dao.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.exception.HttpException;
import com.ipang.wansha.exception.OfflineException;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.DatabaseHelper;
import com.ipang.wansha.utils.HttpUtility;

public class OfflineDaoImpl implements OfflineDao {

	@Override
	public void createDatabase(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
		dbHelper.close();
	}

	@Override
	public boolean addProduct(Product product, Context context) {

		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
		sqliteDatabase.beginTransaction();
		try {
			insertProduct(sqliteDatabase, product, context);
			downloadImages(product, context);
			sqliteDatabase.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			deleteDir(product.getProductId(), context);
			return false;
		} finally {
			sqliteDatabase.endTransaction();
			dbHelper.close();
		}
	}

	@Override
	public int addProducts(List<Product> products, Context context) {
		int success = 0;

		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

		for (Product product : products) {
			sqliteDatabase.beginTransaction();
			try {
				insertProduct(sqliteDatabase, product, context);
				downloadImages(product, context);
				sqliteDatabase.setTransactionSuccessful();
				success++;
			} catch (Exception e) {
				e.printStackTrace();
				deleteDir(product.getProductId(), context);
			} finally {
				sqliteDatabase.endTransaction();
			}
		}
		dbHelper.close();
		return success;
	}

	private void insertProduct(SQLiteDatabase sqliteDatabase, Product product,
			Context context) {

		sqliteDatabase.delete(DatabaseHelper.OFFLINEGUIDE,
				DatabaseHelper.PRODUCTID + "=?",
				new String[] { product.getProductId() + "" });
		sqliteDatabase.delete(DatabaseHelper.LOCALIMAGE,
				DatabaseHelper.PRODUCTID + "=?",
				new String[] { product.getProductId() + "" });

		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.PRODUCTID, product.getProductId());
		values.put(DatabaseHelper.PRODUCTNAME, product.getProductName());
		values.put(DatabaseHelper.CITYID, product.getCityId());
		values.put(DatabaseHelper.CITYNAME, product.getCityName());
		values.put(DatabaseHelper.COUNTRYID, product.getCountryId());
		values.put(DatabaseHelper.COUNTRYNAME, product.getCountryName());
		values.put(DatabaseHelper.PRODUCTTYPE, product.getProductType());
		values.put(DatabaseHelper.DETAIL, product.getDetail());
		values.put(DatabaseHelper.EXPENSEDESCR, product.getExpenseDescr());
		values.put(DatabaseHelper.INSTRUCTION, product.getInstruction());
		values.put(DatabaseHelper.ORDERDESCR, product.getOrderDescr());
		values.put(DatabaseHelper.BRIEF, product.getBrief());
		values.put(DatabaseHelper.LASTMODIFIED, product.getLastModified());
		sqliteDatabase.insert(DatabaseHelper.OFFLINEGUIDE, null, values);

		List<String> imageList = product.getProductImages();
		String path = context.getExternalFilesDir(
				android.os.Environment.DIRECTORY_PICTURES).getPath();
		for (int i = 0; i < imageList.size(); i++) {
			ContentValues imageValues = new ContentValues();
			imageValues.put(DatabaseHelper.PRODUCTID, product.getProductId());
			imageValues.put(DatabaseHelper.FILENAME,
					path + "/" + product.getProductId() + "/" + i);
			imageValues.put(DatabaseHelper.IMAGEURL, imageList.get(i));
			imageValues.put(DatabaseHelper.SEQ, i);
			sqliteDatabase.insert(DatabaseHelper.LOCALIMAGE, null, imageValues);
		}
	}

	private File prepareDir(int productId, Context context) {
		File path = new File(
				context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
				productId + "");
		if (!path.exists()) {
			path.mkdir();
		} else if (path.isDirectory()) {
			File[] files = path.listFiles();
			for (File file : files) {
				file.delete();
				System.out.println("delete: " + file.getPath());
			}
		}
		return path;
	}

	private void deleteDir(int productId, Context context) {
		File path = new File(
				context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
				productId + "");
		if (path.exists() && path.isDirectory()) {
			File[] files = path.listFiles();
			for (File file : files) {
				file.delete();
				System.out.println("delete: " + file.getPath());
			}
			path.delete();
		}
	}

	private void downloadImages(Product product, Context context)
			throws Exception {
		File path = prepareDir(product.getProductId(), context);
		List<String> imageUrls = product.getProductImages();
		for (int i = 0; i < imageUrls.size(); i++) {
			URL url = new URL(imageUrls.get(i));
			HttpUtility.downloadImage(url, new File(path, i + ""), context);
		}
	}

	@Override
	public int getDownloadSize(Product product) {

		int sizeCount = 0;
		for (String imageUrl : product.getProductImages()) {
			try {
				sizeCount += HttpUtility.getFileSize(new URL(imageUrl));
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
		return sizeCount;
	}
}
