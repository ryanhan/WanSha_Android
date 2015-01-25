package com.ipang.wansha.dao;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.ipang.wansha.model.Product;

public interface ProductDao {

	public List<Product> getProductList(int cityId) throws MalformedURLException, InterruptedException, ExecutionException, JSONException;
	
	public Product getProductDetail(int productId) throws MalformedURLException, InterruptedException, ExecutionException, JSONException;

}
