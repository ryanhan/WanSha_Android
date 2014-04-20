package com.ipang.wansha.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {

	private static final long serialVersionUID = 8205438106558942992L;
	
	private String productId;
	private String productName;
	private float duration;
	private TimeUnit timeUnit;
	private int reviewTotalRanking;
	private int reviewCount;
	private int[] starCount;
	private float price;
	private Currency currency;
	private ArrayList<Language> supportLanguage;
	private String overview;
	private String highlight;
//	private ArrayList<Review> reviews;
	private String meetingAddress;
	private ArrayList<String> productImages;
	private String cityName;
	private String countryName;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public int getStarCount(int i) {// i=0 -> 5 star ...
		return starCount[i];
	}

	public void setStarCount(int[] starCount) {
		if (starCount.length != 5)
			return;
		this.starCount = starCount;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public ArrayList<Language> getSupportLanguage() {
		return supportLanguage;
	}

	public void setSupportLanguage(ArrayList<Language> supportLanguage) {
		this.supportLanguage = supportLanguage;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getHighlight() {
		return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

//	public ArrayList<Review> getReviews() {
//		return reviews;
//	}
//
//	public void setReviews(ArrayList<Review> reviews) {
//		this.reviews = reviews;
//	}

	public String getMeetingAddress() {
		return meetingAddress;
	}

	public void setMeetingAddress(String meetingAddress) {
		this.meetingAddress = meetingAddress;
	}

	public ArrayList<String> getProductImages() {
		return productImages;
	}

	public void setProductImages(ArrayList<String> productImages) {
		this.productImages = productImages;
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

	public int getReviewTotalRanking() {
		return reviewTotalRanking;
	}

	public void setReviewTotalRanking(int reviewTotalRanking) {
		this.reviewTotalRanking = reviewTotalRanking;
	}

	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

}
