package com.ipang.wansha.dao;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.ipang.wansha.model.Booking;
import com.ipang.wansha.model.User;

public interface UserDao {

	public boolean register(String userName) throws URISyntaxException, InterruptedException, ExecutionException;

	public User getUserInfo(String userName) throws URISyntaxException, InterruptedException, ExecutionException;

	public List<Booking> getBookingOfUser(String userId);

	
}
