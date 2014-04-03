package com.ipang.wansha.dao.impl;

import java.util.ArrayList;

import com.ipang.wansha.dao.CityDao;
import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.model.City;

public class CityDaoImpl implements CityDao {

	ProductDao productDao;
	
	public CityDaoImpl(){
		productDao = new ProductDaoImpl();
	}
	
	
	@Override
	public ArrayList<City> getRecommendCity() {
		return getCityList();
	}

	@Override
	public ArrayList<City> getCityList() {
		ArrayList<City> cities = new ArrayList<City>();
		City rome = new City();
		rome.setCityName("罗马");
		rome.setInCountry("意大利");
		rome.setPreviewImage("rome");
		rome.setProductCount(productDao.getProductCount(""));
		cities.add(rome);
		
		City paris = new City();
		paris.setCityName("巴黎");
		paris.setInCountry("法国");
		paris.setPreviewImage("paris");
		paris.setProductCount(productDao.getProductCount(""));
		cities.add(paris);
		
		City shanghai = new City();
		shanghai.setCityName("上海");
		shanghai.setInCountry("中国");
		shanghai.setPreviewImage("shanghai");
		shanghai.setProductCount(productDao.getProductCount(""));
		cities.add(shanghai);
		
		City london = new City();
		london.setCityName("伦敦");
		london.setInCountry("英国");
		london.setPreviewImage("london");
		london.setProductCount(productDao.getProductCount(""));
		cities.add(london);

		return cities;
	}

}
