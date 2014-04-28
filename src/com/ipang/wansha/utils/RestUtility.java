package com.ipang.wansha.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class RestUtility {

	private HttpClient client;

	public RestUtility() {
		client = new DefaultHttpClient();
	}

	public String JsonGet(URI uri) {
		HttpGet httpGet = new HttpGet(uri);
		httpGet.addHeader("accept", "application/json");
		try {
			HttpResponse response = client.execute(httpGet);
			if (response.getStatusLine().getStatusCode() != 200)
				return null;
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

	public String TextGet(URI uri) {
		HttpGet httpGet = new HttpGet(uri);
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

	public HttpResponse Post(URI uri, HashMap<String, String> postParam) {
		HttpPost httppost = new HttpPost(uri);
		List<BasicNameValuePair> formParams = new LinkedList<BasicNameValuePair>();
		Iterator<String> iter = postParam.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			formParams.add(new BasicNameValuePair(key, postParam.get(key)));
		}

		try {
			httppost.setEntity(new UrlEncodedFormEntity(formParams, "utf-8"));
			HttpResponse response = client.execute(httppost);
			return response;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Bitmap ImageGet(String path) {
		try {
			URL url = new URL(path);
			InputStream in = url.openStream();
			InputStream buf = new BufferedInputStream(in);
			Bitmap bm = BitmapFactory.decodeStream(buf);
			if (in != null) {
				in.close();
			}
			if (buf != null) {
				buf.close();
			}
			return bm;
		} catch (Exception e) {
			return null;
		}
	}
}
