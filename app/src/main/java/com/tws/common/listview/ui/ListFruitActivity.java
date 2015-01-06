package com.tws.common.listview.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;


import com.tws.common.listview.adapter.GenericAdapter;
import com.tws.common.listview.domain.Fruit;
import com.tws.common.listview.viewmapping.FruitView;
import com.tws.soul.soulbrown.R;

public class ListFruitActivity extends ListActivity {

	static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
			"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
			"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<FruitView> lst = new ArrayList<FruitView>();
		for(int i = 0; i < FRUITS.length; i++){
			FruitView mv = new FruitView(new Fruit(FRUITS[i]), R.layout.list_fruit);
			lst.add(mv);
		}
		setListAdapter(new GenericAdapter(lst, getApplicationContext()));
		

	}

}