package com.ipang.wansha.dao.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.ReviewDao;
import com.ipang.wansha.enums.Currency;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;
import com.ipang.wansha.utils.Utility;

public class ProductDaoImpl implements ProductDao {

	private ReviewDao reviewDao;

	public ProductDaoImpl() {
		reviewDao = new ReviewDaoImpl();
	}

	@Override
	public List<Product> getProductList(int cityId)
			throws MalformedURLException, InterruptedException,
			ExecutionException, JSONException {

		return getProductList(cityId, 0, 500);

	}

	@Override
	public List<Product> getProductList(int cityId, int offset, int number)
			throws MalformedURLException, InterruptedException,
			ExecutionException, JSONException {
		List<Product> products = new ArrayList<Product>();
		URL url = new URL(Const.SERVERNAME + "/product/plist.json?cityId="
				+ cityId + "&top=" + number + "&skip=" + offset);
		String result = HttpUtility.GetJson(url);

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		JSONArray productList = jsonObject.getJSONArray("productList");

		if (productList.length() == 0)
			return null;

		for (int i = 0; i < productList.length(); i++) {
			JSONObject productJson = productList.getJSONObject(i);
			products.add(createProduct(productJson));
		}

		return products;
	}

	@Override
	public Product getProductDetail(int productId)
			throws MalformedURLException, InterruptedException,
			ExecutionException, JSONException {

		URL url = new URL(Const.SERVERNAME + "/product/" + productId + ".json");
		String result = HttpUtility.GetJson(url);

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return createProduct(jsonObject);
	}

	private Product createProduct(JSONObject json) {

		Product product = new Product();
		try {
			product.setProductId(json.getInt("id"));
			product.setProductName(json.getString("productName"));
			product.setProductType(json.getInt("productType"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		if (!json.isNull("pictureList")) {
			JSONArray imageList = null;
			try {
				imageList = json.getJSONArray("pictureList");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (imageList != null && imageList.length() > 0) {
				ArrayList<String> images = new ArrayList<String>();
				for (int i = 0; i < imageList.length(); i++) {
					try {
						JSONObject imageJson = imageList.getJSONObject(i);
						String imageUrl = imageJson.getString("path");

						if (imageUrl.charAt(0) == '/')
							imageUrl = Const.SERVERNAME + imageUrl;
						images.add(imageUrl);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				product.setProductImages(images);
			}
		}

		if (product.getProductType() == 1) { // 收费
			if (!json.isNull("comboList")) {
				JSONArray comboList = null;
				try {
					comboList = json.getJSONArray("comboList");
				} catch (JSONException e) {
					e.printStackTrace();
					product.setPrice(0);
				}
				if (comboList != null && comboList.length() > 0) {
					float lowest = -1;
					for (int i = 0; i < comboList.length(); i++) {
						JSONObject priceJson;
						try {
							priceJson = comboList.getJSONObject(i);
							float price = (float) priceJson.getDouble("price");
							if (!priceJson.isNull("currency")) {
								product.setCurrency(Currency
										.fromString(priceJson
												.getString("currency")));
							}
							if (lowest == -1 && price >= 0){
								lowest = price;
							}
							else if (lowest != -1 && price < lowest)
								lowest = price;
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					product.setPrice(lowest == -1 ? 0 : lowest);
				}
			}
		} else if (product.getProductType() == 2) { // 免费
			product.setPrice(0);
		}

		if (!json.isNull("detail")) {
			try {
				product.setDetail(Utility.formatText(json.getString("detail")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (!json.isNull("expenseDescr")) {
			try {
				product.setExpenseDescr(Utility.formatText(json
						.getString("expenseDescr")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (!json.isNull("instruction")) {
			try {
				product.setInstruction(Utility.formatText(json
						.getString("instruction")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (!json.isNull("orderDescr")) {
			try {
				product.setOrderDescr(Utility.formatText(json
						.getString("orderDescr")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return product;
	}

}
