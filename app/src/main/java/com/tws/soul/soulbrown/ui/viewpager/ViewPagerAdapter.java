package com.tws.soul.soulbrown.ui.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tws.soul.soulbrown.lib.StoreInfo;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    // Declare the number of ViewPager pages

    private String titles[] = new String[]{"store1", "store2", "store3","store4"};
    final int PAGE_COUNT = titles.length;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();

        switch (position) {

            case 0:
                bundle.putString("store", StoreInfo.CODE_HARU);

                StoreMenuFragment storeMenuFragment01 = new StoreMenuFragment();

                storeMenuFragment01.setArguments(bundle);

                return storeMenuFragment01;

            case 1:
                bundle.putString("store", StoreInfo.CODE_1022);

                StoreMenuFragment storeMenuFragment02 = new StoreMenuFragment();

                storeMenuFragment02.setArguments(bundle);

                return storeMenuFragment02;

            //StoreFragment02 storeMenuFragment02 = new StoreFragment02();
            //return storeMenuFragment02;

            case 2:
                bundle.putString("store", StoreInfo.CODE_2FLAT);

                StoreMenuFragment storeMenuFragment03 = new StoreMenuFragment();

                storeMenuFragment03.setArguments(bundle);

                return storeMenuFragment03;
            //StoreFragment03 storeMenuFragment03 = new StoreFragment03();
            //return storeMenuFragment03;

            case 3:
                bundle.putString("store", StoreInfo.CODE_TWS);

                StoreMenuFragment storeMenuFragment04 = new StoreMenuFragment();

                storeMenuFragment04.setArguments(bundle);

                return storeMenuFragment04;
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