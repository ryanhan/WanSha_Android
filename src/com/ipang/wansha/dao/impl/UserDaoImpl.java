package com.ipang.wansha.dao.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;

import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.exception.HttpException;
import com.ipang.wansha.exception.UserException;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;

public class UserDaoImpl implements UserDao {

	@Override
	public void register(String userName, String password) throws UserException {

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/user/register.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new UserException(UserException.UNKNOWN_ERROR);
		}

		JSONObject json = new JSONObject();
		try {
			json.put("uname", userName);
			json.put("password", password);
		} catch (JSONException e) {
			throw new UserException(UserException.JSON_FORMAT_NOT_MATCH);
		}

		String response = null;
		try {
			response = HttpUtility.PostJson(url, json);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new UserException(UserException.NETWORK_CONNECT_FAILED);
		}
		System.out.println("register: " + response);

		try {
			JSONObject responseJson = new JSONObject(response);
			if (responseJson.getInt("code" == 99){
				throw new UserException(UserException.DUPLICATE_USERNAME);
			}
			else if (responseJson.getInt("code") != 0) {
				throw new UserException(UserException.REGISTER_FAILED);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new UserException(UserException.UNKNOWN_ERROR);
		}
	}

	@Override
	public User login(String userName, String password) throws UserException {

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/user/login.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new UserException(UserException.UNKNOWN_ERROR);
		}

		HashMap<String, String> postParam = new HashMap<String, String>();
		postParam.put("user", userName);
		postParam.put("password", password);

		String JSessionId = null;
		try {
			JSessionId = HttpUtility.getJSessionId();
		} catch (IOException e) {
			e.printStackTrace();
			throw new UserException(UserException.JSESSION_NOT_FOUND);
		}

		if (JSessionId == null) {
			throw new UserException(UserException.JSESSION_NOT_FOUND);
		}

		String response = null;
		try {
			response = HttpUtility.PostParam(url, postParam, JSessionId);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new UserException(UserException.NETWORK_CONNECT_FAILED);
		}
		System.out.println("login: " + response);

		User user = new User();
		JSONObject json = null;
		try {
			json = new JSONObject(response);
			if (json.getInt("status") != 1) {
				throw new UserException(UserException.LOGIN_FAILED);
			} else {
				user.setUserId(json.getInt("id"));
				user.setUserName(json.getString("uname"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new UserException(UserException.JSON_FORMAT_NOT_MATCH);
		}
		try {
			if (!json.isNull("email")) {
				user.setEmail(json.getString("email"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			if (!json.isNull("mobile")) {
				user.setMobile(json.getString("mobile"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		user.setPassword(password);
		user.setJSessionId(JSessionId);
		return user;
	}

	public User isAlive(String JSessionId) throws UserException {

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/user/isAlive.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new UserException(UserException.UNKNOWN_ERROR);
		}

		String response = null;
		try {
			response = HttpUtility.GetJson(url, JSessionId);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new UserException(UserException.NETWORK_CONNECT_FAILED);
		}
		System.out.println("isAlive: " + response);

		User user = new User();
		JSONObject json = null;
		try {
			json = new JSONObject(response);
			if (json.getInt("status") != 1) {
				throw new UserException(UserException.NOT_ALIVE);
			} else {
				user.setUserId(json.getInt("id"));
				user.setUserName(json.getString("uname"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new UserException(UserException.JSON_FORMAT_NOT_MATCH);
		}
		try {
			if (!json.isNull("email")) {
				user.setEmail(json.getString("email"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			if (!json.isNull("mobile")) {
				user.setMobile(json.getString("mobile"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		user.setJSessionId(JSessionId);
		return user;
	}

	@Override
	public void changePassword(String oldPassword, String newPassword,
			String JSessionId) throws UserException {

		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/user/changepass.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new UserException(UserException.UNKNOWN_ERROR);
		}

		HashMap<String, String> postParam = new HashMap<String, String>();
		postParam.put("oldPass", oldPassword);
		postParam.put("newPass", newPassword);

		String response = null;
		try {
			response = HttpUtility.PostParam(url, postParam, JSessionId);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new UserException(UserException.NETWORK_CONNECT_FAILED);
		}
		System.out.println("changePassword: " + response);

		try {
			JSONObject json = new JSONObject(response);
			int code = json.getInt("code");
			if (code != 0) {
				throw new UserException(UserException.CHANGE_PASSWORD_FAILED);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new UserException(UserException.JSON_FORMAT_NOT_MATCH);
		}
	}

	@Override
	public void logout(String JSESSIONID) throws UserException {
		URL url = null;
		try {
			url = new URL(Const.SERVERNAME + "/user/logout.json");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new UserException(UserException.UNKNOWN_ERROR);
		}

		String response = null;
		try {
			response = HttpUtility.GetJson(url, JSESSIONID);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new UserException(UserException.NETWORK_CONNECT_FAILED);
		}
		System.out.println("logout: " + response);
	}

	@Override
	public User checkLoginStatus(String userName, String password,
			String JSESSIONID) throws UserException {

		User user = null;
		try {
			user = isAlive(JSESSIONID);
			return user;
		} catch (UserException e) {
			e.printStackTrace();
			if (e.getExceptionCause() == UserException.NOT_ALIVE) {
				try {
					user = login(userName, password);
					return user;
				} catch (UserException e1) {
					e1.printStackTrace();
					throw e1;
				}
			}
			else{
				throw e;
			}
		}

	}
}
