package com.ipang.wansha.dao;

import com.ipang.wansha.exception.UserException;
import com.ipang.wansha.model.User;

public interface UserDao {

	public void register(String userName, String password)
			throws UserException;

	public User login(String userName, String password) throws UserException;

	public User isAlive(String JSESSIONID) throws UserException;

	public void changePassword(String oldPassword, String newPassword,
			String JSessionId) throws UserException;

	public void logout(String JSESSIONID) throws UserException;
}
