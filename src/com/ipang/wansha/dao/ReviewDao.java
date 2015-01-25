package com.ipang.wansha.dao;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.ipang.wansha.model.Review;

public interface ReviewDao {

	public ArrayList<Review> getReviewList(int productId) throws MalformedURLException, InterruptedException, ExecutionException, JSONException, ParseException;

	public int[] getRankingDetail(String productId) throws MalformedURLException, InterruptedException, ExecutionException, JSONException;
	
}
