package com.tws.soul.soulbrown.ui.viewpager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.tws.common.lib.dialog.CuzDialog;
import com.tws.common.lib.gms.LocationDefines;
import com.tws.common.lib.soulbrownlib.OrderDialog;
import com.tws.common.listview.adapter.MenuListAdapter;
import com.tws.network.data.ArrayOptionData;
import com.tws.network.data.ArrayStoreData;
import com.tws.network.data.RetMenuList;
import com.tws.network.data.RetOrderMenu;
import com.tws.network.data.ServerDefineCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.base.BaseFragment;
import com.tws.soul.soulbrown.broadcast.AlarmManagerBroadcastReceiver;
import com.tws.soul.soulbrown.data.Menu;
import com.tws.soul.soulbrown.data.MenuDataManager;
import com.tws.soul.soulbrown.geofence.GeofenceClient;
import com.tws.soul.soulbrown.lib.ConvertData;
import com.tws.soul.soulbrown.lib.Notice;
import com.tws.soul.soulbrown.lib.StoreInfo;
import com.tws.soul.soulbrown.pref.PrefOrderInfo;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.util.ArrayList;
import java.util.List;

public class StoreMenuFragment extends BaseFragment {

    String mStoreID = null;
    List<Menu> mMenuData = null;
    String mStoreName = null;

    List<Menu> orderMenu;
    AnimatorSet animatorSet;
    OrderDialog orderDialog;
    private RecyclerView mRecyclerView;
    private MenuListAdapter mAdapter;
    private TextView tvItemSumCount;
    private TextView tvItemSumPrice;
    private RelativeLayout rlItemBtn;

    private Context context;

    private ArrayStoreData storeInfo;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        context = getActivity();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStoreID = getArguments().getString("store");

        storeInfo = getArguments().getParcelable("menu");

        animatorSet = new AnimatorSet();

