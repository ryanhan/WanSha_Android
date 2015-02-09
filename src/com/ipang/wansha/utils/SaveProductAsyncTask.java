package com.ipang.wansha.utils;

import java.io.File;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.model.Product;

public class SaveProductAsyncTask extends AsyncTask<Void, Integer, Boolean> {

	private Product product;
	private Context context;
	private OfflineDao offlineDao;

	public SaveProductAsyncTask(Product product, Context context) {
		this.product = product;
		this.context = context;
		offlineDao = new OfflineDaoImpl();
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		File path = new File(
				context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
				product.getProductId() + "");
		if (!path.exists()) {
			path.mkdir();
		}
		else if (path.isDirectory()) {
			File[] files = path.listFiles();
			for (File file : files) {
				file.delete();
				System.out.println("delete: " + file.getPath());
			}
		}

		List<String> imageUrls = product.getProductImages();
		for (int i = 0; i < imageUrls.size(); i++) {
			try {
				URL url = new URL(imageUrls.get(i));
				HttpUtility.downloadImage(url, new File(path, i + ""), context);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		offlineDao.insertProduct(product, context);
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
		}
	}

}