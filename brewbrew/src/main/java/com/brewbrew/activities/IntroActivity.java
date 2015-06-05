package com.brewbrew.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.brewbrew.R;
import com.brewbrew.views.adapters.TutorialPagerAdapter;
import com.brewbrew.views.widgets.LoadingDialog;
import com.viewpagerindicator.CirclePageIndicator;

public class IntroActivity extends Activity {

  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


    }
}
