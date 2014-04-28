package com.ipang.wansha.dao.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.UserDao;
import com.ipang.wansha.model.Booking;
import com.ipang.wansha.model.User;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.RestUtility;

public class UserDaoImpl implements UserDao{
	
	private RestUtility utility;

	public UserDaoImpl(){
		utility = new RestUtility();
	}

	@Override
	public boolean register(String userName) throws URISyntaxException, InterruptedException, ExecutionException {
		
		URI uri = new URI(Const.SERVERNAME + "/rest/register");
		HashMap<String, String> postParams = new HashMap<String, String>();
		postParams.put("username", userName);
		HttpResponse response = utility.Post(uri, postParams);
		if (response.getStatusLine().getStatusCode() == 201)
			return true;
		else
			return false;
		
	}

	@Override
	public User getUserInfo(String userName) throws URISyntaxException, InterruptedException, ExecutionException {
		
		URI uri = new URI(Const.SERVERNAME + "/rest/user/name/" + userName);
		String result = utility.JsonGet(uri);

		if (result == null)
			return null;

		JSONObject jsonObject = null;
		
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		User user = new User();
		try {
			user.setUserId(jsonObject.getString("userId"));
			user.setUserName(jsonObject.getString("userName"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		try {
			user.setUserToken(jsonObject.getString("userToken"));
		} catch (JSONException e) {}
		try {
			user.setEmail(jsonObject.getString("email"));
		} catch (JSONException e) {}
		
		return user;
	}

	@Override
	public List<Booking> getBookingOfUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
