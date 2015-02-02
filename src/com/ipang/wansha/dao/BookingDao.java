package com.ipang.wansha.dao;

import com.ipang.wansha.exception.BookingException;
import com.ipang.wansha.model.Contact;
import com.ipang.wansha.model.Traveller;

public interface BookingDao {

	public void submitBooking(int productId, String tripBegda, int total,
			int adultNumber, int childNumber, Traveller[] travellers,
			Contact contact, String JSessionId) throws BookingException;

}
