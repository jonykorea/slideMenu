package com.tws.soul.soulbrown.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import com.tws.common.lib.gms.LocationDefines;
import com.tws.common.lib.gms.LocationGmsClient;
import com.tws.network.data.ExtraType;
import com.tws.network.data.RetCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.gcm.GcmClient;
import com.tws.soul.soulbrown.pref.PrefUserInfo;
import com.tws.soul.soulbrown.ui.own.OwnerAllOrderListFragment;
import com.tws.soul.soulbrown.ui.own.OwnerOrderListFragment;
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

            setGcmClient();

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
        LocationGmsClient location = new LocationGmsClient(SoulBrownMainActivity.this,
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

    // apiUserChecker
    public void apiSetPushKey(String source, String userID, final String regID) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiSetPushKey");

        if (api != null) {
            api.apiSetPushKey(this, source, userID, null, regID ,new Response.Listener<RetCode>() {

                @Override
                public void onResponse(RetCode retCode) {

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);

                    if (retCode.result == 1) {

                        // success

                        gcmClient.savePushKey(regID);

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
