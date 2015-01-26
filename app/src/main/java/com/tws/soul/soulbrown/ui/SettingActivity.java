package com.tws.soul.soulbrown.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.AppController;
import com.app.define.LOG;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tws.network.data.RetCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.base.BaseActivity;
import com.tws.soul.soulbrown.gcm.GcmClient;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingActivity extends BaseActivity {

    private Context context;
    private GcmClient gcmClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        context = getApplicationContext();

        Button btnPushOn = (Button)findViewById(R.id.setting_push_on_btn);
        Button btnLogOut = (Button)findViewById(R.id.setting_logout_btn);
        LinearLayout llBackKey =(LinearLayout)findViewById(R.id.setting_back_btn);

        gcmClient = new GcmClient(this);


        if( !AppController.getInstance().getIsUser() )
        {
            // 점주라면.

            btnPushOn.setVisibility(View.VISIBLE);

            btnPushOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( !mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.show();

                    setGcmClient();

                }
            });
        }
        else
            btnPushOn.setVisibility(View.GONE);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( !mBaseProgressDialog.isShowing() )
                    mBaseProgressDialog.show();

                logoutAction();
            }
        });

        llBackKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    // gcm S
    private void setGcmClient()
    {
        gcmClient.initGCM( new GcmClient.onGcmClientListener() {
            @Override
            public void onGcmClientResult(int result, String msg) {

                LOG.d("onGcmClientResult : "+ result + " msg : "+ msg);

                if( result == GcmClient.RET_CODE_SUCC || result == GcmClient.RET_CODE_EXIST)
                {
                    PrefUserInfo prefUserInfo = new PrefUserInfo(SettingActivity.this);
                    String userID = prefUserInfo.getUserID();

                    // 점주
                    apiSetPushKey("STOREUI", userID, msg);
                }
                else
                {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    //Toast.makeText(SettingActivity.this,"Google Play Store Error : "+ msg,Toast.LENGTH_SHORT).show();
                    //finish();
                }

            }
        });



    }
    // gcm E


    // apiSetPushKey
    public void apiSetPushKey(String source, String storeID, final String regID) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiSetPushKey");

        if (api != null) {

            api.apiSetPushKey(this, source, null, storeID, regID ,new Response.Listener<RetCode>() {

                @Override
                public void onResponse(RetCode retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);

                    if (retCode.result == 1) {


                        // success

                        gcmClient.savePushKey(regID);

                        Toast.makeText(SettingActivity.this, "Push Message 등록되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        // fail
                        LOG.d("apiSetPushKey Fail " + retCode.result);

                        Toast.makeText(SettingActivity.this, retCode.errormsg + "(" + retCode.result + ")", Toast.LENGTH_SHORT).show();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiSetPushKey VolleyError " + volleyError.getMessage());
                    Toast.makeText(SettingActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    // logout
    private void logoutAction()
    {
        String userID = null;
        String storeID = null;
        String source = "";

        PrefUserInfo prefUserInfo = new PrefUserInfo(context);

        String tmpUserID = prefUserInfo.getUserID();

        if(AppController.getInstance().getIsUser())
        {
            userID = tmpUserID;
            source = "USERUI";
        }
        else
        {
            storeID = tmpUserID;
            source = "STOREUI";
        }

        apiRemovePushKey(source, userID, storeID);
    }


    // apiRemovePushKey
    public void apiRemovePushKey(String source, String userID, String storeID) {

        final String regID = "";

        ApiAgent api = new ApiAgent();

        LOG.d("apiRemovePushKey");

        if (api != null) {

            api.apiSetPushKey(this, source, userID, storeID, regID ,new Response.Listener<RetCode>() {

                @Override
                public void onResponse(RetCode retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);

                    if (retCode.result == 1) {

                        // success

                        gcmClient.savePushKey(regID);

                        PrefUserInfo prefUserInfo = new PrefUserInfo(SettingActivity.this);

                        prefUserInfo.setUserID("");

                        setResult(RESULT_OK);
                        finish();

                    } else {
                        // fail
                        LOG.d("apiRemovePushKey Fail " + retCode.result);

                        Toast.makeText(SettingActivity.this, retCode.errormsg + "(" + retCode.result + ")", Toast.LENGTH_SHORT).show();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiRemovePushKey VolleyError " + volleyError.getMessage());
                    Toast.makeText(SettingActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}
