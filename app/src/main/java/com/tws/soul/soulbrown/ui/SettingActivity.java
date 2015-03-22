package com.tws.soul.soulbrown.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

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

        //Button btnPushOn = (Button)findViewById(R.id.setting_push_on_btn);
        LinearLayout llPushStatus =(LinearLayout)findViewById(R.id.setting_push_layout);
        Button btnLogOut = (Button)findViewById(R.id.setting_logout_btn);
        LinearLayout llBackKey =(LinearLayout)findViewById(R.id.setting_back_btn);



        gcmClient = new GcmClient(this);


        if( !AppController.getInstance().getIsUser() )
        {
            // 점주라면.
            llPushStatus.setVisibility(View.VISIBLE);

            ToggleButton toggleButton = (ToggleButton) findViewById(R.id.setting_push_swich);

            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

                    LOG.d("toggleButton isChecked : "+ isChecked);

                    if( isChecked )
                    {
                        if( !mBaseProgressDialog.isShowing() )
                            mBaseProgressDialog.show();

                        setGcmClient();
                    }
                    else
                    {
                        PrefUserInfo prefUserInfo = new PrefUserInfo(SettingActivity.this);
                        String userID = prefUserInfo.getUserID();

                        // 점주

                        apiSetPushKey(userID, "");
                    }

                }
            });

            /*

            btnPushOn.setVisibility(View.VISIBLE);

            btnPushOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if( !mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.show();

                    setGcmClient();

                }
            });
            */
        }
        else {
            llPushStatus.setVisibility(View.GONE);
            //   btnPushOn.setVisibility(View.GONE);
        }
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
                    apiSetPushKey(userID, msg);
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
    public void apiSetPushKey( String storeID, final String regID) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiSetPushKey");

        if (api != null) {

            api.apiSetPushKeyStore(this, storeID, regID ,new Response.Listener<RetCode>() {

                @Override
                public void onResponse(RetCode retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);

                    if (retCode.ret == 1) {

                        // success
                        if(TextUtils.isEmpty(regID)) {
                            setPushStatus(0);
                            Toast.makeText(SettingActivity.this, getString(R.string.push_msg_off), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            setPushStatus(1);
                            gcmClient.savePushKey(regID);
                            Toast.makeText(SettingActivity.this, getString(R.string.push_msg_on), Toast.LENGTH_SHORT).show();

                        }


                    } else {
                        // fail
                        LOG.d("apiSetPushKey Fail " + retCode.ret);

                        Toast.makeText(SettingActivity.this, retCode.msg + "(" + retCode.ret + ")", Toast.LENGTH_SHORT).show();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiSetPushKey VolleyError " + volleyError.getMessage());
                    Toast.makeText(SettingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT).show();

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

        }
        else
        {
            storeID = tmpUserID;

        }

        apiRemovePushKey( userID, storeID);
    }


    // apiRemovePushKey
    public void apiRemovePushKey( String userID, String storeID) {

        final String regID = "";

        ApiAgent api = new ApiAgent();

        LOG.d("apiRemovePushKey");

        if (api != null) {

            api.apiLogout(this, userID, storeID, regID ,new Response.Listener<RetCode>() {

                @Override
                public void onResponse(RetCode retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);

                    if (retCode.ret == 1) {

                        // success

                        gcmClient.savePushKey(regID);

                        PrefUserInfo prefUserInfo = new PrefUserInfo(SettingActivity.this);

                        prefUserInfo.setUserID("");

                        setResult(RESULT_OK);
                        finish();

                    } else {
                        // fail
                        LOG.d("apiRemovePushKey Fail " + retCode.ret);

                        Toast.makeText(SettingActivity.this, retCode.msg + "(" + retCode.ret + ")", Toast.LENGTH_SHORT).show();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiRemovePushKey VolleyError " + volleyError.getMessage());
                    Toast.makeText(SettingActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void setPushStatus(int flag)
    {
        Intent intentGcm = new Intent("push_status");

        intentGcm.putExtra("status", flag);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intentGcm);
    }

}
