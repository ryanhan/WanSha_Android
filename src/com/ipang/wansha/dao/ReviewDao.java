package com.ipang.wansha.dao;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.ipang.wansha.model.Review;

public interface ReviewDao {

	public ArrayList<Review> getReviewList(String productId) throws URISyntaxException, InterruptedException, ExecutionException, JSONException, ParseException;

	public int[] getRankingDetail(String productId) throws URISyntaxException, InterruptedException, ExecutionException, JSONException;
	
}
