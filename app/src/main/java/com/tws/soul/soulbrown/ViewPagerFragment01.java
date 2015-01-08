package com.tws.soul.soulbrown;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ViewPagerFragment01 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragment_viewpager_01.xml
        View view = inflater.inflate(R.layout.fragment_viewpager_01, container, false);
        return view;
    }
}
