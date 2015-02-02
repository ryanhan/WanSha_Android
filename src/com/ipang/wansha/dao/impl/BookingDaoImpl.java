package com.ipang.wansha.dao.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.BookingDao;
import com.ipang.wansha.exception.BookingException;
import com.ipang.wansha.exception.CityException;
import com.ipang.wansha.exception.HttpException;
import com.ipang.wansha.model.Booking;
import com.ipang.wansha.model.City;
import com.ipang.wansha.model.Contact;
import com.ipang.wansha.model.Traveller;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;

public class BookingDaoImpl implements BookingDao {

	@Override
	public void submitBooking(int productId, String tripBegda, int total,
			int adultNumber, int childNumber, int infantNumber,
			Traveller[] travellers, Contact contact, String JSessionId)
			throws BookingException {

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
			JSONObject infantJson = new JSONObject();
			infantJson.put("comboType", 2);
			infantJson.put("productId", productId);
			infantJson.put("quantity", 0);
			comboJson.put(infantJson);
			json.put("comboList", comboJson);
			JSONArray travellersJson = new JSONArray();
			for (Traveller traveller : travellers) {
				JSONObject travellerJson = new JSONObject();
				if (traveller == null) {
					travellerJson.put("chineseName", "");
					travellerJson.put("pingyin", "");
					travellerJson.put("idNo", "");
					travellerJson.put("mobile", "");
				} else {
					travellerJson.put(
							"chineseName",
							traveller.getName() == null ? "" : traveller
									.getName());
					travellerJson.put(
							"pingyin",
							traveller.getPinyin() == null ? "" : traveller
									.getPinyin());
					travellerJson.put(
							"idNo",
							traveller.getPassport() == null ? "" : traveller
									.getPassport());
					travellerJson.put(
							"mobile",
							traveller.getMobile() == null ? "" : traveller
									.getMobile());
				}
				travellersJson.put(travellerJson);
			}
			json.put("travellerList", travellersJson);
			JSONObject contactJson = new JSONObject();
			contactJson.put("chineseName", contact.getName() == null ? ""
					: contact.getName());
			contactJson.put("mobile",
					contact.getTel() == null ? "" : contact.getTel());
			contactJson.put("email",
					contact.getEmail() == null ? "" : contact.getEmail());
			json.put("contact", contactJson);
		} catch (JSONException e) {
			throw new BookingException(BookingException.JSON_FORMAT_NOT_MATCH);
		}

		System.out.println("Booking request: " + json.toString());

		String response = null;
		try {
			response = HttpUtility.PostJson(url, json, JSessionId);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new BookingException(BookingException.NETWORK_CONNECT_FAILED);
		}
		System.out.println("Booking response: " + response);

	}

	@Override
	public List<Booking> getBookingList(int offset, int number,
			String JSessionId) throws BookingException {

		List<Booking> bookings = new ArrayList<Booking>();

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/orderdata/list.json?skip="
					+ offset + "&top=" + number);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new BookingException(BookingException.UNKNOWN_ERROR);
		}

		String result = null;
		try {
			result = HttpUtility.GetJson(url);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new BookingException(BookingException.NETWORK_CONNECT_FAILED);
		}

		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(result);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new BookingException(BookingException.JSON_FORMAT_NOT_MATCH);
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject bookingJson = jsonArray.getJSONObject(i);
				bookings.add(createBooking(bookingJson));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return bookings;
	}

	@Override
	public List<Booking> getBookingList(String JSessionId)
			throws BookingException {
		return getBookingList(0, 100, JSessionId);
	}

	private Booking createBooking(JSONObject bookingJson) throws JSONException {
		Booking booking = new Booking();

		booking.setBookingId(bookingJson.getString("externalOrderId"));
		booking.setProductName(bookingJson.getString("firstProductName"));
		booking.setProductImage(bookingJson.getString("firstProductPath"));
		booking.setOrderStatus(bookingJson.getInt("orderStatus"));
		booking.setOrderStatusText(bookingJson.getString("orderStatusTxt"));
		booking.setOrderDate(bookingJson.getString("orderTime"));
		booking.setTotal(bookingJson.getInt("total"));

		JSONArray contactsArray = bookingJson.getJSONArray("contacts");
		List<Traveller> travellers = new ArrayList<Traveller>();

		for (int i = 0; i < contactsArray.length(); i++) {
			JSONObject contactJson = contactsArray.getJSONObject(i);
			boolean isContact = contactJson.getBoolean("contact");
			if (isContact) {
				Contact contact = new Contact();
				if (!contactJson.isNull("chineseName")) {
					contact.setName(contactJson.getString("chineseName"));
				}
				if (!contactJson.isNull("mobile")) {
					contact.setTel(contactJson.getString("mobile"));
				}
				if (!contactJson.isNull("email")) {
					contact.setEmail(contactJson.getString("email"));
				}
				booking.setContact(contact);
			} else {
				Traveller traveller = new Traveller();
				if (!contactJson.isNull("chineseName")) {
					traveller.setName(contactJson.getString("chineseName"));
				}
				if (!contactJson.isNull("pingyin")) {
					traveller.setPinyin(contactJson.getString("pingyin"));
				}
				if (!contactJson.isNull("idNo")) {
					traveller.setPassport(contactJson.getString("idNo"));
				}
				if (!contactJson.isNull("mobile")) {
					traveller.setMobile(contactJson.getString("mobile"));
				}
				travellers.add(traveller);
			}
		}
		booking.setTravellers(travellers);

		JSONArray itemArray = bookingJson.getJSONArray("items");
		JSONObject itemObject = itemArray.getJSONObject(0);
		booking.setTravelDate(itemObject.getString("tripBegda"));
		booking.setProductId(itemObject.getInt("productId"));

		JSONArray quantitySet = itemObject.getJSONArray("quantitySet");
		for (int i = 0; i < quantitySet.length(); i++) {
			JSONObject quantityJson = quantitySet.getJSONObject(i);
			switch (quantityJson.getInt("comboType")) {
			case 0:
				booking.setChildNumber(quantityJson.getInt("quantity"));
				break;
			case 1:
				booking.setAdultNumber(quantityJson.getInt("quantity"));
				break;
			case 2:
				booking.setInfantNumber(quantityJson.getInt("quantity"));
				break;
			}
		}

		return booking;
	}
}
