package com.app;

import android.app.Application;

import com.flurry.android.FlurryAgent;
import com.brewbrew.flurry.Define;
import com.brewbrew.managers.pref.PrefUserInfo;

public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // configure Flurry
        FlurryAgent.setLogEnabled(false);

        FlurryAgent.setCaptureUncaughtExceptions(true);

        // init Flurry
        FlurryAgent.init(this, Define.FLURRY_KEY);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }


    // soulbrown
    public boolean getIsUser()
    {
        PrefUserInfo prefUserInfo = new PrefUserInfo(this);

        return prefUserInfo.getUserType();
    }
}
