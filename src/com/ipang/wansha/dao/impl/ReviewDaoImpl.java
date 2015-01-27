package com.ipang.wansha.dao.impl;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipang.wansha.dao.ReviewDao;
import com.ipang.wansha.model.Review;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.HttpUtility;
import com.ipang.wansha.utils.Utility;

public class ReviewDaoImpl implements ReviewDao {

	public ArrayList<Review> getReviewList(int productId)
			throws MalformedURLException, InterruptedException,
			ExecutionException, JSONException, ParseException {

		ArrayList<Review> reviews = new ArrayList<Review>();
		URL url = new URL(Const.SERVERNAME + "/rest/review?product="
				+ productId);
		String result = HttpUtility.GetJson(url);
		if (result == null)
			return null;

		JSONObject jsonObject = new JSONObject(result);

		if (jsonObject.isNull("review"))
			return null;

		try {
			JSONArray reviewList = jsonObject.getJSONArray("review");
			for (int i = 0; i < reviewList.length(); i++) {
				JSONObject reviewJson = reviewList.getJSONObject(i);
				reviews.add(createReview(reviewJson));
			}
		} catch (JSONException e) {
			JSONObject json = jsonObject.getJSONObject("review");
			reviews.add(createReview(json));
		}

		return reviews;
	}

	@Override
	public int[] getRankingDetail(String productId) throws MalformedURLException,
			InterruptedException, ExecutionException, JSONException {
		int[] ranking = new int[5];
		URL url = new URL(Const.SERVERNAME + "/rest/product/" + productId
				+ "/ranking");
		String result = HttpUtility.GetJson(url);

		if (result == null)
			return null;

		JSONObject jsonObject = new JSONObject(result);

		if (jsonObject.isNull("ranking"))
			return null;

		JSONArray rankingList = jsonObject.getJSONArray("ranking");
		for (int i = 0; i < rankingList.length(); i++) {
			JSONObject rankingJson = rankingList.getJSONObject(i);
			int rankingLevel = rankingJson.getInt("ranking");
			int rankingCount = rankingJson.getInt("rankingCount");
			ranking[5 - rankingLevel] = rankingCount;
		}

		return ranking;
	}

	private Review createReview(JSONObject json) throws JSONException,
			ParseException {
		Review review = new Review();
		review.setReviewId(json.getString("reviewId"));
		review.setReviewTitle(json.getString("reviewTitle"));
		review.setReviewContent(json.getString("reviewContent"));
		review.setRanking(json.getInt("ranking"));
		review.setReviewDate(Utility.FormatString(json.getString("reviewDate")));
		return review;
	}
}
