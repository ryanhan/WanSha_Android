package com.ipang.wansha.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;


public class TextGetAsyncTask extends AsyncTask<URI, Integer, String>{

	@Override
	protected String doInBackground(URI... params) {
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(params[0]);
		try {
			HttpResponse response = client.execute(httpGet);
			InputStream inputStream = response.getEntity().getContent();

			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = inputStream.read(b);
				if (n > 0)
					out.append(new String(b, 0, n));
			}
			return out.toString();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
