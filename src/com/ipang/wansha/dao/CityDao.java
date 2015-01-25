package com.ipang.wansha.dao;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Country;

public interface CityDao {

	public List<Country> getCountryList() throws MalformedURLException, InterruptedException, ExecutionException, JSONException;
	
	public List<Country> getCountryList(int number) throws MalformedURLException, InterruptedException, ExecutionException, JSONException;
	
	public List<City> getCityList(int countryId) throws MalformedURLException, InterruptedException, ExecutionException, JSONException;
	
	public HashMap<String, List<City>> getCountryCityMap() throws MalformedURLException, JSONException;
	
}
