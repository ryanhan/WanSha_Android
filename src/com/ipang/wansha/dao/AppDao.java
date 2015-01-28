package com.ipang.wansha.dao;

import com.ipang.wansha.exception.AppInfoException;
import com.ipang.wansha.model.AppInfo;

public interface AppDao {

	public AppInfo getLatestVersion() throws AppInfoException;
	
}
