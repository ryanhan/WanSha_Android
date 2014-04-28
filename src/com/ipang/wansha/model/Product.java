package com.ipang.wansha.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.ipang.wansha.enums.Currency;
import com.ipang.wansha.enums.TimeUnit;

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
	private String overview;
	private String highlight;
	private String meetingAddress;
	private ArrayList<String> productImages;

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

	public int getStarCount(int i) {
		return starCount[i];
	}

	public void setStarCount(int[] starCount) {
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
