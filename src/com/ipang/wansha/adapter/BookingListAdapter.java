package com.ipang.wansha.adapter;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.ipang.wansha.model.Booking;

public class BookingListAdapter extends ArrayAdapter<Booking>{

	public BookingListAdapter(Context context, List<Booking> objects) {
		super(context, 0, objects);
	}

	
	
}
