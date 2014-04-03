package com.ipang.wansha.dao;

import java.util.ArrayList;

import com.ipang.wansha.model.Product;

public interface ProductDao {

	public ArrayList<Product> getProductList(String cityId);
	
	public int getProductCount(String cityId);
	
	public Product getProductDetail(String productId);
	
}
