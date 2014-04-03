package com.ipang.wansha.dao;

import java.util.ArrayList;

import com.ipang.wansha.model.City;

public interface CityDao {

	public ArrayList<City> getRecommendCity();

	public ArrayList<City> getCityList();
	
}
