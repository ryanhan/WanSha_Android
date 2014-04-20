package com.ipang.wansha.dao;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.ipang.wansha.model.City;

public interface CityDao {

	public List<City> getRecommendCity() throws URISyntaxException, InterruptedException, ExecutionException, JSONException;

	public List<City> getCityList() throws URISyntaxException, InterruptedException, ExecutionException, JSONException;
	
}
