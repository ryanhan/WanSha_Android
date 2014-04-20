package com.ipang.wansha.dao.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.ReviewDao;
import com.ipang.wansha.model.Currency;
import com.ipang.wansha.model.Language;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.model.TimeUnit;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.JsonGetAsyncTask;
import com.ipang.wansha.utils.TextGetAsyncTask;

public class ProductDaoImpl implements ProductDao {

	private ReviewDao reviewDao;
	
	public ProductDaoImpl(){
		reviewDao = new ReviewDaoImpl();
	}
	
	
	@Override
	public List<Product> getProductList(String cityId)
			throws URISyntaxException, InterruptedException,
			ExecutionException, JSONException {

		List<Product> products = new ArrayList<Product>();
		URI uri = new URI(Const.SERVERNAME + "/rest/city/" + cityId
				+ "/product");
		JsonGetAsyncTask asyncTask = new JsonGetAsyncTask();
		String result = asyncTask.execute(uri).get();

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
	public int getProductCount(String cityId) throws URISyntaxException, InterruptedException, ExecutionException {
		
		URI uri = new URI(Const.SERVERNAME + "/rest/city/" + cityId
				+ "/product/count");
		TextGetAsyncTask asyncTask = new TextGetAsyncTask();
		String result = asyncTask.execute(uri).get();
		return Integer.parseInt(result);
	}

	@Override
	public Product getProductDetail(String productId) throws URISyntaxException, InterruptedException, ExecutionException, JSONException {

		URI uri = new URI(Const.SERVERNAME + "/rest/product/" + productId);
		JsonGetAsyncTask asyncTask = new JsonGetAsyncTask();
		String result = asyncTask.execute(uri).get();

		if (result == null)
			return null;

		JSONObject jsonObject = new JSONObject(result);

		Product product = createProduct(jsonObject);
		
		return product;
	}

	private Product createProduct(JSONObject json) throws JSONException {

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
		product.setReviewCount(Integer.parseInt(json.getString("reviewCount")));
		product.setReviewTotalRanking(Integer.parseInt(json.getString("reviewTotalRanking")));
		product.setSupportLanguage(getLanguages(json));
		product.setStarCount(reviewDao.getRankingDetail(product.getProductId()));
		return product;
	}

	private ArrayList<Language> getLanguages(JSONObject json) throws JSONException{
		ArrayList<Language> languages = new ArrayList<Language>();
		
		try {
			JSONArray languageList = json.getJSONArray("languages");
			for(int i = 0; i < languageList.length(); i++){
				languages.add(Language.fromString(languageList.getString(i)));
			}
			
		} catch (JSONException e) {
			languages.add(Language.fromString(json.getString("languages")));
		}
		
		
		return languages;
	}

}
