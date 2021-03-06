package com.brewbrew.views.user;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.brewbrew.google.geofence.GeofenceClient;
import com.brewbrew.managers.lib.ConvertData;
import com.tws.common.lib.dialog.CuzDialog;
import com.tws.common.lib.gms.LocationDefines;
import com.tws.common.lib.soulbrownlib.OrderDialog;
import com.tws.common.lib.utils.TimeUtil;
import com.tws.common.lib.views.CuzToast;
import com.brewbrew.listview.adapter.StickyListAdapter;
import com.brewbrew.network.data.ArrayOptionData;
import com.brewbrew.network.data.ArrayOrderData;
import com.brewbrew.network.data.ArrayOrderList;
import com.brewbrew.network.data.ReceiptInfoRow;
import com.brewbrew.network.data.RetCode;
import com.brewbrew.network.data.RetOrderList;
import com.brewbrew.network.data.RetOrderMenu;
import com.brewbrew.network.data.ServerDefineCode;
import com.brewbrew.network.lib.ApiAgent;
import com.brewbrew.R;
import com.brewbrew.activities.base.BaseFragment;
import com.brewbrew.broadcast.AlarmManagerBroadcastReceiver;
import com.brewbrew.managers.data.Menu;
import com.brewbrew.google.gcm.GcmIntentService;
import com.brewbrew.managers.lib.GPSUtils;
import com.brewbrew.managers.pref.PrefOrderInfo;
import com.brewbrew.managers.pref.PrefUserInfo;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class UserOrderListFragment extends BaseFragment implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener {

    private final int SELECT_FLAG_USER = -1;
    private Context context;

    private StickyListAdapter listAapter;
    private boolean fadeHeader = true;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;
    private LayoutInflater inflater;

    private RetOrderList mOrderListData;

    private TextView tvHeaderPrice;
    private TextView tvHeaderTime;
    private TextView tvHeaderVisitTime;
    private TextView tvHeaderMenu;
    private TextView tvHeaderStore;
    private TextView tvHeaderKey;
    private LinearLayout llHeaderStatusReady;
    private LinearLayout llHeaderStatusIng;
    private LinearLayout llHeaderStatusFinish;
    private LinearLayout llHeaderStatusCancel;
    private Button btnHeaderStatusCancel;
    private Button btnHeaderStatusRecall;

    private CuzToast mCuzToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // control
        /*
        mAdapter.restore();
        mAdapter.notifyDataSetChanged();
        mAdapter.clear();
        */
        mCuzToast = new CuzToast(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;

        return inflater.inflate(R.layout.fragment_orderlist_sticky_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //refresh
                PrefUserInfo prefUserInfo = new PrefUserInfo(context);

                String userID = prefUserInfo.getUserID();

                apiOrderList("USERUI", userID, SELECT_FLAG_USER);

            }
        });

        stickyList = (StickyListHeadersListView) view.findViewById(R.id.list);
        stickyList.setOnItemClickListener(this);
        stickyList.setOnHeaderClickListener(this);
        stickyList.setOnStickyHeaderChangedListener(this);
        stickyList.setOnStickyHeaderOffsetChangedListener(this);

        // header content S
        View vHeader = inflater.inflate(R.layout.list_header, null);
        tvHeaderPrice = (TextView) vHeader.findViewById(R.id.header_recent_row_price);
        tvHeaderTime = (TextView) vHeader.findViewById(R.id.header_recent_row_time);
        tvHeaderVisitTime = (TextView) vHeader.findViewById(R.id.header_recent_row_visit_time);
        tvHeaderKey = (TextView) vHeader.findViewById(R.id.header_recent_row_key);
        tvHeaderMenu = (TextView) vHeader.findViewById(R.id.header_recent_row_menu);
        tvHeaderStore = (TextView) vHeader.findViewById(R.id.header_recent_row_store);
        llHeaderStatusReady = (LinearLayout) vHeader.findViewById(R.id.header_recent_row_status_ready);
        llHeaderStatusIng = (LinearLayout) vHeader.findViewById(R.id.header_recent_row_status_ing);
        llHeaderStatusFinish = (LinearLayout) vHeader.findViewById(R.id.header_recent_row_status_finish);

        llHeaderStatusCancel = (LinearLayout) vHeader.findViewById(R.id.header_recent_cancel);

        btnHeaderStatusCancel = (Button) vHeader.findViewById(R.id.header_recent_cancel_btn);

        btnHeaderStatusRecall = (Button) vHeader.findViewById(R.id.header_recent_recall_btn);

        // header content E

        stickyList.addHeaderView(vHeader);
        stickyList.addFooterView(inflater.inflate(R.layout.list_footer_user, null));
        stickyList.setEmptyView(view.findViewById(R.id.empty));
        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);
        stickyList.setAdapter(listAapter);


        // option
        //stickyList.setStickyHeaderTopOffset(-10);
        stickyList.setDrawingListUnderStickyHeader(true);
        boolean setFastScroll = false;
        stickyList.setFastScrollEnabled(setFastScroll);
        stickyList.setFastScrollAlwaysVisible(setFastScroll);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                initData();

            }
        }, 300);


    }

    private void setHeaderContent(ArrayOrderList recentOrderInfo)
    {

        if( recentOrderInfo != null )
        {

            int status = recentOrderInfo.status;

            ReceiptInfoRow info = ConvertData.getSumPrice(recentOrderInfo.order);

            tvHeaderKey.setText(getString(R.string.order_id)+" : "+recentOrderInfo.orderkey);
            tvHeaderStore.setText(getString(R.string.order_store)+" : "+recentOrderInfo.storename);
            tvHeaderMenu.setText(info.sumMenu);
            tvHeaderPrice.setText(getString(R.string.order_sum_price)+" : "+info.sumPrice);

            String date = TimeUtil.getSoulBrownOrderDateInfo(recentOrderInfo.regtime);


            String regTime = TimeUtil.getNewSimpleDateFormat("a hh시 mm분", recentOrderInfo.regtime);
            String arrTime = TimeUtil.getNewSimpleDateFormat("a hh시 mm분", recentOrderInfo.arrtime);

            tvHeaderTime.setText("음료 주문시간 : "+date + " " +regTime);
            tvHeaderVisitTime.setText("방문 예정시간 : "+date + " " +arrTime);
            if(status == 3)
            {
                llHeaderStatusReady.setBackgroundResource(R.drawable.icon_btn_bg_s);
                llHeaderStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusCancel.setVisibility(View.VISIBLE);

                // cancel
                final String orderkey =  recentOrderInfo.orderkey;
                btnHeaderStatusCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if( !TextUtils.isEmpty(orderkey))
                        {
                            // cancel popup
                            if( mBaseDialog == null || !mBaseDialog.isShowing()) {

                                // order info
                                String orderInfo =
                                        tvHeaderStore.getText().toString() +"\n\n"
                                        +tvHeaderTime.getText().toString() +"\n"
                                        +tvHeaderKey.getText().toString() +"\n"
                                        +tvHeaderPrice.getText().toString() +"\n"
                                        +tvHeaderMenu.getText().toString();

                                mBaseDialog = new CuzDialog(context,
                                        getString(R.string.order_cancel), orderInfo);

                                mBaseDialog.show();

                                mBaseDialog.setCancelable(true);

                                mBaseDialog.getButtonAccept().setText(getString(R.string.order_cancel));

                                mBaseDialog.getButtonCancel().setText(getString(R.string.cancel));

                                mBaseDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        mBaseDialog.dismiss();
                                        // api
                                        apiOrderCancel(orderkey);
                                    }
                                });
                            }
                        }
                        else
                        {
                            // order key error
                        }
                    }
                });


                    final long regUnixTime = Long.parseLong(recentOrderInfo.regtime);
                   // btnHeaderStatusRecall.setTextColor(Color.parseColor("E52A19"));
                    btnHeaderStatusRecall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // 현재시간이랑 비교후 5분이상 지난 후에만 클릭 가능하게 처리.
                            final int FIVE_MIN = 60 * 5;
                            long nowUnixTime = System.currentTimeMillis() / 1000L;
                            long tmpTime = regUnixTime + FIVE_MIN;

                            if (tmpTime <= nowUnixTime) {

                            if (!TextUtils.isEmpty(orderkey)) {
                                // cancel popup
                                if (mBaseDialog == null || !mBaseDialog.isShowing()) {

                                    // order info
                                    String orderInfo =
                                            tvHeaderStore.getText().toString() + "\n\n"
                                                    + tvHeaderTime.getText().toString() + "\n"
                                                    + tvHeaderKey.getText().toString() + "\n"
                                                    + tvHeaderPrice.getText().toString() + "\n"
                                                    + tvHeaderMenu.getText().toString();

                                    mBaseDialog = new CuzDialog(context,
                                            getString(R.string.order_recall), orderInfo);

                                    mBaseDialog.show();

                                    mBaseDialog.setCancelable(true);

                                    mBaseDialog.getButtonAccept().setText(getString(R.string.order_recall));

                                    mBaseDialog.getButtonCancel().setText(getString(R.string.cancel));

                                    mBaseDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            mBaseDialog.dismiss();
                                            // api
                                            apiOrderRecall(orderkey);
                                        }
                                    });
                                }
                            } else {
                                // order key error
                            }
                            } else {
                                // 5분 미만일 경우

                                mCuzToast.showToast(getString(R.string.order_recall_fail), Toast.LENGTH_LONG);
                            }
                        }
                    });



            }else if(status == 2)
            {
                llHeaderStatusReady.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_s);
                llHeaderStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusCancel.setVisibility(View.GONE);

            }else if(status == 1)
            {
                llHeaderStatusReady.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_s);
                llHeaderStatusCancel.setVisibility(View.GONE);

            }




        }

    }

    private ReceiptInfoRow getSumPrice(ArrayList<ArrayOrderData> orderData) {

        ReceiptInfoRow receiptInfoRow = new ReceiptInfoRow();

        int sum = 0;
        String sumMenu = "";

        if( orderData != null)
        {


            for(int i = 0 ; i< orderData.size() ; i++)
            {
                int price = orderData.get(i).totalprice;
                String name = orderData.get(i).name;

                sum += price;

                for(int j = 0; j < orderData.get(i).option.size();j++)
                {
                    String nameOpt = orderData.get(i).option.get(j).name;
                    // timesale
                    if(nameOpt.equals("주문 개수"))
                    {
                        nameOpt = "Time Sale";
                    }

                    sumMenu += name +"("+nameOpt+")x" +orderData.get(i).option.get(j).count+",";
                }

            }
        }

        receiptInfoRow.sumPrice = ConvertData.getPrice(sum);
        receiptInfoRow.sumMenu = sumMenu;

        return receiptInfoRow;
    }
    private void initData() {
        PrefUserInfo prefUserInfo = new PrefUserInfo(context);

        String userID = prefUserInfo.getUserID();

        if (!TextUtils.isEmpty(userID)) {

            if( !mBaseProgressDialog.isShowing() )
                mBaseProgressDialog.show();

            apiOrderList("USERUI", userID, SELECT_FLAG_USER);

        }
        else {

        }
    }

    private void refreshDataSet(RetOrderList orderListData) {

        setHeaderContent(orderListData.orders.get(0));

        mOrderListData = orderListData;

        listAapter = new StickyListAdapter(context, orderListData.orders);

        stickyList.setAdapter(listAapter);

    }


    // apiOrderList
    public void apiOrderList(String source, String userCode, int flag) {

        ApiAgent api = new ApiAgent();

        if (api != null && !TextUtils.isEmpty(userCode)) {

            if( !mBaseProgressDialog.isShowing() )
                mBaseProgressDialog.show();

            api.apiGetOrderList(context, source, userCode, null, flag, new Response.Listener<RetOrderList>() {
                @Override
                public void onResponse(RetOrderList retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    if( !isAdded() )
                        return;

                    if (refreshLayout != null) {

                        if (refreshLayout.isRefreshing())
                            refreshLayout.setRefreshing(false);
                    }

                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);


                    if (retCode.ret == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiOrderList Succ");

                        if (retCode.orders != null)
                            refreshDataSet(retCode);
                        else {
                            // 주문 내역이 없다.
                            stickyList.setAdapter(null);
                        }


                    } else {
                        // fail
                        LOG.d("apiOrderList Fail " + retCode.ret);

                        //showToast("주문 이력 오류 : "+ retCode.errormsg+"["+retCode.result+"]");

                    }

                }
            }, new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    if (refreshLayout != null) {

                        if (refreshLayout.isRefreshing())
                            refreshLayout.setRefreshing(false);
                    }
                    LOG.d("apiOrderList VolleyError " + volleyError.getMessage());

                    //showToast("네트워크 오류 : "+ volleyError.getMessage());

                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        LOG.d("onItemClick position : " + position);
        if( position == 0 )
        {
            // header content
        }
        else {

            if (mOrderListData != null && mOrderListData.orders != null) {
                int select = position - 1;

                if( select < mOrderListData.orders.size())
                    setOrderMenu(mOrderListData.orders.get(position - 1));

            }
        }

        //Toast.makeText(getActivity(), "Item " + position + " clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
        //Toast.makeText(getActivity(), "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();


    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {
        if (fadeHeader && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
        header.setAlpha(1);

    }


    public void setOrderMenu(ArrayOrderList orderMenu) {

        String storeID = orderMenu.store;
        String storeName = orderMenu.storename;
        ArrayList<ArrayOrderData> orderData = orderMenu.order;


        List<Menu> ListMenu = new ArrayList<Menu>();

        for (int i = 0; i < orderData.size(); i++) {
            Menu menu = new Menu();

            menu.price = orderData.get(i).price;
            menu.name = orderData.get(i).name;
            menu.saleprice = orderData.get(i).saleprice;
            menu.code = orderData.get(i).code;
            menu.option = new ArrayList<ArrayOptionData>();
            menu.option.addAll(orderData.get(i).option);

            ListMenu.add(menu);
        }

        showDialog(storeName, storeID, ListMenu);
    }

    private OrderDialog orderDialog;

    private void showDialog(final String storeName, final String storeID, final List<Menu> ListMenu) {


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

                        // timesale
                        if(nameOpt.equals("주문 개수"))
                        {
                            nameOpt = "Time Sale";
                        }

                        int saleprice = menu.saleprice;

                        sumPrice += (saleprice + menu.option.get(j).addprice) * cnt;

                        if (cnt != 0) {

                            orderMenuList += name+" ("+nameOpt + ") : " + cnt + "개\n";
                            //orderMenuList += ConvertPrice.getPrice(price * cnt)+"\n";
                        }
                    }

                }

                orderMenuList += getString(R.string.order_sum_price)+" : " + ConvertData.getPrice(sumPrice);

                orderDialog = new OrderDialog(context, getString(R.string.reorder)+" ( " + storeName + " )", orderMenuList);

                orderDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String arriveTime = (String) ((TextView) orderDialog.getArriveTime()).getText();

                        confirmOrder(storeID, ListMenu, arriveTime);
                        //apiOrderMenu(storeID, ListMenu, arriveTime);

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


                orderDialog.getButtonAccept().setText(getString(R.string.order_confirm));
                orderDialog.getButtonCancel().setText(getString(R.string.order_cancel));

            } else {
                mCuzToast.showToast( getString(R.string.order_select),Toast.LENGTH_SHORT);
            }
        }


    }

    private void confirmOrder(final String storeId, final List<Menu> ListMenu,final String arriveTime)
    {

        if( mBaseDialog == null || !mBaseDialog.isShowing()) {
            mBaseDialog = new CuzDialog(context,
                    getString(R.string.confirm),getString(R.string.order_menu_confirm_content));

            mBaseDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new Handler().postDelayed(new Runnable() {// 0.2 초 후에 실행
                        @Override
                        public void run() {
                            // 실행할 동작 코딩
                            apiOrderMenu(storeId, ListMenu, arriveTime);
                        }
                    }, 200);


                }
            });

            mBaseDialog.setOnCancelButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            mBaseDialog.show();

            mBaseDialog.setCancelable(true);

            mBaseDialog.getButtonAccept().setText(getString(R.string.order_confirm));
            mBaseDialog.getButtonCancel().setText(getString(R.string.order_cancel));
            mBaseDialog.getButtonCancel().setVisibility(View.VISIBLE);
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

            api.apiOrderMenu(context, userID, storeID, calcTime, listMenu,true, new Response.Listener<RetOrderMenu>() {
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


                    }else if( retCode.ret == ServerDefineCode.NET_RESULT_ALREADY)
                    {

                        if( mBaseDialog == null || !mBaseDialog.isShowing()) {
                            mBaseDialog = new CuzDialog(context,
                                    getString(R.string.confirm), getString(R.string.order_check_list));

                            mBaseDialog.show();

                            mBaseDialog.setCancelable(true);

                            mBaseDialog.getButtonAccept().setText(getString(R.string.confirm));

                            mBaseDialog.getButtonCancel().setVisibility(View.INVISIBLE);



                        }
                    }
                    else {
                        // fail
                        LOG.d("apiOrderMenu Fail " + retCode.ret);

                        mCuzToast.showToast( getString(R.string.order_fail)+ " : " + retCode.msg + "[" + retCode.ret + "]",Toast.LENGTH_SHORT);

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

        initData();

        mCuzToast.showToast( getString(R.string.reorder_succ),Toast.LENGTH_LONG);

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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if( mCuzToast != null)
                        mCuzToast.showToast( getString(R.string.gps_fail),Toast.LENGTH_LONG);

                }
            },3000);
        }

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

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(GcmMsgRefreshSync, new IntentFilter(GcmIntentService.GCM_BROADCAST));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(GcmMsgRefreshSync);
    }

    private BroadcastReceiver GcmMsgRefreshSync = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String msg = intent.getStringExtra("msg");

            mCuzToast.showToast(msg,Toast.LENGTH_SHORT);

            initData();

        }
    };

    private void refreshData()
    {
        //refresh
        PrefUserInfo prefUserInfo = new PrefUserInfo(context);

        String userID = prefUserInfo.getUserID();

        apiOrderList("USERUI", userID, SELECT_FLAG_USER);
    }


    public void apiOrderCancel(String orderKey) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiOrderCanel orderKey " + orderKey);

        if (api != null && !TextUtils.isEmpty(orderKey)) {
            api.apiChgOrderMenu(context, orderKey, 1, new Response.Listener<RetCode>() {
                @Override
                public void onResponse(RetCode retCode) {


                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);

                    if( !isAdded() )
                        return;


                    if (retCode.ret == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiOrderCancel Succ");

                        mCuzToast.showToast( getString(R.string.order_succ_cancel),Toast.LENGTH_SHORT);
                        refreshData();


                    } else {
                        // fail
                        LOG.d("apiOrderCancel Fail " + retCode.ret);

                        //showToast("주문 이력 오류 : "+ retCode.errormsg+"["+retCode.result+"]");

                        mCuzToast.showToast( getString(R.string.order_fail)+ " : " + retCode.msg + "[" + retCode.ret + "]",Toast.LENGTH_SHORT);
                        refreshData();
                    }

                }
            }, new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError volleyError) {


                    LOG.d("apiOrderCancel VolleyError " + volleyError.getMessage());

                    mCuzToast.showToast( getString(R.string.network_fail),Toast.LENGTH_SHORT);

                }
            });
        }
    }

    public void apiOrderRecall(String orderKey) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiOrderRecall orderKey " + orderKey);

        if (api != null && !TextUtils.isEmpty(orderKey)) {
            api.apiOrderRecall(context, orderKey, new Response.Listener<RetCode>() {
                @Override
                public void onResponse(RetCode retCode) {


                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);

                    if (!isAdded())
                        return;


                    if (retCode.ret == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiOrderRecall Succ");

                        mCuzToast.showToast(getString(R.string.order_recall_succ), Toast.LENGTH_LONG);
                        refreshData();


                    } else {
                        // fail
                        LOG.d("apiOrderRecall Fail " + retCode.ret);

                        //showToast("주문 이력 오류 : "+ retCode.errormsg+"["+retCode.result+"]");

                        mCuzToast.showToast(getString(R.string.order_fail) + " : " + retCode.msg + "[" + retCode.ret + "]", Toast.LENGTH_SHORT);
                        refreshData();
                    }

                }
            }, new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError volleyError) {


                    LOG.d("apiOrderRecall VolleyError " + volleyError.getMessage());

                    mCuzToast.showToast(getString(R.string.network_fail), Toast.LENGTH_SHORT);

                }
            });
        }
    }

}
