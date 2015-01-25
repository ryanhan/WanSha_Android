package com.ipang.wansha.dao.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.model.Booking;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.RestUtility;
import com.ipang.wansha.utils.Utility;

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
		String response = RestUtility.PostJson(url, json);
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

		String response = RestUtility.PostParam(url, postParam);

		try {
			JSONObject json = new JSONObject(response);
			if (json.getInt("status") == 1) {
				User user = new User();
				user.setUserId(json.getInt("id"));
				user.setUserName(json.getString("uname"));
				user.setPassword(password);
				user.setEmail(json.isNull("email") ? null : json.getString("email"));
				user.setMobile(json.isNull("mobile") ? null : json.getString("mobile"));
				return user;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public User isAlive() throws MalformedURLException{
		URL url = new URL(Const.SERVERNAME + "/user/isAlive.json");

		String response = RestUtility.GetJson(url);

		try {
			JSONObject json = new JSONObject(response);
			if (json.getInt("status") == 1) {
				User user = new User();
				user.setUserId(json.getInt("id"));
				user.setUserName(json.getString("uname"));
				user.setEmail(json.isNull("email") ? null : json.getString("email"));
				user.setMobile(json.isNull("mobile") ? null : json.getString("mobile"));
				return user;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@Override
	public boolean changePassword(String oldPassword, String newPassword) throws MalformedURLException {
		URL url = new URL(Const.SERVERNAME + "/user/changepass.json");
		HashMap<String, String> postParam = new HashMap<String, String>();
		postParam.put("oldPass", oldPassword);
		postParam.put("newPass", newPassword);

		//String response = RestUtility.PostParam(url, postParam);
		
		return false;
	}

}
