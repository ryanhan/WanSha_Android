package com.ipang.wansha.dao.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.AppDao;
import com.ipang.wansha.exception.AppInfoException;
import com.ipang.wansha.exception.HttpException;
import com.ipang.wansha.model.AppInfo;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;

public class AppDaoImpl implements AppDao{

	@Override
	public AppInfo getLatestVersion() throws AppInfoException {
		
		AppInfo appInfo = new AppInfo(); 
		
		URL url = null;

		try {
			url = new URL(Const.FIR_IM_URL + Const.FIR_IM_ID);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new AppInfoException(AppInfoException.UNKNOWN_ERROR);
		}

		String result = null;
		try {
			result = HttpUtility.GetJson(url);
		} catch (HttpException e) {
			e.printStackTrace();
			throw new AppInfoException(AppInfoException.NETWORK_CONNECT_FAILED);
		}
		
		try {
			JSONObject json = new JSONObject(result);
			appInfo.setVersion(json.getString("version"));
			appInfo.setVersionShort(json.getString("versionShort"));
			appInfo.setUrl(json.getString("installUrl"));
		} catch (JSONException e) {
			e.printStackTrace();
			throw new AppInfoException(AppInfoException.JSON_FORMAT_NOT_MATCH);
		}
		return appInfo;
	}

}
