package com.brewbrew.views.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.brewbrew.network.data.ArrayStoreData;
import com.brewbrew.network.data.RetMenuList;

public class ViewPagerAdapter extends FragmentPagerAdapter{

    // Declare the number of ViewPager pages

    //private String titles[] = new String[]{"store1", "store2"};
    //int PAGE_COUNT = titles.length;
    private RetMenuList mMenuList;
    private StoreMenuFragment.CuzOnMoveListener listener;

    public ViewPagerAdapter(FragmentManager fm, RetMenuList menuList) {
        super(fm);
        this.mMenuList = menuList;


    }




    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();

        ArrayStoreData storeInfo = mMenuList.store.get(position);

        String id = storeInfo.store;
        String name = storeInfo.storename;
        String addr = storeInfo.addr;

        bundle.putString("store_id", id);
        bundle.putString("store_name", name);
        bundle.putString("store_addr", addr);

        bundle.putParcelable("menu",storeInfo);
        StoreMenuFragment storeMenuFragment = new StoreMenuFragment();

        storeMenuFragment.setArguments(bundle);

        return storeMenuFragment;

        /*
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
        */
    }

    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getCount() {
        return mMenuList.store.size();
    }


}