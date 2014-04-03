package com.ipang.wansha.dao.impl;

import java.util.ArrayList;
import java.util.Date;

import com.ipang.wansha.dao.ReviewDao;
import com.ipang.wansha.model.Review;

public class ReviewDaoImpl implements ReviewDao {

	public ArrayList<Review> getReviewList(String productId) {
		ArrayList<Review> reviews = new ArrayList<Review>();
		Review review = new Review();
		review.setRanking(5);
		review.setReviewTitle("Perfect!");
		review.setReviewContent("Very Good!");
		review.setReviewDate(new Date());
		reviews.add(review);
		Review review1 = new Review();
		review1.setRanking(3);
		review1.setReviewTitle("Good!");
		review1.setReviewContent("Well place!");
		review1.setReviewDate(new Date());
		reviews.add(review1);
		Review review2 = new Review();
		review2.setRanking(1);
		review2.setReviewTitle("Bad!");
		review2.setReviewContent("Not recommended!");
		review2.setReviewDate(new Date());
		reviews.add(review2);
		return reviews;
	}

}
