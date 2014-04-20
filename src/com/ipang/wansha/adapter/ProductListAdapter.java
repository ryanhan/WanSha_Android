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
import com.ipang.wansha.model.Product;
import com.ipang.wansha.utils.Const;
import com.ipang.wansha.utils.Utility;

public class ProductListAdapter extends ArrayAdapter<Product> {

	private Context context;
	private LayoutInflater mInflater;

	public final class ViewHolder {
		public ImageView productPreviewImage;
		public TextView productNameTextView;
		public TextView rankingCountTextView;
		public TextView fromPriceTextView;
		public ImageView[] rankingImage;
	}
	
	
	public ProductListAdapter(Context context, List<Product> objects) {
		super(context, 0, objects);
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.adapter_listview_products, null);
			
			holder.productPreviewImage = (ImageView) convertView.findViewById(R.id.image_product_preview);  
            holder.productNameTextView = (TextView) convertView.findViewById(R.id.product_name_text);  
            holder.rankingCountTextView = (TextView) convertView.findViewById(R.id.ranking_count);
            holder.fromPriceTextView = (TextView) convertView.findViewById(R.id.from_price);
            holder.rankingImage = new ImageView[5];
            holder.rankingImage[0] = (ImageView) convertView.findViewById(R.id.product_list_rank1); 
            holder.rankingImage[1] = (ImageView) convertView.findViewById(R.id.product_list_rank2); 
            holder.rankingImage[2] = (ImageView) convertView.findViewById(R.id.product_list_rank3); 
            holder.rankingImage[3] = (ImageView) convertView.findViewById(R.id.product_list_rank4); 
            holder.rankingImage[4] = (ImageView) convertView.findViewById(R.id.product_list_rank5); 
            
            convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.productNameTextView.setText(getItem(position).getProductName());
		holder.rankingCountTextView.setText("(" + getItem(position).getReviewCount() + ")");
		holder.fromPriceTextView.setText(getItem(position).getCurrency().getSymbol() + " " + getItem(position).getPrice());

		if (getItem(position).getProductImages() == null || getItem(position).getProductImages().size() == 0){
			holder.productPreviewImage.setImageResource(R.drawable.missing);
		}
		else {
			int resID = context.getResources().getIdentifier(
					getItem(position).getProductImages().get(0), "drawable",
					Const.PACKAGENAME);
			holder.productPreviewImage.setImageResource(resID);
		}

		float ranking = (float)getItem(position).getReviewTotalRanking() / getItem(position).getReviewCount();
		
		Utility.drawRankingStar(holder.rankingImage, ranking);
		
		return convertView;
	}

}
