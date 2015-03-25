package com.tws.soul.soulbrown.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tws.common.lib.dialog.CuzDialog;
import com.tws.network.data.RetCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.base.BaseActivity;
import com.tws.soul.soulbrown.gcm.GcmClient;
import com.tws.soul.soulbrown.pref.PrefStoreInfo;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingActivity extends BaseActivity {

    private Context context;
    private GcmClient gcmClient;

    private final String INFO_URL= "http://coffeebrewbrew.blogspot.kr/2015/03/close-beta.html";
    private final String EVENT_URL= "https://www.facebook.com/coffeebrewbrew/posts/856353921103968";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        context = getApplicationContext();

        //Button btnPushOn = (Button)findViewById(R.id.setting_push_on_btn);
        LinearLayout llPushStatus =(LinearLayout)findViewById(R.id.setting_push_layout);
        Button btnLogOut = (Button)findViewById(R.id.setting_logout_btn);
        LinearLayout llBackKey =(LinearLayout)findViewById(R.id.setting_back_btn);

        Button btnInfo = (Button)findViewById(R.id.setting_webview_info_btn);
        Button btnEvent = (Button)findViewById(R.id.setting_webview_event_btn);


        gcmClient = new GcmClient(this);


        if( !AppController.getInstance().getIsUser() )
        {
            // 점주라면.
            llPushStatus.setVisibility(View.VISIBLE);

            ToggleButton toggleButton = (ToggleButton) findViewById(R.id.setting_push_swich);

            PrefStoreInfo prefStoreInfo = new PrefStoreInfo(context);
            int status = prefStoreInfo.getPushStatus();

            if( status == 1 )
            {
                toggleButton.setChecked(true);
            }
            else
            {
                toggleButton.setChecked(false);
            }

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

                if(mBaseDialog == null || !mBaseDialog.isShowing()) {

                    mBaseDialog = new CuzDialog(SettingActivity.this,
                            getString(R.string.confirm), getString(R.string.setting_logout_action));

                    mBaseDialog.show();

                    mBaseDialog.setCancelable(true);

                    mBaseDialog.getButtonAccept().setText(getString(R.string.confirm));

                    mBaseDialog.getButtonCancel().setText(getString(R.string.cancel));

                    mBaseDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mBaseDialog.dismiss();
                            // api
                            if( !mBaseProgressDialog.isShowing() )
                                mBaseProgressDialog.show();

                            logoutAction();

                        }
                    });
                }


            }
        });

        llBackKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // webview S

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mBaseDialog == null || !mBaseDialog.isShowing()) {

                    mBaseDialog = new CuzDialog(SettingActivity.this,
                            getString(R.string.confirm), getString(R.string.setting_info) +" "+getString(R.string.setting_mv_browser));

                    mBaseDialog.show();

                    mBaseDialog.setCancelable(true);

                    mBaseDialog.getButtonAccept().setText(getString(R.string.confirm));

                    mBaseDialog.getButtonCancel().setText(getString(R.string.cancel));

                    mBaseDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mBaseDialog.dismiss();
                            // api
                            callBrowser(INFO_URL);

                        }
                    });
                }



            }
        });

        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mBaseDialog == null || !mBaseDialog.isShowing()) {

                    mBaseDialog = new CuzDialog(SettingActivity.this,
                            getString(R.string.confirm), getString(R.string.setting_event) +" "+getString(R.string.setting_mv_browser));

                    mBaseDialog.show();

                    mBaseDialog.setCancelable(true);

                    mBaseDialog.getButtonAccept().setText(getString(R.string.confirm));

                    mBaseDialog.getButtonCancel().setText(getString(R.string.cancel));

                    mBaseDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mBaseDialog.dismiss();
                            // api
                            callBrowser(EVENT_URL);

                        }
                    });
                }

            }
        });
        // webview E

    }

    private void callBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
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

    private void setPushStatus(int status)
    {
        Intent intentGcm = new Intent("push_status");

        intentGcm.putExtra("status", status);

        PrefStoreInfo prefStoreInfo = new PrefStoreInfo(context);
        prefStoreInfo.setPushStatus(status);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intentGcm);
    }

    // flurry
    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
