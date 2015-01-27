package com.ipang.wansha.dao.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;

public class UserDaoImpl implements UserDao {

	@Override
	public boolean register(String userName, String password)
			throws MalformedURLException {

		URL url = new URL(Const.SERVERNAME + "/user/register.json");

		JSONObject json = new JSONObject();
		try {
			json.put("uname", userName);
			json.put("password", password);
		} catch (JSONException e) {
			return false;
		}
		String response = HttpUtility.PostJson(url, json);
		System.out.println(response);

		try {
			JSONObject responseJson = new JSONObject(response);
			if (responseJson.getInt("code") == 0) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	@Override
	public User login(String userName, String password)
			throws MalformedURLException {

		URL url = new URL(Const.SERVERNAME + "/user/login.json");
		HashMap<String, String> postParam = new HashMap<String, String>();
		postParam.put("user", userName);
		postParam.put("password", password);

		String JSessionId = null;
		try {
			JSessionId = HttpUtility.getJSessionId();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (JSessionId == null)
			return null;

		String response = HttpUtility.PostParam(url, postParam, JSessionId);
		System.out.println("login: " + response);

		try {
			JSONObject json = new JSONObject(response);
			if (json.getInt("status") == 1) {
				User user = new User();
				user.setUserId(json.getInt("id"));
				user.setUserName(json.getString("uname"));
				user.setPassword(password);
				user.setEmail(json.isNull("email") ? null : json
						.getString("email"));
				user.setMobile(json.isNull("mobile") ? null : json
						.getString("mobile"));
				user.setJSessionId(JSessionId);
				return user;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	public User isAlive(String JSessionId) throws MalformedURLException {
		URL url = new URL(Const.SERVERNAME + "/user/isAlive.json");

		String response = HttpUtility.GetJson(url, JSessionId);
		System.out.println("isAlive: " + response);

		try {
			JSONObject json = new JSONObject(response);
			if (json.getInt("status") == 1) {
				User user = new User();
				user.setUserId(json.getInt("id"));
				user.setUserName(json.getString("uname"));
				user.setEmail(json.isNull("email") ? null : json
						.getString("email"));
				user.setMobile(json.isNull("mobile") ? null : json
						.getString("mobile"));
				user.setJSessionId(JSessionId);
				return user;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@Override
	public boolean changePassword(String oldPassword, String newPassword,
			String JSessionId) throws MalformedURLException {
		URL url = new URL(Const.SERVERNAME + "/user/changepass.json");
		HashMap<String, String> postParam = new HashMap<String, String>();
		postParam.put("oldPass", oldPassword);
		postParam.put("newPass", newPassword);

		String response = HttpUtility.PostParam(url, postParam, JSessionId);
		System.out.println("changePassword: " + response);

		try {
			JSONObject json = new JSONObject(response);
			int code = json.getInt("code");
			if (code == 0) {
				return true;
			} else {
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void logout(String JSESSIONID) throws MalformedURLException {
		URL url = new URL(Const.SERVERNAME + "/user/logout.json");
		String response = HttpUtility.GetJson(url, JSESSIONID);
		System.out.println("logout: " + response);
	}

}
