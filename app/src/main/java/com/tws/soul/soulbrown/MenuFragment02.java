package com.tws.soul.soulbrown;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuFragment02 extends Fragment {

    // Activity 로 데이터를 전달할 커스텀 리스너
    private CustomOnClickListener customListener;

    // Activity 로 데이터를 전달할 커스텀 리스너의 인터페이스
    public interface CustomOnClickListener{
        public void onChangeViewPager(int position);
    }
    // Activity 로 데이터를 전달할 커스텀 리스너를 연결
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        customListener = (CustomOnClickListener)activity;
    }
    private int mPosition = 0;

    public void setPosition(int position)
    {
        if( mViewPager == null)
        {
            mPosition = position;
        }
        else
        {
            mViewPager.setCurrentItem(position,false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("jony", "onDestroy MenuFragment02 ");

        mViewPager = null;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.i("jony", "onDestroyView MenuFragment02 ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("jony", "onCreate MenuFragment02 ");
    }
    ViewPager mViewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("jony", "onCreateView MenuFragment02 mPosition "+ mPosition);

        View view = inflater.inflate(R.layout.viewpager_main, container, false);
        // Locate the ViewPager in viewpager_main.xml
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        // Set the ViewPagerAdapter into ViewPager
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if( customListener != null)
                {
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

                mViewPager.setCurrentItem(mPosition, false);
            }
        }, 200);


        return view;
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
}
