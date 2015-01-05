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

			// Open ViewPagerFragment01.java
		case 0:
			ViewPagerFragment01 fragmenttab1ViewPager = new ViewPagerFragment01();
			return fragmenttab1ViewPager;

			// Open ViewPagerFragment02.java
		case 1:
			ViewPagerFragment02 fragmenttab2ViewPager = new ViewPagerFragment02();
			return fragmenttab2ViewPager;

        case 2:
                ViewPagerFragment03 fragmenttab3ViewPager = new ViewPagerFragment03();
                return fragmenttab3ViewPager;
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