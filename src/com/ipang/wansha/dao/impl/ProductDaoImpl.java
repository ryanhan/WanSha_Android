package com.ipang.wansha.dao.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.enums.Currency;
import com.ipang.wansha.exception.HttpException;
import com.ipang.wansha.exception.ProductException;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;
import com.ipang.wansha.utils.Utility;

public class ProductDaoImpl implements ProductDao {

	@Override
	public List<Product> getProductList(int cityId) throws ProductException {
		return getProductList(cityId, 0, 500);
	}

	@Override
	public List<Product> getProductList(int cityId, int offset, int number)
			throws ProductException {
		List<Product> products = new ArrayList<Product>();

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/product/plist.json?cityId="
					+ cityId + "&top=" + number + "&skip=" + offset);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new ProductException(ProductException.UNKNOWN_ERROR);
		}

		String result = null;
		try {
			result = HttpUtility.GetJson(url);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new ProductException(ProductException.NETWORK_CONNECT_FAILED);
		}

		JSONArray productList = null;
		try {
			JSONObject jsonObject = new JSONObject(result);
			productList = jsonObject.getJSONArray("productList");
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ProductException(ProductException.JSON_FORMAT_NOT_MATCH);
		}

		for (int i = 0; i < productList.length(); i++) {
			try {
				JSONObject productJson = productList.getJSONObject(i);
				products.add(createProduct(productJson));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return products;
	}

	@Override
	public Product getProductDetail(int productId) throws ProductException {

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/product/" + productId + ".json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new ProductException(ProductException.UNKNOWN_ERROR);
		}

		String result = null;
		try {
			result = HttpUtility.GetJson(url);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new ProductException(ProductException.NETWORK_CONNECT_FAILED);
		}
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ProductException(ProductException.JSON_FORMAT_NOT_MATCH);
		}

		try {
			return createProduct(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new ProductException(ProductException.JSON_FORMAT_NOT_MATCH);
		}
	}

	private Product createProduct(JSONObject json) throws JSONException {

		Product product = new Product();
		product.setProductId(json.getInt("id"));
		product.setProductName(json.getString("productName"));
		product.setProductType(json.getInt("productType"));

		if (!json.isNull("pictureList")) {
			JSONArray imageList = null;
			try {
				if (!json.isNull("pictureList")) {
					imageList = json.getJSONArray("pictureList");
				}
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
							if (lowest == -1 && price >= 0) {
								lowest = price;
							} else if (lowest != -1 && price < lowest)
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

		try {
			if (!json.isNull("detail")) {
				product.setDetail(Utility.formatText(json.getString("detail")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			if (!json.isNull("expenseDescr")) {
				product.setExpenseDescr(Utility.formatText(json
						.getString("expenseDescr")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			if (!json.isNull("instruction")) {

				product.setInstruction(Utility.formatText(json
						.getString("instruction")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			if (!json.isNull("orderDescr")) {
				product.setOrderDescr(Utility.formatText(json
						.getString("orderDescr")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return product;
	}
}
