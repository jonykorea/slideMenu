package com.brewbrew.views;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.AppController;
import com.app.define.LOG;
import com.brewbrew.activities.base.BaseFragmentActivity;
import com.brewbrew.google.gcm.GcmClient;
import com.brewbrew.managers.lib.BackPressCloseHandler;
import com.brewbrew.managers.pref.PrefStoreInfo;
import com.brewbrew.managers.pref.PrefUserInfo;
import com.brewbrew.views.own.OwnerAllOrderListFragment;
import com.brewbrew.views.own.OwnerOrderListFragment;
import com.brewbrew.views.user.UserOrderListFragment;
import com.brewbrew.views.user.UserStoreMenuFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tws.common.lib.gms.LocationDefines;
import com.tws.common.lib.gms.LocationGmsClient;
import com.tws.common.lib.views.CuzToast;
import com.brewbrew.network.data.ExtraType;
import com.brewbrew.network.data.RetCode;
import com.brewbrew.network.data.RetMenuList;
import com.brewbrew.network.data.RetPushMsgStatus;
import com.brewbrew.network.lib.ApiAgent;
import com.brewbrew.R;
import com.brewbrew.views.viewpager.StoreMenuFragment;

import android.widget.RelativeLayout.LayoutParams;

import src.com.wunderlist.slidinglayer.SlidingLayer;


public class SoulBrownMainActivity extends BaseFragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, UserStoreMenuFragment.CustomOnClickListener,StoreMenuFragment.CuzOnMoveListener{

    // init fragment

    // user
    Fragment userOrderListFragment;
    Fragment userStoreMenuFragment;

    // owner
    Fragment ownerOrderListFragment;
    Fragment ownerAllOrderListFragment;

    Context context;

    private CuzToast mCuzToast;

    public static int INIT_MENU_POSITION = 0;

    private BackPressCloseHandler backPressCloseHandler;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        setCustomActionBar();

        mCuzToast = new CuzToast(this);

        backPressCloseHandler = new BackPressCloseHandler(this);

