package com.ipang.wansha.dao.impl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Country;
import com.ipang.wansha.model.Download;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.DatabaseHelper;
import com.ipang.wansha.utils.HttpUtility;

public class OfflineDaoImpl implements OfflineDao {

	private static final int ALL = 0;
	private static final int BYCOUNTRY = 1;
	private static final int BYCITY = 2;
	private static final int BYPRODUCT = 3;

	@Override
	public void createDatabase(Context context) {
		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
		dbHelper.close();
	}

	@Override
	public boolean addProduct(Context context, Product product) {

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
	public void rollback(Context context, int productId) {
		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
		sqliteDatabase.delete(DatabaseHelper.OFFLINEGUIDE,
				DatabaseHelper.PRODUCTID + "=?",
				new String[] { productId + "" });
		sqliteDatabase.delete(DatabaseHelper.LOCALIMAGE,
				DatabaseHelper.PRODUCTID + "=?",
				new String[] { productId + "" });
		deleteDir(productId, context);
		dbHelper.close();
	}

	@Override
	public List<Country> getAllCountries(Context context) {

		List<Country> countries = new ArrayList<Country>();

		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = sqliteDatabase.query(true, DatabaseHelper.OFFLINEGUIDE,
				new String[] { DatabaseHelper.COUNTRYID,
						DatabaseHelper.COUNTRYNAME }, null, null, null, null,
				DatabaseHelper.COUNTRYID, null);
		while (cursor.moveToNext()) {
			Country country = new Country();
			country.setCountryId(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.COUNTRYID)));
			country.setCountryName(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.COUNTRYNAME)));
			countries.add(country);
		}
		dbHelper.close();
		return countries;
	}

	@Override
	public List<City> getCitiesByCountry(Context context, int countryId) {

		List<City> cities = new ArrayList<City>();
		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = sqliteDatabase
				.query(true, DatabaseHelper.OFFLINEGUIDE, new String[] {
						DatabaseHelper.CITYID, DatabaseHelper.CITYNAME },
						DatabaseHelper.COUNTRYID + "=?",
						new String[] { countryId + "" }, null, null,
						DatabaseHelper.CITYID, null);
		while (cursor.moveToNext()) {
			City city = new City();
			city.setCityId(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.CITYID)));
			city.setCityName(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.CITYNAME)));
			cities.add(city);
		}
		dbHelper.close();
		return cities;
	}

	@Override
	public List<Product> getAllProducts(Context context) {
		return getProducts(context, 0, ALL);
	}

	@Override
	public List<Product> getProductsByCountry(Context context, int countryId) {
		return getProducts(context, countryId, BYCOUNTRY);
	}

	@Override
	public List<Product> getProductsByCity(Context context, int cityId) {
		return getProducts(context, cityId, BYCITY);
	}

	@Override
	public Product getProduct(Context context, int productId) {
		List<Product> productList = getProducts(context, productId, BYPRODUCT);
		if (productList != null && productList.size() == 1) {
			return productList.get(0);
		}
		return null;
	}

	@Override
	public void addProductToDownloadList(Context context,
			List<Download> downloads) {

		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

		for (Download download : downloads) {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.PRODUCTID, download.getProductId());
			values.put(DatabaseHelper.PRODUCTNAME, download.getProductName());
			values.put(DatabaseHelper.PREVIEW, download.getProductImage());
			values.put(DatabaseHelper.FILESIZE, 0);
			values.put(DatabaseHelper.STATUS, Download.NOTSTARTED);
			sqliteDatabase.insert(DatabaseHelper.DOWNLOADLIST, null, values);
		}
		dbHelper.close();
	}

	@Override
	public List<Download> getDownloadList(Context context) {

		List<Download> downloads = new ArrayList<Download>();
		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = sqliteDatabase.query(DatabaseHelper.DOWNLOADLIST, null,
				null, null, null, null, DatabaseHelper.ID);
		while (cursor.moveToNext()) {
			Download download = new Download();
			download.setDownloadId(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.ID)));
			download.setProductId(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.PRODUCTID)));
			download.setProductName(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.PRODUCTNAME)));
			download.setProductImage(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.PREVIEW)));
			download.setFileSize(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.FILESIZE)));
			download.setStatus(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.STATUS)));
			download.setDownloadedSize(0);
			downloads.add(download);
		}
		dbHelper.close();
		return downloads;
	}

	@Override
	public void removeDownload(Context context, int productId) {
		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
		sqliteDatabase.delete(DatabaseHelper.DOWNLOADLIST,
				DatabaseHelper.PRODUCTID + "=?",
				new String[] { productId + "" });
		dbHelper.close();
	}

	@Override
	public void updateDownloadStatus(Context context, int productId, int status){
		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.STATUS, status);
		sqliteDatabase.update(DatabaseHelper.DOWNLOADLIST, values, DatabaseHelper.PRODUCTID + "=?",
				new String[] { productId + "" });
		dbHelper.close();
	}

	private List<Product> getProducts(Context context, int id, int type) {

		List<Product> products = new ArrayList<Product>();
		DatabaseHelper dbHelper = new DatabaseHelper(context,
				DatabaseHelper.DATABASENAME);
		SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		if (type == BYCOUNTRY) {
			cursor = sqliteDatabase.query(DatabaseHelper.OFFLINEGUIDE, null,
					DatabaseHelper.COUNTRYID + "=?", new String[] { id + "" },
					null, null, DatabaseHelper.CITYID + ", "
							+ DatabaseHelper.PRODUCTID);
		} else if (type == BYCITY) {
			cursor = sqliteDatabase.query(DatabaseHelper.OFFLINEGUIDE, null,
					DatabaseHelper.CITYID + "=?", new String[] { id + "" },
					null, null, DatabaseHelper.PRODUCTID);
		} else if (type == BYPRODUCT) {
			cursor = sqliteDatabase.query(DatabaseHelper.OFFLINEGUIDE, null,
					DatabaseHelper.PRODUCTID + "=?", new String[] { id + "" },
					null, null, null);
		} else if (type == ALL) {
			cursor = sqliteDatabase.query(DatabaseHelper.OFFLINEGUIDE, null,
					null, null, null, null, DatabaseHelper.COUNTRYID + ", "
							+ DatabaseHelper.CITYID + ", "
							+ DatabaseHelper.PRODUCTID);
		}
		while (cursor.moveToNext()) {
			Product product = new Product();
			product.setProductId(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.PRODUCTID)));
			product.setProductName(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.PRODUCTNAME)));
			product.setCityId(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.CITYID)));
			product.setCityName(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.CITYNAME)));
			product.setCountryId(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.COUNTRYID)));
			product.setCountryName(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.COUNTRYNAME)));
			product.setProductType(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.PRODUCTTYPE)));
			product.setDetail(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.DETAIL)));
			product.setExpenseDescr(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.EXPENSEDESCR)));
			product.setInstruction(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.INSTRUCTION)));
			product.setOrderDescr(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.ORDERDESCR)));
			product.setBrief(cursor.getString(cursor
					.getColumnIndex(DatabaseHelper.BRIEF)));
			product.setLastModified(cursor.getInt(cursor
					.getColumnIndex(DatabaseHelper.LASTMODIFIED)));

			List<String> images = new ArrayList<String>();
			Cursor cursor2 = sqliteDatabase
					.query(DatabaseHelper.LOCALIMAGE, new String[] {
							DatabaseHelper.FILENAME, DatabaseHelper.SEQ },
							DatabaseHelper.PRODUCTID + "=?",
							new String[] { product.getProductId() + "" }, null,
							null, DatabaseHelper.SEQ);
			while (cursor2.moveToNext()) {
				images.add(cursor2.getString(cursor2
						.getColumnIndex(DatabaseHelper.FILENAME)));
			}
			System.out.println(images);

			product.setProductImages(images);
			products.add(product);
		}
		dbHelper.close();
		return products;
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
		String path = "file://"
				+ context.getExternalFilesDir(
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
