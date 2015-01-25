package com.ipang.wansha.model;

public class Country {

	private int countryId;
	private String countryName;
	private String countryImage;
	private String continent;
	private int countryProductCount;
	
	public int getCountryId() {
		return countryId;
	}
	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getCountryImage() {
		return countryImage;
	}
	public void setCountryImage(String countryImage) {
		this.countryImage = countryImage;
	}
	
	public String getContinent() {
		return continent;
	}
	public void setContinent(String continent) {
		this.continent = continent;
	}
	
	public int getCountryProductCount() {
		return countryProductCount;
	}
	
	public void setCountryProductCount(int countryProductCount) {
		this.countryProductCount = countryProductCount;
	}
}
