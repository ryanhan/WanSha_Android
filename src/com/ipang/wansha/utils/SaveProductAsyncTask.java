package com.ipang.wansha.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ipang.wansha.dao.OfflineDao;
import com.ipang.wansha.dao.impl.OfflineDaoImpl;
import com.ipang.wansha.exception.OfflineException;
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
		return offlineDao.addProduct(context, product);
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