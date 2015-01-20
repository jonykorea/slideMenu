package com.tws.soul.soulbrown.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.app.AppController;
import com.app.define.LOG;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tws.soul.soulbrown.ui.SplashActivity;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/* sample source.

// gcm S
    GcmClient gcmClient;

    private void setGcmClient()
    {
        gcmClient = new GcmClient(this);

        gcmClient.initGCM( new GcmClient.onGcmClientListener() {
            @Override
            public void onGcmClientResult(int result, String msg) {

                LOG.d("onGcmClientResult : "+ result + " msg : "+ msg);

                if( result == GcmClient.RET_CODE_SUCC)
                {
                    PrefUserInfo prefUserInfo = new PrefUserInfo(SoulBrownMainActivity.this);
                    String userID = prefUserInfo.getUserID();

                    // 사용자
                    apiSetPushKey("USERUI", userID, msg);
                }
                else
                {

                }

            }
        });

    }
    // gcm E

 */
/**
 * Created by jonychoi on 15. 1. 19..
 */
public class GcmClient {

    public static int RET_CODE_SUCC = 1;
    public static int RET_CODE_FAIL = -1;

    public static int RET_CODE_EXIST = 2;

    private static int SEND_REGID = 100;

    // 커스텀 리스너
    private onGcmClientListener customListener;

    // 커스텀 리스너의 인터페이스
    public interface onGcmClientListener {
        public void onGcmClientResult(int result, String msg);
    }

    private Context context;

    public GcmClient(Context context) {

        this.context = context;

    }

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

    public void initGCM(onGcmClientListener customListener)
    {
        this.customListener = customListener;

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);

            LOG.d("regid : " + regid);

            if (regid.isEmpty()) {
                registerInBackground();
            }
            else
            {
                this.customListener.onGcmClientResult(RET_CODE_EXIST, regid);
            }
        } else {

            this.customListener.onGcmClientResult(RET_CODE_FAIL, "GCM No valid Google Play Services APK found.");
            LOG.d("GCM No valid Google Play Services APK found.");
        }
    }

    public void savePushKey(String pushKey)
    {
        storeRegistrationId(context,pushKey);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                this.customListener.onGcmClientResult(RET_CODE_FAIL, "isUserRecoverableError : "+resultCode);
                //GooglePlayServicesUtil.getErrorDialog(resultCode, context,
                //        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {

                this.customListener.onGcmClientResult(RET_CODE_FAIL, "GCM This device is not supported.");

                LOG.d("GCM This device is not supported.");
                //finish();
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
        return context.getSharedPreferences(SplashActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String regID) {
        // Your implementation here.


        Message msg = Message.obtain();
        msg.what = SEND_REGID;
        msg.obj = regID;

        gcmHanlder.sendMessage(msg);



        /*
        if(AppController.getInstance().getIsUser())
        {
            PrefUserInfo prefUserInfo = new PrefUserInfo(context);
            String userID = prefUserInfo.getUserID();

            // 사용자
            apiSetPushKey("USERUI", userID, regID);

        }
        else
        {
            // 점주

        }
        */


    }

    Handler gcmHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if( msg.what == SEND_REGID) {
                String regID = (String) msg.obj;

                customListener.onGcmClientResult(RET_CODE_SUCC, regID);
            }

        }
    };

    // gcm E
}
