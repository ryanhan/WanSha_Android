package com.ipang.wansha.dao;

import java.util.List;

import android.content.Context;

import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Country;
import com.ipang.wansha.model.Download;
import com.ipang.wansha.model.Product;

public interface OfflineDao {

	public void createDatabase(Context context);

	public void insertProduct(Context context, Product product);

	public void rollback(Context context, int productId);

	public int getDownloadSize(Product product);

	public List<Country> getAllCountries(Context context);

	public List<City> getCitiesByCountry(Context context, int countryId);

	public List<Product> getAllProducts(Context context);

	public List<Product> getProductsByCountry(Context context, int countryId);

	public List<Product> getProductsByCity(Context context, int cityId);

	public Product getProduct(Context context, int productId);

	public void addProductToDownloadList(Context context,
			List<Download> downloads);

	public List<Download> getDownloadList(Context context);

	public void removeDownload(Context context, int productId);

	public void updateDownloadStatus(Context context, int productId, int status);

	public void updateDownloadStatus(Context context, int status);

	public void updateGuideStatus(Context context, int productId, int status);

	public void startDownloadProducts(Context context, List<Download> downloads);

	public void startDownloadService(Context context, int productId);

	public void deleteOfflineCountry(Context context, int countryId);

	public void deleteOfflineCity(Context context, int cityId);

	public void deleteOfflineProduct(Context context, int productId);

}
