package com.ipang.wansha.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ipang.wansha.R;
import com.ipang.wansha.adapter.SortTypeAdapter;
import com.ipang.wansha.enums.SortType;
import com.ipang.wansha.utils.Const;

public class SortListFragment extends Fragment {

	public interface OnSortTypeChangedListener {
		public void onSortTypeChanged(SortType sortType);
	}

	private SortType sortType;
	private OnSortTypeChangedListener mListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		sortType = SortType.fromIndex(args.getInt(Const.SORTTYPE));
		System.out.println(sortType);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSortTypeChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sort_list, null);

		ListView sortList = (ListView) view.findViewById(R.id.sort_list);
		final String[] sortTypeList = new String[] {
				getString(R.string.sort_default),
				getString(R.string.sort_price_up),
				getString(R.string.sort_comment),
				getString(R.string.sort_popularity),
				getString(R.string.sort_time_long),
				getString(R.string.sort_time_short),
				getString(R.string.sort_discount) };

		SortTypeAdapter sortAdapter = new SortTypeAdapter(
				this.getActivity(), sortTypeList, sortType);
		sortList.setAdapter(sortAdapter);
		sortList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mListener.onSortTypeChanged(SortType.fromIndex(position));
			}
		});
		
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.fragment_sort_layout);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onSortTypeChanged(sortType);
			}
		});

		return view;
	}
	

}