        if (AppController.getInstance().getIsUser()) {

            if (!mBaseProgressDialog.isShowing())
                mBaseProgressDialog.show();

            apiGetMenuList("");
        } else {
            bindViews(null);
        }

    }

    private void setCustomActionBar() {

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F2F4F5")));

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setIcon(null);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.actionbar_custom_layout, null);
        getActionBar().setCustomView(actionBarView);
        getActionBar().setDisplayShowCustomEnabled(true);

    }


    private RetMenuList mMenuList;

    public RetMenuList getMenuList() {
        return mMenuList;
    }

    private void bindViews(RetMenuList menuList) {
        if (mBaseProgressDialog.isShowing())
            mBaseProgressDialog.dismiss();

        if (AppController.getInstance().getIsUser()) {
            if (menuList != null) {

                mMenuList = menuList;

                // user
                userOrderListFragment = new UserOrderListFragment();
                userStoreMenuFragment = new UserStoreMenuFragment();

                setGcmClient();
            } else {
                // 종료 팝업.
                finish();
            }

        } else {
            // owner
            ownerOrderListFragment = new OwnerOrderListFragment();
            ownerAllOrderListFragment = new OwnerAllOrderListFragment();

        }

        setContentView(R.layout.activity_soul_brown_main);

        Intent intent = getIntent();

        int userType = intent.getIntExtra(ExtraType.USER_TYPE, 0);

        LOG.d("SoulBrownMainActivity : userType : " + userType);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (!AppController.getInstance().getIsUser()) {
            // 현재 push 상태 확인.
            apiGetPushMsgStatus();
        }

        // location
        requestLocation();

        onChangeDrawerLayout(INIT_MENU_POSITION);

        initSlidingState();

    }

    private SlidingLayer mSlidingLayer;
    private ImageView mIvSlidingLayer;

    private void openSliding(String url) {
        if (mSlidingLayer.isClosed() && mClosedSlidingState ) {

            Glide.with(this).load(url)
                    //.bitmapTransform(new RoundedCornersTransformation(Glide.get(this).getBitmapPool(), 30, 0))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {


                            if( mNavigationDrawerFragment.isDrawerOpen())
                                return;

                            mIvSlidingLayer.setImageDrawable(resource);

                            backgroundFadeIn();

                            mSlidingLayer.openLayer(true);

                        }
                    });


        }
    }

    private void closeSliding() {
        if (mSlidingLayer.isOpened() && mOpenedSlidingState) {


            mSlidingLayer.closeLayer(true);
        }
    }

    boolean mClosedSlidingState = true;
    boolean mOpenedSlidingState = false;

    LinearLayout mllScreenBackground;

    private void backgroundFadeIn() {

        /*
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(mllScreenBackground, "backgroundColor", new ArgbEvaluator(), Color.argb(0,255,255,255), 0x99000000);
        objectAnimator.setDuration(500);
        objectAnimator.start();
        */

        mllScreenBackground.setBackgroundColor(Color.argb(153,0,0,0));
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);

        mllScreenBackground.startAnimation(alphaAnimation);

    }

    private void backgroundFadeOut() {
        /*
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(mllScreenBackground, "backgroundColor", new ArgbEvaluator(), Color.argb(153,0,0,0), 0x00000000);
        objectAnimator.setDuratin(500);
        objectAnimator.start();
        */

        mllScreenBackground.setBackgroundColor(Color.argb(153,0,0,0));
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);

        mllScreenBackground.startAnimation(alphaAnimation);
    }

    private void initSlidingState() {

        mllScreenBackground = (LinearLayout) findViewById(R.id.screen_background);

        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);
        mIvSlidingLayer = (ImageView) findViewById(R.id.slidinglayer_image);

        mSlidingLayer.setStickTo(SlidingLayer.STICK_TO_BOTTOM);

        LayoutParams rlp = (LayoutParams) mSlidingLayer.getLayoutParams();
        rlp.width = LayoutParams.MATCH_PARENT;
        rlp.height = LayoutParams.MATCH_PARENT;
        mSlidingLayer.setLayoutParams(rlp);

        mSlidingLayer.setShadowSize(0);
        mSlidingLayer.setShadowDrawable(null);

        mSlidingLayer.setOffsetDistance(0);
        mSlidingLayer.setPreviewOffsetDistance(-1);

        mSlidingLayer.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpen() {


            }

            @Override
            public void onShowPreview() {

            }

            @Override
            public void onClose() {

                backgroundFadeOut();

            }

            @Override
            public void onOpened() {

                mClosedSlidingState = false;
                mOpenedSlidingState = true;

            }

            @Override
            public void onPreviewShowed() {

            }

            @Override
            public void onClosed() {

                mClosedSlidingState = true;
                mOpenedSlidingState = false;

            }
        });

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        selectItem(position);
    }

    @Override
    public void onChangeDrawerLayout(int position) {

    }

    @Override
    public void onChangeDrawerLayoutOpened(int position) {

        if (mSlidingLayer.isOpened())
            closeSliding();

    }


    private void selectItem(int position) {

        FragmentTransaction ft;

        ft = getSupportFragmentManager().beginTransaction();
        // Locate Position
        if (position == 0) {
            if (AppController.getInstance().getIsUser()) {
                ft.replace(R.id.container, userOrderListFragment);
            } else {
                ft.replace(R.id.container, ownerOrderListFragment);

            }
        } else {
            if (AppController.getInstance().getIsUser()) {

                ft.replace(R.id.container, userStoreMenuFragment);
            } else {
                ft.replace(R.id.container, ownerAllOrderListFragment);

            }
        }

        ft.commit();

        if (AppController.getInstance().getIsUser()) {
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

    private void requestLocation() {
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

    private void setGcmClient() {
        gcmClient = new GcmClient(this);

        gcmClient.initGCM(new GcmClient.onGcmClientListener() {
            @Override
            public void onGcmClientResult(int result, String msg) {

                LOG.d("onGcmClientResult : " + result + " msg : " + msg);

                if (result == GcmClient.RET_CODE_SUCC) {
                    PrefUserInfo prefUserInfo = new PrefUserInfo(SoulBrownMainActivity.this);
                    String userID = prefUserInfo.getUserID();

                    // 사용자
                    apiSetPushKey("USERUI", userID, msg);
                } else {

                }

            }
        });

    }

    // gcm E

    // apiSetPushKey
    public void apiSetPushKey(String source, String userID, final String regID) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiSetPushKey");

        if (api != null) {
            api.apiSetPushKeyUser(this, userID, regID, new Response.Listener<RetCode>() {

                @Override
                public void onResponse(RetCode retCode) {

                    if(isFinishing())
                        return;

                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);

                    if (retCode.ret == 1) {

                        // success

                        gcmClient.savePushKey(regID);

                    } else {
                        // fail
                        LOG.d("apiSetPushKey Fail " + retCode.ret);

                        //Toast.makeText(SoulBrownMainActivity.this, retCode.msg + "(" + retCode.ret + ")", Toast.LENGTH_SHORT).show();
                        mCuzToast.showToast(retCode.msg + "(" + retCode.ret + ")", Toast.LENGTH_SHORT);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if(isFinishing())
                        return;

                    LOG.d("apiSetPushKey VolleyError " + volleyError.getMessage());
                    //Toast.makeText(SoulBrownMainActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                    mCuzToast.showToast(getString(R.string.network_fail), Toast.LENGTH_SHORT);

                }
            });
        }
    }

    // apiSetPushKey
    public void apiGetPushMsgStatus() {

        ApiAgent api = new ApiAgent();

        LOG.d("apiSetPushKey");

        PrefUserInfo prefUserInfo = new PrefUserInfo(context);

        String storeID = prefUserInfo.getUserID();

        if (api != null) {
            api.apiGetPushMsgStatus(this, storeID, new Response.Listener<RetPushMsgStatus>() {

                @Override
                public void onResponse(RetPushMsgStatus retCode) {

                    if(isFinishing())
                        return;

                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);
                    LOG.d("retCode.push : " + retCode.push);

                    if (retCode.ret == 1) {

                        // success
                        Intent intentGcm = new Intent("push_status");

                        intentGcm.putExtra("status", retCode.push);

                        PrefStoreInfo prefStoreInfo = new PrefStoreInfo(context);
                        prefStoreInfo.setPushStatus(retCode.push);


                        LocalBroadcastManager.getInstance(context).sendBroadcast(intentGcm);

                    } else {
                        // fail
                        LOG.d("apiSetPushKey Fail " + retCode.ret);

                        //Toast.makeText(SoulBrownMainActivity.this, retCode.msg + "(" + retCode.ret + ")", Toast.LENGTH_SHORT).show();
                        mCuzToast.showToast(retCode.msg + "(" + retCode.ret + ")", Toast.LENGTH_SHORT);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if(isFinishing())
                        return;

                    LOG.d("apiSetPushKey VolleyError " + volleyError.getMessage());
                    //Toast.makeText(SoulBrownMainActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                    mCuzToast.showToast(getString(R.string.network_fail), Toast.LENGTH_SHORT);
                }
            });
        }
    }

    // apiSetPushKey
    public void apiGetMenuList(String storeID) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiGetMenuList");

        if (api != null) {
            api.apiGetMenuList(this, storeID, new Response.Listener<RetMenuList>() {

                @Override
                public void onResponse(RetMenuList retCode) {

                    if(isFinishing())
                        return;


                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);
                    LOG.d("retCode.store.size() : " + retCode.store.size());

                    if (retCode.ret == 1) {

                        // success
                        bindViews(retCode);

                    } else {
                        // fail
                        if (mBaseProgressDialog.isShowing())
                            mBaseProgressDialog.dismiss();

                        LOG.d("apiGetMenuList Fail " + retCode.ret);

                        //Toast.makeText(SoulBrownMainActivity.this, retCode.msg + "(" + retCode.ret + ")", Toast.LENGTH_SHORT).show();
                        mCuzToast.showToast(retCode.msg + "(" + retCode.ret + ")", Toast.LENGTH_SHORT);

                        finish();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if(isFinishing())
                        return;

                    if (mBaseProgressDialog.isShowing())
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiGetMenuList VolleyError " + volleyError.getMessage());
                    //Toast.makeText(SoulBrownMainActivity.this, getString(R.string.network_fail), Toast.LENGTH_SHORT).show();
                    mCuzToast.showToast(getString(R.string.network_fail), Toast.LENGTH_SHORT);

                    finish();
                }
            });
        }
    }


    @Override
    public void onBackPressed() {

        if (mSlidingLayer.isOpened()) {
            closeSliding();
            return;
        }
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.openCloseDrawerMenu();

            return;
        } else {
            backPressCloseHandler.onBackPressed();

            ///setResult(RESULT_OK);
            //finish();
        }
    }

    // flurry
    @Override
    protected void onStart() {
        super.onStart();
        //FlurryAgent.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //FlurryAgent.onEndSession(this);
    }

    @Override
    public void onResume() {
        super.onResume();


        if( mSlidingLayer!= null && mSlidingLayer.isClosed() )
        {
            backgroundFadeOut();
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(SlidingImageSync, new IntentFilter("image_url"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(SlidingImageSync);
    }

    private BroadcastReceiver SlidingImageSync = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String url = intent.getStringExtra("url");

            //mCuzToast.showToast(msg,Toast.LENGTH_SHORT);
            if (mSlidingLayer != null) {
                LOG.d("SlidingImageSync url " + url);

                openSliding(url);
            }


        }
    };

    @Override
    public void onMoveOrderList() {

        // 주문완료시 주문내역 페이지로 셋팅.
        selectItem(0);
        mNavigationDrawerFragment.setListViewItemChecked(0);

    }


}
