package com.tws.soul.soulbrown;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerFragment02 extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from fragment_viewpager_02.xml
		View view = inflater.inflate(R.layout.fragment_viewpager_02, container, false);
		return view;
	}
}
