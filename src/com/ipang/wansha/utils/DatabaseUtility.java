package com.ipang.wansha.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.model.Download;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.service.OfflineGuideDownloadService;

public class DatabaseUtility {
	
	public static void StartDownloadProducts(Context context, List<Download> downloads){
		OfflineDao offlineDao = new OfflineDaoImpl();
		offlineDao.addProductToDownloadList(context, downloads);
		Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
		for (Download download: downloads){
			Intent intent = new Intent(context, OfflineGuideDownloadService.class);
			intent.putExtra(Const.DOWNLOAD, download);
			context.startService(intent);
		}
	}
	
}
