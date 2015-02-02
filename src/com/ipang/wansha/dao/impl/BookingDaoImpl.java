package com.ipang.wansha.dao.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.BookingDao;
import com.ipang.wansha.exception.BookingException;
import com.ipang.wansha.exception.HttpException;
import com.ipang.wansha.model.Contact;
import com.ipang.wansha.model.Traveller;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;

public class BookingDaoImpl implements BookingDao {

	@Override
	public void submitBooking(int productId, String tripBegda, int total,
			int adultNumber, int childNumber, Traveller[] travellers,
			Contact contact, String JSessionId) throws BookingException {

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/orderdata/place.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new BookingException(BookingException.UNKNOWN_ERROR);
		}

		JSONObject json = new JSONObject();
		try {
			json.put("productId", productId);
			json.put("tripBegda", tripBegda);
			json.put("total", total);
			JSONArray comboJson = new JSONArray();
			JSONObject adultJson = new JSONObject();
			adultJson.put("comboType", 1);
			adultJson.put("productId", productId);
			adultJson.put("quantity", adultNumber);
			comboJson.put(adultJson);
			JSONObject childJson = new JSONObject();
			childJson.put("comboType", 0);
			childJson.put("productId", productId);
			childJson.put("quantity", childNumber);
			comboJson.put(childJson);
			json.put("comboList", comboJson);
			JSONArray travellersJson = new JSONArray();
			for (Traveller traveller: travellers){
				JSONObject travellerJson = new JSONObject();
				travellerJson.put("chineseName", traveller.getName());
				travellerJson.put("pinyin", traveller.getPinyin());
				travellerJson.put("passport", traveller.getPassport());
				travellerJson.put("mobile", traveller.getMobile());
				travellersJson.put(travellerJson);
			}
			json.put("travellerList", travellersJson);
			JSONObject contactJson = new JSONObject();
			contactJson.put("chineseName", contact.getName());
			contactJson.put("mobile", contact.getTel());
			contactJson.put("email", contact.getEmail());
			json.put("contact", contactJson);
		} catch (JSONException e) {
			throw new BookingException(BookingException.JSON_FORMAT_NOT_MATCH);
		}
		
		String response = null;
		try {
			response = HttpUtility.PostJson(url, json, JSessionId);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new BookingException(BookingException.NETWORK_CONNECT_FAILED);
		}
		System.out.println("submitBooking: " + response);

	}

}
