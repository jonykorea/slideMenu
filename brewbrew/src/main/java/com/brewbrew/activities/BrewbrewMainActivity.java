package com.brewbrew.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.brewbrew.R;
import com.brewbrew.fragments.MainViewFragment;
import com.brewbrew.views.adapters.TitlePagerAdapter;
import com.brewbrew.views.layout.CuzPagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;


public class BrewbrewMainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brewbrew_main);


        List<Fragment> fragments = new ArrayList<Fragment>();

        // Fragement setting
        fragments.add(MainViewFragment.newInstance(R.layout.fragment_tab_store));
        fragments.add(MainViewFragment.newInstance(R.layout.fragment_tab_list));
        fragments.add(MainViewFragment.newInstance(R.layout.fragment_tab_setting));


        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TitlePagerAdapter(getSupportFragmentManager(), fragments));
        CuzPagerSlidingTabStrip tabStrip = (CuzPagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(pager);
    }


}
