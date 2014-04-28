package com.ipang.wansha.dao;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.model.TripHour;

public interface ProductDao {

	public List<Product> getProductList(String cityId) throws URISyntaxException, InterruptedException, ExecutionException, JSONException;
	
	public int getProductCount(String cityId) throws URISyntaxException, InterruptedException, ExecutionException;
	
	public Product getProductDetail(String productId) throws URISyntaxException, InterruptedException, ExecutionException, JSONException;

	public City getCityOfProduct(String productId) throws URISyntaxException, InterruptedException, ExecutionException, JSONException;

	public List<TripHour> getTripTimeInDate(String productId, String dateStr) throws URISyntaxException, InterruptedException, ExecutionException, JSONException;

	public boolean bookTrip(String userId, String productId, Date startDate, int memberNo) throws URISyntaxException, InterruptedException, ExecutionException;
}
