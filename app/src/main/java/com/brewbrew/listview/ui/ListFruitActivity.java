package com.brewbrew.listview.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;


import com.brewbrew.listview.domain.OrderList;
import com.brewbrew.listview.adapter.GenericAdapter;
import com.brewbrew.listview.viewmapping.OrderListView;
import com.brewbrew.R;

public class ListFruitActivity extends ListActivity {

	static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
			"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
			"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<OrderListView> lst = new ArrayList<OrderListView>();
		for(int i = 0; i < FRUITS.length; i++){
			OrderListView mv = new OrderListView(new OrderList(FRUITS[i]), R.layout.list_order);
			lst.add(mv);
		}
		setListAdapter(new GenericAdapter(lst, getApplicationContext()));
		

	}

}