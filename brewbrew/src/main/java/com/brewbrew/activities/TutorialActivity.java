package com.brewbrew.activities;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.brewbrew.R;
import com.brewbrew.views.adapters.TutorialPagerAdapter;
import com.brewbrew.views.widgets.LoadingDialog;
import com.viewpagerindicator.CirclePageIndicator;

public class TutorialActivity extends Activity {

    // Declare Variables
    ViewPager viewPager;
    PagerAdapter adapter;
    String[] titleColor;
    String[] pageTitle;
    String[] pageText;
    int[] imageRes;
    CirclePageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        titleColor = new String[] { "1", "2", "3", "4"};

        pageTitle = new String[] { "커피는 주문은 브루브루", "커피는 주문은 브루브루", "커피는 주문은 브루브루",
                "커피는 주문은 브루브루" };

        pageText = new String[] { "가고자 하는 커피숍을 선택하세요.",
                "원하는 메뉴를 선택하고 아주쉽게 주문할 수 있습니다.",
                "주문이 접수되고 커피숍에서 만들기 시작합니다.",
                "주문한 음료를 커피숍에서 결제하시고 Take out 하시면 됩니다." };

        imageRes = new int[] { R.drawable.start_01, R.drawable.start_02,
                R.drawable.start_03, R.drawable.start_04};

        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new TutorialPagerAdapter(TutorialActivity.this, titleColor, pageTitle,
                pageText, imageRes);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);

        // ViewPager Indicator
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        //mIndicator.setFades(false);
        mIndicator.setViewPager(viewPager);

        LoadingDialog loadingDialog = new LoadingDialog(TutorialActivity.this);

        loadingDialog.show();

    }
}
