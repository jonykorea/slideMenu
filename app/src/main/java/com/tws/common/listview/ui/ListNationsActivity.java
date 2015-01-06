package com.tws.common.listview.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.tws.common.listview.adapter.GenericAdapter;
import com.tws.common.listview.domain.Nations;
import com.tws.common.listview.viewmapping.NationsView;
import com.tws.soul.soulbrown.R;

public class ListNationsActivity extends ListActivity {

	static final String[] NATIONS = new String[] { "Afghanistan", "Albania",
			"Algeria", "Andorra", "Angola", "Argentina", "Armenia",
			"Australia", "Austria", "Azerbaijan", "Bangladesh", "Belgium",
			"Bolivia" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<NationsView> lst = new ArrayList<NationsView>();
		for (int i = 0; i < NATIONS.length; i++) {
			NationsView mv = new NationsView(new Nations(NATIONS[i],
					R.drawable.ic_launcher), R.layout.list_nations);
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