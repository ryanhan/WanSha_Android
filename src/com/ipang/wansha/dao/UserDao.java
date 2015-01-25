package com.ipang.wansha.dao;

import java.net.MalformedURLException;

import com.ipang.wansha.model.User;

public interface UserDao {

	public boolean register(String userName, String password)
			throws MalformedURLException;

	public User login(String userName, String password)
			throws MalformedURLException;

	public User isAlive() throws MalformedURLException;

	public boolean changePassword(String oldPassword, String newPassword) throws MalformedURLException;
	
}
