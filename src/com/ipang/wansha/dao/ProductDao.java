package com.ipang.wansha.dao;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.ipang.wansha.exception.ProductException;
import com.ipang.wansha.model.Product;

public interface ProductDao {

	public List<Product> getProductList(int cityId) throws ProductException;
	
	public List<Product> getProductList(int cityId, int offset, int number) throws ProductException;
	
	public Product getProductDetail(int productId) throws ProductException;

}
