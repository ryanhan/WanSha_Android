package com.ipang.wansha.dao;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.ipang.wansha.model.Product;

public interface ProductDao {

	public List<Product> getProductList(String cityId) throws URISyntaxException, InterruptedException, ExecutionException, JSONException;
	
	public int getProductCount(String cityId) throws URISyntaxException, InterruptedException, ExecutionException;
	
	public Product getProductDetail(String productId) throws URISyntaxException, InterruptedException, ExecutionException, JSONException;
	
}
