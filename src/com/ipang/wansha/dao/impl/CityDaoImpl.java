package com.ipang.wansha.dao.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.model.City;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.RestUtility;

public class CityDaoImpl implements CityDao {

	private ProductDao productDao;
	private RestUtility utility;

	public CityDaoImpl() {
		productDao = new ProductDaoImpl();
		utility = new RestUtility();
	}

	@Override
	public List<City> getRecommendCity() throws URISyntaxException,
			InterruptedException, ExecutionException, JSONException {
		return getCityList();
	}

	@Override
	public List<City> getCityList() throws URISyntaxException,
			InterruptedException, ExecutionException, JSONException {

		List<City> cities = new ArrayList<City>();
		URI uri = new URI(Const.SERVERNAME + "/rest/city");

		String result = utility.JsonGet(uri);

		JSONObject jsonObject = new JSONObject(result);
		try {
			JSONArray cityList = jsonObject.getJSONArray("city");
			for (int i = 0; i < cityList.length(); i++) {
				JSONObject cityJson = cityList.getJSONObject(i);
				cities.add(createCity(cityJson));
			}
		} catch (JSONException e) {
			JSONObject json = jsonObject.getJSONObject("city");
			cities.add(createCity(json));
		}
		return cities;
	}

	private City createCity(JSONObject json) {

		City city = new City();
		try {
			city.setCityId(json.getString("cityId"));
			city.setCityName(json.getString("cityName"));
			city.setInCountry(json.getString("inCountry"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		try {
			city.setPreviewImage(json.getString("cityImage"));
		} catch (JSONException e) {
		}
		try {
			city.setProductCount(productDao.getProductCount(json
					.getString("cityId")));
		} catch (Exception e) {
			e.printStackTrace();
			city.setProductCount(0);
		}

		return city;
	}

}
