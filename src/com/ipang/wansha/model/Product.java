package com.ipang.wansha.model;

import java.util.List;

import com.ipang.wansha.enums.Currency;

public class Product{

	public static final int PAYSIGHT = 1;
	public static final int FREESIGHT = 2;

	private int productId;
	private String productName;
	private int productType;
	private float lowestPrice;
	private Currency currency;
	private String detail;
	private String expenseDescr;
	private String instruction;
	private String orderDescr;
	private List<String> productImages;
	private String brief;
	private List<Combo> combos;
	private int cityId;
	private String cityName;
	private int countryId;
	private String countryName;
	private long lastModified;

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public float getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(float lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public Currency getCurrency() {
		if (currency == null) {
			currency = Currency.CHINESEYUAN;
		}
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public List<String> getProductImages() {
		return productImages;
	}

	public void setProductImages(List<String> productImages) {
		this.productImages = productImages;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getExpenseDescr() {
		return expenseDescr;
	}

	public void setExpenseDescr(String expenseDescr) {
		this.expenseDescr = expenseDescr;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public String getOrderDescr() {
		return orderDescr;
	}

	public void setOrderDescr(String orderDescr) {
		this.orderDescr = orderDescr;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public List<Combo> getCombos() {
		return combos;
	}

	public void setCombos(List<Combo> combos) {
		this.combos = combos;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

}
