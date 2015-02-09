package com.ipang.wansha.dao;

import java.util.List;

import com.ipang.wansha.exception.ProductException;
import com.ipang.wansha.model.Product;

public interface ProductDao {

	public List<Product> getProductList(int cityId) throws ProductException;

	public List<Product> getProductList(int cityId, int offset, int number)
			throws ProductException;

	public Product getProductDetail(int productId) throws ProductException;

	public List<Product> getRecommendProductList() throws ProductException;

}
