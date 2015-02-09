package com.ipang.wansha.dao.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.exception.CityException;
import com.ipang.wansha.exception.HttpException;
import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Country;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;

public class CityDaoImpl implements CityDao {

	@Override
	public List<Country> getCountryList() throws CityException {

		List<Country> countries = new ArrayList<Country>();

		URL url = null;

		try {
			url = new URL(Const.SERVERNAME + "/geodata/city/list.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new CityException(CityException.UNKNOWN_ERROR);
		}

		String result = null;
		try {
			result = HttpUtility.GetJson(url);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new CityException(CityException.NETWORK_CONNECT_FAILED);
		}

		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(result);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new CityException(CityException.JSON_FORMAT_NOT_MATCH);
		}

		HashMap<Integer, String> names = new HashMap<Integer, String>();
		HashMap<Integer, Integer> productCounts = new HashMap<Integer, Integer>();
		HashMap<Integer, String> images = new HashMap<Integer, String>();

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
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
			} catch (JSONException e) {
				e.printStackTrace();
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
	public List<City> getCityList(int countryId) throws CityException {

		List<City> cities = new ArrayList<City>();

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/geodata/city/list.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new CityException(CityException.UNKNOWN_ERROR);
		}

		String result = null;
		try {
			result = HttpUtility.GetJson(url);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new CityException(CityException.NETWORK_CONNECT_FAILED);
		}

		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(result);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new CityException(CityException.JSON_FORMAT_NOT_MATCH);
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject cityJson = jsonArray.getJSONObject(i);
				int country = cityJson.getInt("countryId");
				if (country == countryId) {
					cities.add(createCity(cityJson));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return cities;
	}

	@Override
	public HashMap<String, List<City>> getCountryCityMap() throws CityException {

		HashMap<String, List<City>> map = new HashMap<String, List<City>>();

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/geodata/city/list.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new CityException(CityException.UNKNOWN_ERROR);
		}

		String result = null;
		try {
			result = HttpUtility.GetJson(url);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new CityException(CityException.NETWORK_CONNECT_FAILED);
		}

		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(result);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new CityException(CityException.JSON_FORMAT_NOT_MATCH);
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
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
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return map;
	}

	@Override
	public String[] getCountryAndCity(int countryId, int cityId)
			throws CityException {

		String[] res = new String[2];

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/genericdb/country/" + countryId
					+ ".json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new CityException(CityException.UNKNOWN_ERROR);
		}

		String result = null;
		try {
			result = HttpUtility.GetJson(url);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new CityException(CityException.NETWORK_CONNECT_FAILED);
		}

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new CityException(CityException.JSON_FORMAT_NOT_MATCH);
		}

		try {
			res[0] = jsonObject.getString("countryName");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new CityException(CityException.JSON_FORMAT_NOT_MATCH);
		}

		try {
			JSONArray cityArray = jsonObject.getJSONArray("cities");
			for (int i = 0; i < cityArray.length(); i++) {
				JSONObject cityJson = cityArray.getJSONObject(i);
				int id = cityJson.getInt("id");
				if (id == cityId) {
					res[1] = cityJson.getString("cityName");
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new CityException(CityException.JSON_FORMAT_NOT_MATCH);
		}

		return res;
	}

	private City createCity(JSONObject json) throws JSONException {

		City city = new City();
		city.setCityId(json.getInt("cityId"));
		city.setCityName(json.getString("cityName"));

		try {
			if (!json.isNull("productImgPath")) {
				String path = json.getString("productImgPath");
				if (!path.toLowerCase().startsWith("http"))
					path = Const.SERVERNAME + path;
				city.setCityImage(path);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			System.err.println("No City Image Found!");
		}

		try {
			if (!json.isNull("productCount")) {
				city.setProductCount(json.getInt("productCount"));
			} else {
				city.setProductCount(0);

			}
		} catch (JSONException e) {
			System.err.println("No Product Count Found!");
			city.setProductCount(0);
		}
		return city;
	}

}
