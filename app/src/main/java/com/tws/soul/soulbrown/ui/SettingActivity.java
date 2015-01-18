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
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingActivity extends Activity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        context = getApplicationContext();

        Button btnPushOn = (Button)findViewById(R.id.setting_push_on_btn);
        Button btnLogOut = (Button)findViewById(R.id.setting_logout_btn);
        LinearLayout llBackKey =(LinearLayout)findViewById(R.id.setting_back_btn);


        if( !AppController.getInstance().getIsUser() )
        {
            // 점주라면.

            btnPushOn.setVisibility(View.VISIBLE);

            btnPushOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   initGCM();

                }
            });
        }
        else
            btnPushOn.setVisibility(View.GONE);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    public static final String SENDER_ID = "137338214881";

    /**
     * Tag used on log messages.
     */

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid;

    private void initGCM()
    {
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);

            LOG.d("regid : " + regid);

            if (regid.isEmpty()) {
                registerInBackground();
            }
            else
            {
                PrefUserInfo prefUserInfo = new PrefUserInfo(this);
                String userID = prefUserInfo.getUserID();

                // 점주
                apiSetPushKey("STOREUI", userID, regid);
            }
        } else {
            LOG.d("GCM No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                LOG.d("GCM This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        LOG.d("GCM Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            LOG.d("GCM Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            LOG.d("GCM App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend(regid);

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.


                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    // Send an upstream message.
    /*
    public void onClick(final View view) {

        if (view == findViewById(R.id.send)) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";
                    try {
                        Bundle data = new Bundle();
                        data.putString("my_message", "Hello World");
                        data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
                        String id = Integer.toString(msgId.incrementAndGet());
                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                        msg = "Sent message";
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                    return msg;
                }

                @Override
                protected void onPostExecute(String msg) {
                    mDisplay.append(msg + "\n");
                }
            }.execute(null, null, null);
        } else if (view == findViewById(R.id.clear)) {
            mDisplay.setText("");
        }
    }
    */

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(SoulBrownMainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String regID) {
        // Your implementation here.

        if(!AppController.getInstance().getIsUser())
        {
            PrefUserInfo prefUserInfo = new PrefUserInfo(this);
            String userID = prefUserInfo.getUserID();

            // 점주
            apiSetPushKey("STOREUI", userID, regID);

        }
    }

    // gcm E

    // apiSetPushKey
    public void apiSetPushKey(String source, String storeID, final String regID) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiSetPushKey");

        if (api != null) {
            api.apiSetPushKey(this, source, null, storeID, regID, "on" ,new Response.Listener<RetCode>() {

                @Override
                public void onResponse(RetCode retCode) {

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);

                    if (retCode.result == 1) {

                        // success

                        storeRegistrationId(context, regID);

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
            api.apiSetPushKey(this, source, userID, storeID, regID, "on" ,new Response.Listener<RetCode>() {

                @Override
                public void onResponse(RetCode retCode) {

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);

                    if (retCode.result == 1) {

                        // success

                        storeRegistrationId(context, regID);

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

                    LOG.d("apiRemovePushKey VolleyError " + volleyError.getMessage());
                    Toast.makeText(SettingActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}
