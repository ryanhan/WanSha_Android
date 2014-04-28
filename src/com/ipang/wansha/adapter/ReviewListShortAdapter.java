package com.ipang.wansha.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipang.wansha.R;
import com.ipang.wansha.model.Review;
import com.ipang.wansha.utils.Utility;

public class ReviewListShortAdapter extends ArrayAdapter<Review> {

	private LayoutInflater mInflater;

	public final class ViewHolder {
		public TextView reviewTitle;
		public TextView reviewContent;
		public TextView reviewDate;
		public ImageView[] reviewStar;
	}
	
	public ReviewListShortAdapter(Context context, List<Review> objects) {
		super(context, 0, objects);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_listview_review, null);
			
			holder.reviewTitle = (TextView) convertView.findViewById(R.id.review_list_title);  
            holder.reviewContent = (TextView) convertView.findViewById(R.id.review_list_content);  
            holder.reviewDate = (TextView) convertView.findViewById(R.id.review_list_date);
            holder.reviewStar = new ImageView[5];
            holder.reviewStar[0] = (ImageView) convertView.findViewById(R.id.review_list_rank1);
            holder.reviewStar[1] = (ImageView) convertView.findViewById(R.id.review_list_rank2);
            holder.reviewStar[2] = (ImageView) convertView.findViewById(R.id.review_list_rank3);
            holder.reviewStar[3] = (ImageView) convertView.findViewById(R.id.review_list_rank4);
            holder.reviewStar[4] = (ImageView) convertView.findViewById(R.id.review_list_rank5);

            convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.reviewTitle.setText(getItem(position).getReviewTitle());
		holder.reviewContent.setText(getItem(position).getReviewContent());
		holder.reviewDate.setText(Utility.FormatFullDate(getItem(position).getReviewDate()));
		int ranking = getItem(position).getRanking();
		for(int i = 0; i < ranking; i++){
			holder.reviewStar[i].setBackgroundResource(R.drawable.star_full);
		}
		
		return convertView;
	}

}