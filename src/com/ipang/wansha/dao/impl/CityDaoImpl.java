package com.ipang.wansha.dao.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Country;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;

public class CityDaoImpl implements CityDao {

	@Override
	public List<Country> getCountryList() throws InterruptedException,
			ExecutionException, JSONException, MalformedURLException {

		List<Country> countries = new ArrayList<Country>();

		URL url = new URL(Const.SERVERNAME + "/geodata/city/list.json");
		String result = HttpUtility.GetJson(url);

		JSONArray jsonArray = new JSONArray(result);
		
		HashMap<Integer, String> names = new HashMap<Integer, String>();
		HashMap<Integer, Integer> productCounts = new HashMap<Integer, Integer>();
		HashMap<Integer, String> images = new HashMap<Integer, String>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject city = jsonArray.getJSONObject(i);
			int countryId = city.getInt("countryId");
			String countryName = city.getString("countryName");
			int cityProductCount = city.getInt("productCount");
			String cityImage = city.getString("productImgPath");

			if (names.containsKey(countryId)) {
				productCounts.put(countryId, productCounts.get(countryId)
						+ cityProductCount);
			} else {
				names.put(countryId, countryName);

				if (!cityImage.toLowerCase().startsWith("http"))
					cityImage = Const.SERVERNAME + cityImage;
				images.put(countryId, cityImage);

				productCounts.put(countryId, cityProductCount);
			}
		}

		Iterator<Integer> iter = names.keySet().iterator();

		while (iter.hasNext()) {
			int countryId = iter.next();
			Country country = new Country();
			country.setCountryId(countryId);
			country.setCountryName(names.get(countryId));
			country.setCountryImage(images.get(countryId));
			country.setCountryProductCount(productCounts.get(countryId));
			countries.add(country);
		}

		return countries;
	}

	@Override
	public List<Country> getCountryList(int number)
			throws InterruptedException, ExecutionException, JSONException,
			MalformedURLException {

		List<Country> countries = new ArrayList<Country>();

		URL url = new URL(Const.SERVERNAME + "/genericdb/continent/list.json");
		String result = HttpUtility.GetJson(url);

		JSONArray jsonArray = new JSONArray(result);

		for (int i = 0; i < (jsonArray.length() < number ? jsonArray.length()
				: number); i++) {
			JSONObject continent = jsonArray.getJSONObject(i);
			String continentName = continent.getString("continentName");
			JSONArray countryList = continent.getJSONArray("countries");
			for (int j = 0; j < countryList.length(); j++) {
				JSONObject countryJson = countryList.getJSONObject(j);
				countries.add(createCountry(countryJson, continentName));
			}
		}

		return countries;
	}

	@Override
	public List<City> getCityList(int countryId) throws MalformedURLException,
			InterruptedException, ExecutionException, JSONException {

		List<City> cities = new ArrayList<City>();
		URL url = new URL(Const.SERVERNAME + "/geodata/city/list.json");

		String result = HttpUtility.GetJson(url);

		JSONArray jsonArray = new JSONArray(result);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject cityJson = jsonArray.getJSONObject(i);
			int country = cityJson.getInt("countryId");
			if (country == countryId)
				cities.add(createCity(cityJson));
		}
		return cities;
	}

	@Override
	public HashMap<String, List<City>> getCountryCityMap()
			throws MalformedURLException, JSONException {

		HashMap<String, List<City>> map = new HashMap<String, List<City>>();
		URL url = new URL(Const.SERVERNAME + "/geodata/city/list.json");

		String result = HttpUtility.GetJson(url);

		JSONArray jsonArray = new JSONArray(result);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject cityJson = jsonArray.getJSONObject(i);
			String countryName = cityJson.getString("countryName");
			if (map.containsKey(countryName)) {
				List<City> cities = map.get(countryName);
				cities.add(createCity(cityJson));
				map.put(countryName, cities);
			} else {
				List<City> cities = new ArrayList<City>();
				cities.add(createCity(cityJson));
				map.put(countryName, cities);
			}
		}

		return map;
	}

	private Country createCountry(JSONObject json, String continentName) {
		Country country = new Country();
		try {
			country.setCountryId(json.getInt("id"));
			country.setCountryName(json.getString("countryName"));
			country.setContinent(continentName);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		country.setCountryImage("");
		return country;
	}

	private City createCity(JSONObject json) {

		City city = new City();
		try {
			city.setCityId(json.getInt("cityId"));
			city.setCityName(json.getString("cityName"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		try {
			String path = json.getString("productImgPath");
			if (!path.toLowerCase().startsWith("http"))
				path = Const.SERVERNAME + path;
			city.setCityImage(path);
		} catch (JSONException e) {
			System.err.println("No City Image Found!");
			city.setCityImage("");
		}
		try {
			city.setProductCount(json.getInt("productCount"));
		} catch (JSONException e) {
			System.err.println("No Product Count Found!");
			city.setProductCount(0);
		}
		return city;
	}

	private HashMap<Integer, City> getCityInfoList()
			throws MalformedURLException, JSONException {

		HashMap<Integer, City> cities = new HashMap<Integer, City>();
		URL url = new URL(Const.SERVERNAME + "/geodata/city/list.json");

		String result = HttpUtility.GetJson(url);

		JSONArray jsonArray = new JSONArray(result);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject city = jsonArray.getJSONObject(i);
			cities.put(city.getInt("cityId"), createCity(city));
		}

		return cities;
	}

}
