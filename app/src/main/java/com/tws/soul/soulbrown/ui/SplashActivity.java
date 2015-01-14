package com.tws.soul.soulbrown.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.tws.network.data.ExtraType;
import com.tws.network.data.RetCode;
import com.tws.network.data.RetUserChecker;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.SoulBrownMainActivity;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

/**
 * Created by jonychoi on 15. 1. 14..
 */
public class SplashActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        context = getApplicationContext();

        // pref 확인 > 로그인 처리.

        PrefUserInfo prefUserInfo = new PrefUserInfo(context);

        String userID = prefUserInfo.getUserID();

        userID = "jony@thinkware.co.kr";

        if(TextUtils.isEmpty(userID))
        {
            // 없으면 입력창 노출.
        }
        else
        {
            // 있다면 api 호출.
            apiUserChecker(userID);

        }


    }


    // apiUserChecker
    public void apiUserChecker(String userID) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiUserChecker");

        if (api != null) {
            api.apiUserChecker(this, userID, new Response.Listener<RetUserChecker>() {

                @Override
                public void onResponse(RetUserChecker retCode) {

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);
                    LOG.d("retCode.usertype : " + retCode.usertype);


                    if (retCode.result == 1) {

                        // success

                        LOG.d("apiSetUserLoc Succ");

                        Intent intent = new Intent(context, SoulBrownMainActivity.class);
                        intent.putExtra(ExtraType.USER_TYPE,retCode.usertype);
                        startActivity(intent);


                    } else {
                        // fail
                        LOG.d("apiSetUserLoc Fail " + retCode.result);

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    LOG.d("apiSetUserLoc VolleyError " + volleyError.getMessage());

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
