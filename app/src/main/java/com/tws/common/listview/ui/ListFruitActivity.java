package com.tws.common.listview.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;


import com.tws.common.listview.adapter.GenericAdapter;
import com.tws.common.listview.domain.OrderList;
import com.tws.common.listview.viewmapping.OrderListView;
import com.tws.soul.soulbrown.R;

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