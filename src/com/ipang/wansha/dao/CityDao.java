package com.ipang.wansha.dao;

import java.util.HashMap;
import java.util.List;

import com.ipang.wansha.exception.CityException;
import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Country;

public interface CityDao {

	public List<Country> getCountryList() throws CityException;

	public List<City> getCityList(int countryId) throws CityException;

	public HashMap<String, List<City>> getCountryCityMap() throws CityException;

	public String[] getCountryAndCity(int countryId, int cityId)
			throws CityException;
}
