package com.ipang.wansha.model;

import java.util.Date;

public class TripHour {

	private String tripHourId;
	private Date startTime;
	private int vacancy;
	private float price;

	public String getTripHourId() {
		return tripHourId;
	}

	public void setTripHourId(String tripHourId) {
		this.tripHourId = tripHourId;
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getVacancy() {
		return vacancy;
	}

	public void setVacancy(int vacancy) {
		this.vacancy = vacancy;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

}
