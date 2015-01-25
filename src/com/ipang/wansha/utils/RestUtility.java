package com.ipang.wansha.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class RestUtility {

	private static String readInputStream(InputStream inputStream)
			throws IOException {
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = inputStream.read(b);
			if (n > 0)
				out.append(new String(b, 0, n));
		}
		inputStream.close();
		return out.toString();
	}

	public static String GetJson(URL url) {
		HttpURLConnection urlConn = null;
		try {
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.addRequestProperty("accept", "application/json");
			urlConn.setConnectTimeout(Const.CONNECT_TIMEOUT);
			urlConn.setReadTimeout(Const.READ_TIMEOUT);

			int responseCode = urlConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = urlConn.getInputStream();
				return readInputStream(inputStream);
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}

	}

	public static String PostParam(URL url, HashMap<String, String> postParam) {

		HttpURLConnection urlConn = null;
		try {
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setConnectTimeout(Const.CONNECT_TIMEOUT);
			urlConn.setReadTimeout(Const.READ_TIMEOUT);
			String data = "";
			if (postParam != null) {
				Iterator<String> iter = postParam.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					data = data + key + "="
							+ URLEncoder.encode(postParam.get(key), "UTF-8")
							+ "&";
				}
				data = data.substring(0, data.length() - 1);
				System.out.println(data);
				urlConn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				urlConn.setRequestProperty("Content-Length",
						String.valueOf(data.getBytes().length));
				OutputStream os = urlConn.getOutputStream();
				os.write(data.getBytes());
				os.flush();
			}

			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = urlConn.getInputStream();
				return readInputStream(inputStream);
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}

	public static String PostJson(URL url, JSONObject json) {

		HttpURLConnection urlConn = null;
		try {
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setDoOutput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setConnectTimeout(Const.CONNECT_TIMEOUT);
			urlConn.setReadTimeout(Const.READ_TIMEOUT);
			if (json != null) {
				String data = URLEncoder.encode(json.toString(), "UTF-8");
				urlConn.setRequestProperty("Content-Type", "application/json");
				urlConn.setRequestProperty("Content-Length",
						String.valueOf(data.getBytes().length));
				OutputStream os = urlConn.getOutputStream();
				os.write(data.getBytes());
				os.flush();
			}

			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = urlConn.getInputStream();
				return readInputStream(inputStream);
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
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
