package com.tws.soul.soulbrown;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	// Declare the number of ViewPager pages

	private String titles[] = new String[] { "Tab1", "Tab2", "Tab3" };

    final int PAGE_COUNT = titles.length;

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {

		case 0:
			ViewPagerFragment01 storeMenuFragment01 = new ViewPagerFragment01();
			return storeMenuFragment01;

		case 1:
			ViewPagerFragment02 storeMenuFragment02 = new ViewPagerFragment02();
			return storeMenuFragment02;

        case 2:
                ViewPagerFragment03 storeMenuFragment03 = new ViewPagerFragment03();
                return storeMenuFragment03;
		}

    		return null;
	}

	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

}