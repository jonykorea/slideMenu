package com.tws.soul.soulbrown.lib;

import android.app.Activity;
import android.widget.Toast;

import com.tws.common.lib.views.CuzToast;
import com.tws.soul.soulbrown.R;

/**
 * Created by Jony on 2015-03-26.
 */
public class BackPressCloseHandler {

    private long mBackKeyPressedTime = 0;
    CuzToast mCuzToast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > mBackKeyPressedTime + 2000) {
            mBackKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= mBackKeyPressedTime + 2000) {

            activity.setResult(Activity.RESULT_OK);
            activity.finish();
            mCuzToast.cancel();
        }
    }


    private void showGuide() {
        mCuzToast = new CuzToast(activity);
        mCuzToast.showToast(activity.getString(R.string.finish),Toast.LENGTH_SHORT);
    }

}