        Notice.toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        if (mStoreID == null) {
            getActivity().finish();
            return;
        }
        else {


            initMenuData();
        }
    }

    private void initMenuData() {
        /*
        if (mStoreID.equals(StoreInfo.CODE_HARU)) {
            mMenuData = MenuDataManager.getInstance().getMenuHARU();
            mStoreName = context.getResources().getString(R.string.store_haru);
        } else if (mStoreID.equals(StoreInfo.CODE_1022)) {
            mMenuData = MenuDataManager.getInstance().getMenu1022();
            mStoreName = context.getResources().getString(R.string.store_1022);
        } else if (mStoreID.equals(StoreInfo.CODE_2FLAT)) {
            mMenuData = MenuDataManager.getInstance().getMenu2FLAT();
            mStoreName = context.getResources().getString(R.string.store_2flat);
        } else if (mStoreID.equals(StoreInfo.CODE_TWS)) {
            mMenuData = MenuDataManager.getInstance().getMenuTWS();
            mStoreName = context.getResources().getString(R.string.store_tws);
        }
        */
        mMenuData =  new ArrayList<Menu>();

        int cnt = storeInfo.menu.size();

        Log.i("jony","initMenuData "+ cnt);

        Menu menu = null;
        for( int i = 0; i < cnt; i++)
        {

            menu = new Menu();
            menu.option = new ArrayList<ArrayOptionData>();
            menu.option.addAll(storeInfo.menu.get(i).option);
            menu.name = storeInfo.menu.get(i).name;
            menu.price = storeInfo.menu.get(i).price;

            Log.i("jony","initMenuData name "+  menu.name);
            //menu.image = storeInfo.menu.get(i).img;
            mMenuData.add(menu);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewpager_menu, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        TextView tvStoreName = (TextView) view.findViewById(R.id.store_name_txt);
        tvStoreName.setText(mStoreName);

        tvItemSumCount = (TextView) view.findViewById(R.id.store_item_sum_count);
        tvItemSumPrice = (TextView) view.findViewById(R.id.store_item_sum_price);
        rlItemBtn = (RelativeLayout) view.findViewById(R.id.store_item_btn);

        rlItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog(orderMenu);
            }
        });

        orderMenu = new ArrayList<Menu>();

        initAdapter(mMenuData);

        return view;
    }

    private void showDialog(final List<Menu> ListMenu) {

        if (orderDialog != null && orderDialog.isShowing())
            return;

        String orderMenuList = "";
        int sumPrice = 0;

        if (ListMenu != null) {
            if (ListMenu.size() > 0) {


                for (int i = 0; i < ListMenu.size(); i++) {
                    int cnt = ListMenu.get(i).count;
                    String name = ListMenu.get(i).name;
                    int price = ListMenu.get(i).price;

                    sumPrice += price * cnt;

                    if (ListMenu.get(i).count != 0) {
                        orderMenuList += name + " : " + cnt + "개\n";
                        //orderMenuList += ConvertPrice.getPrice(price * cnt)+"\n";
                    }

                }

                if (sumPrice == 0) {
                    showToast("주문 선택을 해주세요.");
                } else {

                    orderMenuList += "총 주문 금액 : " + ConvertData.getPrice(sumPrice);

                    String storeName = getResources().getString(StoreInfo.getStoreName(mStoreID));

                    orderDialog = new OrderDialog(context, "주문 ( " + storeName + " )", orderMenuList);

                    orderDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String arriveTime = (String) ((TextView) orderDialog.getArriveTime()).getText();

                            apiOrderMenu(mStoreID, ListMenu, arriveTime);

                        }
                    });

                    orderDialog.setOnCancelButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });


                    orderDialog.show();

                    // get setting time S
                    PrefOrderInfo prefOrderInfo = new PrefOrderInfo(context);

                    String settingTime = prefOrderInfo.getSettingTime();

                    orderDialog.setTvArriveTime(settingTime);

                    // get setting time E

                    orderDialog.getButtonAccept().setText("주문");
                    orderDialog.getButtonCancel().setText("취소");
                }

            } else {
                showToast("주문 선택을 해주세요.");
            }
        }


    }

    private String getArriveTime(String arriveTime) {
        LOG.d("requestOrder arriveTime : " + arriveTime);

        arriveTime = arriveTime.replace("분", "");

        long time = Long.parseLong(arriveTime) * 60;
        LOG.d("requestOrder time : " + time);

        long nowUnixTime = System.currentTimeMillis();

        LOG.d("requestOrder nowUnixTime : " + nowUnixTime);

        long arriveUnixTime = nowUnixTime + (time * 1000);

        LOG.d("requestOrder arriveUnixTime : " + arriveUnixTime);

        arriveUnixTime = arriveUnixTime / 1000;

        String calcTime = Long.toString(arriveUnixTime);

        LOG.d("requestOrder calcTime : " + calcTime);

        return calcTime;

    }

    // apiSetUserLoc
    public void apiOrderMenu(String storeID, List<Menu> listMenu, final String arriveTime) {

        ApiAgent api = new ApiAgent();

        String calcTime = getArriveTime(arriveTime);

        PrefUserInfo prefUserInfo = new PrefUserInfo(context);

        String userID = prefUserInfo.getUserID();

        LOG.d("apiOrderMenu userID " + userID);

        if (api != null && !TextUtils.isEmpty(userID)) {

            if( !mBaseProgressDialog.isShowing() )
                mBaseProgressDialog.show();

            api.apiOrderMenu(context, userID, storeID, calcTime, listMenu, new Response.Listener<RetOrderMenu>() {
                @Override
                public void onResponse(RetOrderMenu retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);
                    LOG.d("retCode.orderkey : " + retCode.orderkey);
                    LOG.d("retCode.arrivaltime : " + retCode.arrtime);

                    if (retCode.ret == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiOrderMenu Succ");

                        // 성공시 메뉴 리셋. > 알람 setting.

                        // save setting time S
                        PrefOrderInfo prefOrderInfo = new PrefOrderInfo(context);

                        prefOrderInfo.setSettingTime(arriveTime);
                        // save setting time E

                        setSchLocation(retCode);


                    } else if(retCode.ret == ServerDefineCode.NET_RESULT_ALREADY) {

                        if( mBaseDialog == null || !mBaseDialog.isShowing()) {
                            mBaseDialog = new CuzDialog(context,
                                    "확인", "이미 주문된 내역이 있습니다.\n주문내역에서 확인하세요.");

                            mBaseDialog.show();

                            mBaseDialog.setCancelable(true);

                            mBaseDialog.getButtonAccept().setText("확인");

                            mBaseDialog.getButtonCancel().setVisibility(View.INVISIBLE);



                        }

                    }
                    else
                    {
                        // fail
                        LOG.d("apiOrderMenu Fail " + retCode.ret);

                        showToast("주문 오류 : " + retCode.msg + "[" + retCode.ret + "]");

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiOrderMenu VolleyError " + volleyError.getMessage());

                    showToast("네트워크 오류 : " + volleyError.getMessage());

                }
            });
        }
    }

    private void setSchLocation(RetOrderMenu orderMenuInfo) {

        initMenuData();

        getMenuItem(mMenuData);

        initAdapter(mMenuData);

        showToast(getString(R.string.order_succ));

        String time = orderMenuInfo.arrtime;

        long arriveUnixTime = Long.parseLong(time);
        LOG.d("setSchLocation arriveUnixTime : " + arriveUnixTime);

        // save arriveTime
        PrefOrderInfo prefOrderInfo = new PrefOrderInfo(context);
        prefOrderInfo.setArriveTime(arriveUnixTime * 1000);

        long nowUnixTime = System.currentTimeMillis() / 1000;
        LOG.d("setSchLocation nowUnixTime : " + nowUnixTime);

        final long MIN_10 = 60 * 10;
        long calcUnixTime = (arriveUnixTime - nowUnixTime) - MIN_10;
        LOG.d("setSchLocation calcUnixTime : " + calcUnixTime);

        if (calcUnixTime < 0) {
            calcUnixTime = 0;
        }

        AlarmManagerBroadcastReceiver alarmManagerBroadcastReceiver = new AlarmManagerBroadcastReceiver();
        alarmManagerBroadcastReceiver.setRepeatTimer(context, calcUnixTime);

        geofenceHanlder.sendEmptyMessage(GeofenceClient.SET_GEOFENCE);

    }

    GeofenceClient geofenceClient;

    Handler geofenceHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if( msg.what == GeofenceClient.SET_GEOFENCE)
            {
                geofenceClient = new GeofenceClient(context, GeofenceResultHandler);

                geofenceClient.connect();

            }


        }
    };

    public Handler GeofenceResultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case LocationDefines.GMS_CONNECT_SUCC:
                    Log.i("LocationResultHandler", "GMS_CONNECT_SUCC");

                    if( geofenceClient != null)
                        geofenceClient.startGeofence();


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

                case LocationDefines.GMS_LOCATION_FAIL:
                    Log.i("LocationResultHandler", "GMS_LOCATION_FAIL");
                    break;

            }

        }
    };

    private void initAdapter(List<Menu> resetMenu) {

        mAdapter = new MenuListAdapter(resetMenu, R.layout.list_item_menu, context, new MenuListAdapter.CuzOnClickListener() {
            @Override
            public void onChangeItem(List<Menu> menu) {

                getMenuItem(menu);

            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    private void showToast(int resID) {
        if (Notice.toast != null) {
            Notice.toast.setText(resID);
            Notice.toast.show();
        }
    }

    private void showToast(String msg) {
        if (Notice.toast != null) {
            Notice.toast.setText(msg);
            Notice.toast.show();
        }
    }

    private void getMenuItem(List<Menu> listMenu) {


        orderMenu = new ArrayList<Menu>();
        orderMenu.addAll(listMenu);


        int sumPrice = 0;
        int sumCount = 0;

        for (int i = 0; i < listMenu.size(); i++) {
            Menu menu = listMenu.get(i);

            sumCount += menu.count;
            sumPrice += menu.count * menu.price;

        }

        if (sumPrice > 100000) {

            showToast(R.string.price_warning);

        }

        tvItemSumCount.setText(Integer.toString(sumCount));
        tvItemSumPrice.setText(ConvertData.getPrice(sumPrice));

        aniZoomInOut();
    }

    private void aniZoomInOut() {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(tvItemSumCount, "scaleX", 0.8f);
        scaleXAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scaleXAnimator.setRepeatCount(1);
        scaleXAnimator.setDuration(500);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(tvItemSumCount, "scaleY", 0.8f);
        scaleYAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scaleYAnimator.setRepeatCount(1);
        scaleYAnimator.setDuration(500);

        if (animatorSet != null && !animatorSet.isRunning()) {
            animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
            animatorSet.start();
        }

    }

}