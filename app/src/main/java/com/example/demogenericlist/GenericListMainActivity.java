package com.example.demogenericlist;

import com.tws.common.listview.ui.ListFruitActivity;
import com.tws.common.listview.ui.ListNationsActivity;
import com.tws.soul.soulbrown.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GenericListMainActivity extends Activity {

	public Button mBtn01;
	public Button mBtn02;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic_list_main);
		
		mBtn01 = (Button)findViewById(R.id.button1);
		mBtn01.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GenericListMainActivity.this, ListFruitActivity.class);
				startActivity(intent);
			}
		});
		
		mBtn02 = (Button)findViewById(R.id.button2);
		mBtn02.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GenericListMainActivity.this, ListNationsActivity.class);
				startActivity(intent);
				
			}
		});

	}

}
