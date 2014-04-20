package com.ipang.wansha.activity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.JsonGetAsyncTask;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		TextView text = (TextView) findViewById(R.id.test_text);

		URI uri = null;
		try {
			uri = new URI(Const.SERVERNAME
					+ "/rest/city/8a80808945701adb0145701c6c090000/product");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JsonGetAsyncTask asyncTask = new JsonGetAsyncTask();
		String result = null;
		try {
			result = asyncTask.execute(uri).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Object json = new JSONTokener(result).nextValue();
			if (json instanceof JSONObject){
				text.setText("Object");
			}
			else if (json instanceof JSONArray){
				text.setText("Array");
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
