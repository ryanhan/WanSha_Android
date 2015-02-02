package com.ipang.wansha.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.ipang.wansha.exception.HttpException;

public class HttpUtility {

	public static String GetJson(URL url) throws HttpException {
		return GetJson(url, null);
	}

	public static String GetJson(URL url, String JSessionId)
			throws HttpException {

		HttpURLConnection urlConn = null;
		try {
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.addRequestProperty("accept", "application/json");
			urlConn.setConnectTimeout(Const.CONNECT_TIMEOUT);
			urlConn.setReadTimeout(Const.READ_TIMEOUT);
			if (JSessionId != null)
				urlConn.setRequestProperty("Cookie", Const.JSESSIONID + "="
						+ JSessionId);

			int responseCode = urlConn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				return readInputStream(urlConn);
			} else {
				throw new HttpException(HttpException.HTTP_RESPONSE_ERROR);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpException(HttpException.HOST_CONNECT_FAILED);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}

	public static String PostParam(URL url, HashMap<String, String> postParam)
			throws HttpException {
		return PostParam(url, postParam, null);
	}

	public static String PostParam(URL url, HashMap<String, String> postParam,
			String JSessionId) throws HttpException {

		HttpURLConnection urlConn = null;
		try {
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setConnectTimeout(Const.CONNECT_TIMEOUT);
			urlConn.setReadTimeout(Const.READ_TIMEOUT);
			if (JSessionId != null)
				urlConn.setRequestProperty("Cookie", Const.JSESSIONID + "="
						+ JSessionId);

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
				return readInputStream(urlConn);
			} else {
				throw new HttpException(HttpException.HTTP_RESPONSE_ERROR);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpException(HttpException.HOST_CONNECT_FAILED);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}

	public static String PostJson(URL url, JSONObject json)
			throws HttpException {
		return PostJson(url, json, null);
	}

	public static String PostJson(URL url, JSONObject json, String JSessionId)
			throws HttpException {

		HttpURLConnection urlConn = null;
		try {
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("POST");
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setConnectTimeout(Const.CONNECT_TIMEOUT);
			urlConn.setReadTimeout(Const.READ_TIMEOUT);
			if (JSessionId != null)
				urlConn.setRequestProperty("Cookie", Const.JSESSIONID + "="
						+ JSessionId);

			if (json != null) {
				String data = json.toString();
				urlConn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
				urlConn.setRequestProperty("Content-Length",
						String.valueOf(data.getBytes().length));
				urlConn.setRequestProperty("Accept", "application/json");
				OutputStream os = urlConn.getOutputStream();
				os.write(data.getBytes("UTF-8"));
				os.flush();
				os.close();
			}
			System.out.println("PostJson Response Code: "
					+ urlConn.getResponseCode());
			System.out.println("PostJson Response Content: "
					+ readInputStream(urlConn));

			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return readInputStream(urlConn);
			} else {
				throw new HttpException(HttpException.HTTP_RESPONSE_ERROR);
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpException(HttpException.HOST_CONNECT_FAILED);
		} finally {
			if (urlConn != null) {
				urlConn.disconnect();
			}
		}
	}

	private static String readInputStream(HttpURLConnection urlConn)
			throws IOException {
		Charset charset = Charset.forName("UTF-8");
		InputStreamReader stream = new InputStreamReader(
				urlConn.getInputStream(), charset);
		BufferedReader reader = new BufferedReader(stream);
		StringBuffer responseBuffer = new StringBuffer();
		String read = "";
		while ((read = reader.readLine()) != null) {
			responseBuffer.append(read);
		}
		return responseBuffer.toString();
	}

	public static String getJSessionId() throws IOException {
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
		URL url = new URL(Const.SERVERNAME);
		URLConnection connection = url.openConnection();
		connection.getContent();
		CookieStore cookieJar = manager.getCookieStore();
		List<HttpCookie> cookies = cookieJar.getCookies();
		for (HttpCookie cookie : cookies) {
			if (cookie.getName().equals("JSESSIONID")) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
