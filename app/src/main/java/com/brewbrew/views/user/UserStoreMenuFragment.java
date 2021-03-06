package com.brewbrew.views.user;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brewbrew.network.data.RetMenuList;
import com.brewbrew.views.SoulBrownMainActivity;
import com.brewbrew.R;
import com.brewbrew.views.viewpager.ViewPagerAdapter;

public class UserStoreMenuFragment extends Fragment {

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

        mMenuList = ((SoulBrownMainActivity)activity).getMenuList();
        customListener = (CustomOnClickListener)activity;
    }
    private int mPosition = 0;

    private RetMenuList mMenuList;

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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


        mViewPager = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.viewpager_main, container, false);
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

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initDataSet();
            }
        }, 300);

    }




    private void initDataSet() {
        // Set the ViewPagerAdapter into ViewPager
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
    }


}
