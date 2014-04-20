package com.ipang.wansha.dao;

import java.util.ArrayList;

import com.ipang.wansha.model.Review;

public interface ReviewDao {

	public ArrayList<Review> getReviewList(String productId);

	public int[] getRankingDetail(String productId);
	
}
