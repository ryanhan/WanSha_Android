package com.ipang.wansha.dao;

import java.util.List;

import android.content.Context;

import com.ipang.wansha.model.Product;

public interface OfflineDao {

	public void createDatabase(Context context);

	public boolean addProduct(Product products, Context context);

	public int addProducts(List<Product> products, Context context);

	public int getDownloadSize(Product product);

}
