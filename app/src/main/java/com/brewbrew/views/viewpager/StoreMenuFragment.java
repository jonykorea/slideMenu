package com.brewbrew.views.viewpager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.brewbrew.activities.base.BaseFragment;
import com.brewbrew.broadcast.AlarmManagerBroadcastReceiver;
import com.brewbrew.managers.data.Menu;
import com.brewbrew.google.geofence.GeofenceClient;
import com.brewbrew.managers.lib.ConvertData;
import com.brewbrew.managers.lib.GPSUtils;
import com.brewbrew.managers.pref.PrefOrderInfo;
import com.brewbrew.managers.pref.PrefUserInfo;
import com.tws.common.lib.dialog.CuzDialog;
import com.tws.common.lib.gms.LocationDefines;
import com.tws.common.lib.soulbrownlib.OrderDialog;
import com.tws.common.lib.views.CuzToast;
import com.brewbrew.listview.adapter.MenuListAdapter;
import com.brewbrew.network.data.ArrayOptionData;
import com.brewbrew.network.data.ArrayStoreData;
import com.brewbrew.network.data.RetOrderMenu;
import com.brewbrew.network.data.ServerDefineCode;
import com.brewbrew.network.lib.ApiAgent;
import com.brewbrew.R;

import java.util.ArrayList;
import java.util.List;

public class StoreMenuFragment extends BaseFragment {

    // viewpager 로 데이터를 전달할 커스텀 리스너
    private CuzOnMoveListener customListener;

    // viewpager 로 데이터를 전달할 커스텀 리스너의 인터페이스
    public interface CuzOnMoveListener {
        public void onMoveOrderList();
    }

    String mStoreID = null;
    List<Menu> mMenuData = null;
    String mStoreName = null;
    String mStoreAddr = null;

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

    private CuzToast mCuzToast;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        context = getActivity();

        this.customListener = (CuzOnMoveListener)activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStoreID = getArguments().getString("store_id");
        mStoreName = getArguments().getString("store_name");
        mStoreAddr = getArguments().getString("store_addr");
        storeInfo = getArguments().getParcelable("menu");

        animatorSet = new AnimatorSet();

        mCuzToast = new CuzToast(getActivity());

        if (mStoreID == null || storeInfo == null || storeInfo.menu == null) {
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

        Menu menu = null;
        for( int i = 0; i < cnt; i++)
        {

            menu = new Menu();
            menu.option = new ArrayList<ArrayOptionData>();

            for( int j = 0;j<storeInfo.menu.get(i).option.size();j++)
            {
                storeInfo.menu.get(i).option.get(j).count = 0;
            }


            menu.option.addAll(storeInfo.menu.get(i).option);

            menu.code = storeInfo.menu.get(i).code;
            menu.name = storeInfo.menu.get(i).name;
            menu.price = storeInfo.menu.get(i).price;
            menu.saleprice = storeInfo.menu.get(i).saleprice;

            menu.image = storeInfo.menu.get(i).img;
            menu.image_thumb = storeInfo.menu.get(i).imgtb;
            menu.comment = storeInfo.menu.get(i).comment;
            menu.comment_write = storeInfo.menu.get(i).commentwriter;

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

        TextView tvStoreAddr = (TextView) view.findViewById(R.id.store_addr_txt);
        tvStoreAddr.setText("주소 : "+mStoreAddr);

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

                    Menu menu = ListMenu.get(i);
                    String name = menu.name;


                    for(int j = 0; j<menu.option.size();j++) {
                        int cnt = menu.option.get(j).count;
                        String nameOpt = menu.option.get(j).name;
                        int saleprice = menu.saleprice;

                        sumPrice += (saleprice + menu.option.get(j).addprice) * cnt;

                        if (cnt != 0) {

                            orderMenuList += name+" ("+nameOpt + ") : " + cnt + "개\n";
                            //orderMenuList += ConvertPrice.getPrice(price * cnt)+"\n";
                        }
                    }

                }


                if (sumPrice == 0) {
                    mCuzToast.showToast( getString(R.string.order_select),Toast.LENGTH_SHORT);
                } else {

                    orderMenuList += "총 주문 금액 : " + ConvertData.getPrice(sumPrice);

                    String storeName = mStoreName;

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
                mCuzToast.showToast( getString(R.string.order_select),Toast.LENGTH_SHORT);
            }
        }


    }

    private String getArriveTime(String arriveTime) {
        LOG.d("requestOrder arriveTime : " + arriveTime);

        arriveTime = arriveTime.replace("분 후", "");

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

            api.apiOrderMenu(context, userID, storeID, calcTime, listMenu,false, new Response.Listener<RetOrderMenu>() {
                @Override
                public void onResponse(RetOrderMenu retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    if( !isAdded() )
                        return;

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

                        mCuzToast.showToast( getString(R.string.order_fail)+" : " + retCode.msg + "[" + retCode.ret + "]",Toast.LENGTH_SHORT);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiOrderMenu VolleyError " + volleyError.getMessage());

                    mCuzToast.showToast( getString(R.string.network_fail),Toast.LENGTH_SHORT);

                }
            });
        }
    }

    private void setSchLocation(RetOrderMenu orderMenuInfo) {

        initMenuData();

        getMenuItem(mMenuData);

        initAdapter(mMenuData);

        mCuzToast.showToast( getString(R.string.order_succ),Toast.LENGTH_LONG);

        String time = orderMenuInfo.arrtime;
        String store = orderMenuInfo.store;

        long arriveUnixTime = Long.parseLong(time);
        LOG.d("setSchLocation arriveUnixTime : " + arriveUnixTime);

        // save arriveTime
        PrefOrderInfo prefOrderInfo = new PrefOrderInfo(context);
        prefOrderInfo.setArriveTime(arriveUnixTime * 1000);
        prefOrderInfo.setOrderStore(store);

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

        if(!GPSUtils.getLocaionProvider(context))
        {
            final String msg = getString(R.string.gps_fail);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if( mCuzToast != null)
                        mCuzToast.showToast( msg,Toast.LENGTH_LONG);

                }
            },3000);
        }

        // 주문내역으로 이동. S
        customListener.onMoveOrderList();
        // 주문내역으로 이동. E

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


    private void getMenuItem(List<Menu> listMenu) {


        orderMenu = new ArrayList<Menu>();
        orderMenu.addAll(listMenu);


        int sumPrice = 0;
        int sumCount = 0;

        for (int i = 0; i < listMenu.size(); i++) {
            Menu menu = listMenu.get(i);


            for(int j = 0; j<menu.option.size();j++)
            {
                sumCount += menu.option.get(j).count;

                int saleprice = menu.saleprice;

                sumPrice += (saleprice + menu.option.get(j).addprice) * menu.option.get(j).count;
            }

            //sumCount += menu.count;
            //sumPrice += menu.count * menu.price;

        }

        if (sumPrice > 100000) {

            mCuzToast.showToast( getString(R.string.price_warning),Toast.LENGTH_SHORT);

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