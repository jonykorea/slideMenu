package com.brewbrew.listview.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.brewbrew.listview.adapter.GenericAdapter;
import com.brewbrew.listview.domain.SideMenu;
import com.brewbrew.listview.viewmapping.SideMenuView;
import com.brewbrew.R;

public class ListNationsActivity extends ListActivity {

	static final String[] NATIONS = new String[] { "Afghanistan", "Albania",
			"Algeria", "Andorra", "Angola", "Argentina", "Armenia",
			"Australia", "Austria", "Azerbaijan", "Bangladesh", "Belgium",
			"Bolivia" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<SideMenuView> lst = new ArrayList<SideMenuView>();
		for (int i = 0; i < NATIONS.length; i++) {
			SideMenuView mv = new SideMenuView(new SideMenu(NATIONS[i],
					R.drawable.push_logo_s), R.layout.list_sidemenu);
			lst.add(mv);
		}
		setListAdapter(new GenericAdapter(lst, getApplicationContext()));

		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(ListNationsActivity.this,
						"position " + position, Toast.LENGTH_SHORT).show();

			}
		});

	}

}