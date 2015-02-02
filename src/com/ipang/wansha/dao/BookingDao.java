package com.ipang.wansha.dao;

import java.util.List;

import com.ipang.wansha.exception.BookingException;
import com.ipang.wansha.model.Booking;
import com.ipang.wansha.model.Contact;
import com.ipang.wansha.model.Traveller;

public interface BookingDao {

	public void submitBooking(int productId, String tripBegda, int total,
			int adultNumber, int childNumber, int infantNumber,
			Traveller[] travellers, Contact contact, String JSessionId)
			throws BookingException;

	public List<Booking> getBookingList(int offset, int number,
			String JSessionId) throws BookingException;

	public List<Booking> getBookingList(String JSessionId)
			throws BookingException;

}
