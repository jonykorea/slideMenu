package com.tws.soul.soulbrown.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.AppController;
import com.app.define.LOG;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tws.common.lib.gms.LocationDefines;
import com.tws.common.lib.gms.LocationGMS;
import com.tws.network.data.ExtraType;
import com.tws.network.data.RetCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.pref.PrefUserInfo;
import com.tws.soul.soulbrown.ui.owner.OwnerAllOrderListFragment;
import com.tws.soul.soulbrown.ui.owner.OwnerOrderListFragment;
import com.tws.soul.soulbrown.ui.user.UserOrderListFragment;
import com.tws.soul.soulbrown.ui.user.UserStoreMenuFragment;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class SoulBrownMainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks , UserStoreMenuFragment.CustomOnClickListener{

    // init fragment

    // user
    Fragment userOrderListFragment;
    Fragment userStoreMenuFragment;

    // owner
    Fragment ownerOrderListFragment;
    Fragment ownerAllOrderListFragment;

    Context context;

    public static int INIT_MENU_POSITION = 0;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AppController.getInstance().getIsUser())
        {
            // user
            userOrderListFragment = new UserOrderListFragment();
            userStoreMenuFragment = new UserStoreMenuFragment();

            initGCM();

        }
        else
        {
            // owner
            ownerOrderListFragment = new OwnerOrderListFragment();
            ownerAllOrderListFragment = new OwnerAllOrderListFragment();
        }

        setContentView(R.layout.activity_soul_brown_main);

        context = getApplicationContext();

        Intent intent = getIntent();

        String userType = intent.getStringExtra(ExtraType.USER_TYPE);

        LOG.d("SoulBrownMainActivity : userType : " +userType);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // location
        requestLocation();

        onChangeDrawerLayout(INIT_MENU_POSITION);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        selectItem(position);
    }

    @Override
    public void onChangeDrawerLayout(int position) {

    }

    private void selectItem(int position) {

        FragmentTransaction ft;

        ft = getSupportFragmentManager().beginTransaction();
        // Locate Position
        switch (position) {
            case 0:

                if(AppController.getInstance().getIsUser())
                {
                    ft.replace(R.id.container,  userOrderListFragment);
                }
                else
                {
                    ft.replace(R.id.container, ownerOrderListFragment);

                }

                break;
            case 1:
            case 2:
            case 3:

                if(AppController.getInstance().getIsUser())
                {

                    ft.replace(R.id.container,  userStoreMenuFragment);
                }
                else
                {
                    ft.replace(R.id.container, ownerAllOrderListFragment);

                }

                break;

        }

        ft.commit();

        if(AppController.getInstance().getIsUser())
        {
            if (position != 0 && userStoreMenuFragment != null) {
                int movePos = position - 1;

                ((UserStoreMenuFragment) userStoreMenuFragment).setPosition(movePos);
            }
        }
    }

    @Override
    public void onChangeViewPager(int position) {

        mNavigationDrawerFragment.setListViewItemChecked(position + 1);

    }

    // gms location S

    private void requestLocation()
    {
        LocationGMS location = new LocationGMS(SoulBrownMainActivity.this,
                LocationResultHandler);

        location.connect();
    }
    public Handler LocationResultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (isFinishing())
                return;

            switch (msg.what) {

                case LocationDefines.GMS_CONNECT_SUCC:
                    Log.i("LocationResultHandler", "GMS_CONNECT_SUCC");

                    break;
                case LocationDefines.GMS_CONNECT_FAIL:
                    Log.i("LocationResultHandler", "GMS_CONNECT_FAIL");

                    break;
                case LocationDefines.GMS_DISCONNECT_SUCC:
                    Log.i("LocationResultHandler", "GMS_DISCONNECT_SUCC");
                    break;
                case LocationDefines.GMS_LOCATION_NEED_SETTING:
                    Log.i("LocationResultHandler", "GMS_LOCATION_NEED_SETTING");
                    break;
                case LocationDefines.GMS_LOCATION_SUCC:
                    Log.i("LocationResultHandler", "GMS_LOCATION_SUCC");

                    if (msg.obj != null) {
                        Location location = (Location) msg.obj;

                        Log.i("LocationResultHandler", "location getAccuracy : "
                                + location.getAccuracy());
                        Log.i("LocationResultHandler", "location getLongitude : "
                                + location.getLongitude());
                        Log.i("LocationResultHandler", "location getLatitude : "
                                + location.getLatitude());
                    } else {

                    }

                    break;
                case LocationDefines.GMS_LOCATION_FAIL:
                    Log.i("LocationResultHandler", "GMS_LOCATION_FAIL");
                    break;

            }

        }
    };
    // gms location E

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
            regid = getRegistrationId(context);

            LOG.d("regid : "+ regid);

            if (regid.isEmpty()) {
                registerInBackground();
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

        if(AppController.getInstance().getIsUser())
        {
            PrefUserInfo prefUserInfo = new PrefUserInfo(this);
            String userID = prefUserInfo.getUserID();

            // 사용자
            apiSetPushKey("USERUI", userID, regID);

        }
        else
        {
            // 점주

        }





    }

    // gcm E

    // apiUserChecker
    public void apiSetPushKey(String source, String userID, final String regID) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiSetPushKey");

        if (api != null) {
            api.apiSetPushKey(this, source, userID, null, regID, null ,new Response.Listener<RetCode>() {

                @Override
                public void onResponse(RetCode retCode) {

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);

                    if (retCode.result == 1) {

                        // success

                        storeRegistrationId(context, regID);

                    } else {
                        // fail
                        LOG.d("apiSetPushKey Fail " + retCode.result);

                        Toast.makeText(SoulBrownMainActivity.this, retCode.errormsg + "(" + retCode.result + ")", Toast.LENGTH_SHORT).show();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    LOG.d("apiSetPushKey VolleyError " + volleyError.getMessage());
                    Toast.makeText(SoulBrownMainActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    @Override
    public void onBackPressed() {


        if(mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.openCloseDrawerMenu();

            return;
        }
        else
        {
            finish();
        }

        super.onBackPressed();
    }


}
