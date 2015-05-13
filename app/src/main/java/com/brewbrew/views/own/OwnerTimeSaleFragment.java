package com.brewbrew.views.own;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brewbrew.R;
import com.brewbrew.network.data.RetMenuList;
import com.brewbrew.views.SoulBrownMainActivity;
import com.brewbrew.views.viewpager.ViewPagerAdapter;

import java.lang.reflect.Field;

public class OwnerTimeSaleFragment extends Fragment {


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


        //mViewPager = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_timesale, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initDataSet();
            }
        }, 300);
        */

    }




    private void initDataSet() {
        // Set the ViewPagerAdapter into ViewPager
        /*
        if( mViewPager != null) {
            mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), mMenuList));

            mViewPager.setOffscreenPageLimit(mMenuList.store.size());

            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {


                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (customListener != null) {
                        customListener.onChangeViewPager(position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            new Handler().postDelayed(new Runnable() {// 0.2 초 후에 실행
                @Override
                public void run() {
                    // 실행할 동작 코딩
                    if (mViewPager != null)
                        mViewPager.setCurrentItem(mPosition, false);
                }
            }, 200);
        }
        */
    }


}
