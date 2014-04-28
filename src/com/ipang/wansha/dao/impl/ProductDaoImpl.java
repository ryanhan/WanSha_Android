package com.ipang.wansha.dao.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.ReviewDao;
import com.ipang.wansha.enums.Currency;
import com.ipang.wansha.enums.TimeUnit;
import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.model.TripHour;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.RestUtility;
import com.ipang.wansha.utils.Utility;

public class ProductDaoImpl implements ProductDao {

	private ReviewDao reviewDao;
	private RestUtility utility;


	public ProductDaoImpl() {
		reviewDao = new ReviewDaoImpl();
		utility = new RestUtility();
	}

	@Override
	public List<Product> getProductList(String cityId)
			throws URISyntaxException, InterruptedException,
			ExecutionException, JSONException {

		List<Product> products = new ArrayList<Product>();
		URI uri = new URI(Const.SERVERNAME + "/rest/city/" + cityId
				+ "/product");
		String result = utility.JsonGet(uri);

		if (result == null)
			return null;

		JSONObject jsonObject = new JSONObject(result);

		if (jsonObject.isNull("product"))
			return null;

		try {
			JSONArray productList = jsonObject.getJSONArray("product");
			for (int i = 0; i < productList.length(); i++) {
				JSONObject productJson = productList.getJSONObject(i);
				products.add(createProduct(productJson));
			}
		} catch (JSONException e) {
			JSONObject json = jsonObject.getJSONObject("product");
			products.add(createProduct(json));
		}
		return products;
	}

	@Override
	public int getProductCount(String cityId) throws URISyntaxException,
			InterruptedException, ExecutionException {

		URI uri = new URI(Const.SERVERNAME + "/rest/city/" + cityId
				+ "/product/count");
		String result = utility.TextGet(uri);
		return Integer.parseInt(result);
	}

	@Override
	public Product getProductDetail(String productId)
			throws URISyntaxException, InterruptedException,
			ExecutionException, JSONException {

		URI uri = new URI(Const.SERVERNAME + "/rest/product/" + productId);
		String result = utility.JsonGet(uri);

		if (result == null)
			return null;

		JSONObject jsonObject = new JSONObject(result);

		Product product = createProduct(jsonObject);

		return product;
	}

	private Product createProduct(JSONObject json) throws JSONException,
			URISyntaxException, InterruptedException, ExecutionException {

		Product product = new Product();
		product.setProductId(json.getString("productId"));
		product.setProductName(json.getString("productName"));
		product.setCurrency(Currency.fromString(json.getString("currency")));
		product.setDuration(Float.parseFloat(json.getString("duration")));
		product.setHighlight(json.getString("highlight"));
		product.setMeetingAddress(json.getString("meetingAddress"));
		product.setOverview(json.getString("overview"));
		product.setPrice(Float.parseFloat(json.getString("price")));
		product.setTimeUnit(TimeUnit.fromString(json.getString("timeUnit")));
		product.setReviewCount(json.getInt("reviewCount"));
		product.setReviewTotalRanking(json.getInt("reviewTotalRanking"));
		product.setProductImages(getProductImages(json));
		product.setStarCount(reviewDao.getRankingDetail(product.getProductId()));
		return product;
	}

	private ArrayList<String> getProductImages(JSONObject json)
			throws JSONException {
		ArrayList<String> images = new ArrayList<String>();

		if (json.isNull("productImages"))
			return null;
		
		try {
			JSONArray imageList = json.getJSONArray("productImages");
			for (int i = 0; i < imageList.length(); i++) {
				images.add(imageList.getString(i));
			}

		} catch (JSONException e) {
			images.add(json.getString("productImages"));
		}

		return images;
	}

	@Override
	public City getCityOfProduct(String productId) throws URISyntaxException,
			InterruptedException, ExecutionException, JSONException {
		URI uri = new URI(Const.SERVERNAME + "/rest/product/" + productId
				+ "/city");
		String result = utility.JsonGet(uri);

		if (result == null)
			return null;

		JSONObject jsonObject = new JSONObject(result);
		City city = new City();
		city.setCityId(jsonObject.getString("cityId"));
		city.setCityName(jsonObject.getString("cityName"));
		city.setInCountry(jsonObject.getString("inCountry"));
		return city;
	}

	@Override
	public List<TripHour> getTripTimeInDate(String productId, String dateStr) throws URISyntaxException, InterruptedException, ExecutionException, JSONException {

		List<TripHour> timeList = new ArrayList<TripHour>();
		URI uri = new URI(Const.SERVERNAME + "/rest/product/" + productId
				+ "/available?date=" + dateStr);
		String result = utility.JsonGet(uri);

		if (result == null)
			return null;
		
		JSONObject jsonObject = new JSONObject(result);

		if (jsonObject.isNull("tripHour"))
			return null;

		try {
			JSONArray jsonList = jsonObject.getJSONArray("tripHour");
			for (int i = 0; i < jsonList.length(); i++) {
				JSONObject timeJson = jsonList.getJSONObject(i);
				timeList.add(createTripHour(timeJson));
			}
		} catch (JSONException e) {
			JSONObject json = jsonObject.getJSONObject("tripHour");
			timeList.add(createTripHour(json));
		}

		return timeList;
	}

	private TripHour createTripHour(JSONObject json) throws JSONException {
		TripHour th = new TripHour();
		th.setTripHourId(json.getString("tripHourId"));
		try {
			th.setStartTime(Utility.FormatString(json.getString("startTime")));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}		
		th.setVacancy(json.getInt("vacancy"));
		th.setPrice(Float.parseFloat(json.getString("price")));
		
		return th;
	}

	@Override
	public boolean bookTrip(String userId, String productId, Date startDate, int memberNo) throws URISyntaxException, InterruptedException, ExecutionException {

		if (userId == null || productId == null)
			return false;

		URI uri = new URI(Const.SERVERNAME + "/rest/user/" + userId + "/product/" + productId + "/booking");
		HashMap<String, String> postParams = new HashMap<String, String>();
		postParams.put("memberNo", memberNo + "");
		postParams.put("time", Utility.FormatDateTime(startDate));

		HttpResponse response = utility.Post(uri, postParams);
		if (response.getStatusLine().getStatusCode() == 201)
			return true;
		else
			return false;
		
		
	}

}
