package com.ipang.wansha.model;

import java.util.List;

public class Booking {

	private String bookingId;
	private int productId;
	private String productName;
	private String productImage;
	private int orderStatus;
	private String orderStatusText;
	private int adultNumber;
	private int childNumber;
	private int infantNumber;
	private int total;
	private String travelDate;
	private String orderDate;
	private List<Traveller> travellers;
	private Contact contact;

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public int getAdultNumber() {
		return adultNumber;
	}

	public void setAdultNumber(int adultNumber) {
		this.adultNumber = adultNumber;
	}

	public int getChildNumber() {
		return childNumber;
	}

	public void setChildNumber(int childNumber) {
		this.childNumber = childNumber;
	}

	public int getInfantNumber() {
		return infantNumber;
	}

	public void setInfantNumber(int infantNumber) {
		this.infantNumber = infantNumber;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getTravelDate() {
		return travelDate;
	}

	public void setTravelDate(String travelDate) {
		this.travelDate = travelDate;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public List<Traveller> getTravellers() {
		return travellers;
	}

	public void setTravellers(List<Traveller> travellers) {
		this.travellers = travellers;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getOrderStatusText() {
		return orderStatusText;
	}

	public void setOrderStatusText(String orderStatusText) {
		this.orderStatusText = orderStatusText;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

}
