package com.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.volley.VolleyUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.flurry.android.FlurryAgent;
import com.tws.soul.soulbrown.flurry.Define;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.io.InputStream;

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
