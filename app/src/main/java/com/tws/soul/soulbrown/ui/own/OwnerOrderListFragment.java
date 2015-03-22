package com.tws.soul.soulbrown.ui.own;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.tws.common.lib.soulbrownlib.OrderDialog;
import com.tws.common.lib.utils.TimeUtil;
import com.tws.common.listview.adapter.OrderListAdapter;
import com.tws.network.data.ArrayOrderData;
import com.tws.network.data.ArrayOrderList;
import com.tws.network.data.ReceiptInfoRow;
import com.tws.network.data.RetCode;
import com.tws.network.data.RetOrderList;
import com.tws.network.data.RetOrderMenu;
import com.tws.network.data.ServerDefineCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.base.BaseFragment;
import com.tws.soul.soulbrown.broadcast.AlarmManagerBroadcastReceiver;
import com.tws.soul.soulbrown.data.Menu;
import com.tws.soul.soulbrown.gcm.GcmIntentService;
import com.tws.soul.soulbrown.lib.ConvertData;
import com.tws.soul.soulbrown.lib.Notice;
import com.tws.soul.soulbrown.lib.StoreInfo;
import com.tws.soul.soulbrown.pref.PrefOrderInfo;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class OwnerOrderListFragment extends BaseFragment implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener, OrderListAdapter.onChangeStatusListener {

    private final int SELECT_FLAG_USER = 1;

    private OrderListAdapter listAapter;
    private boolean fadeHeader = true;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;
    private LayoutInflater inflater;

    private RetOrderList mOrderListData;

    private TextView tvHeaderKey;
    private TextView tvHeaderName;
    private TextView tvHeaderArriveTime;
    private TextView tvHeaderDistance;
    private TextView tvHeaderPrice;
    private TextView tvHeaderTime;
    private TextView tvHeaderMenu;

    private LinearLayout llHeaderStatusIng;
    private LinearLayout llHeaderStatusFinish;
    private Button btnHeaderStatusAllFinish;

    private Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        context = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // control
        /*
        mAdapter.restore();
        mAdapter.notifyDataSetChanged();
        mAdapter.clear();
        */
        Notice.toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;

        return inflater.inflate(R.layout.fragment_orderlist_sticky, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //refresh
                initData();

            }
        });

        stickyList = (StickyListHeadersListView) view.findViewById(R.id.list);
        //stickyList.setOnItemClickListener(this);
        //stickyList.setOnHeaderClickListener(this);
        stickyList.setOnStickyHeaderChangedListener(this);
        stickyList.setOnStickyHeaderOffsetChangedListener(this);

        stickyList.setSelector(new ColorDrawable(0x0));

        // header content S
        View vHeader = inflater.inflate(R.layout.list_owner_header_order, null);
        tvHeaderPrice = (TextView) vHeader.findViewById(R.id.header_recent_row_price);
        tvHeaderTime = (TextView) vHeader.findViewById(R.id.header_recent_row_time);
        tvHeaderMenu = (TextView) vHeader.findViewById(R.id.header_recent_row_menu);

        tvHeaderKey = (TextView) vHeader.findViewById(R.id.header_recent_row_key);
        tvHeaderName = (TextView) vHeader.findViewById(R.id.header_recent_row_name);
        tvHeaderArriveTime = (TextView) vHeader.findViewById(R.id.header_recent_row_arrive_time);
        tvHeaderDistance = (TextView) vHeader.findViewById(R.id.header_recent_row_distance);

        llHeaderStatusIng = (LinearLayout) vHeader.findViewById(R.id.header_recent_row_status_ing);
        llHeaderStatusFinish = (LinearLayout) vHeader.findViewById(R.id.header_recent_row_status_finish);

        btnHeaderStatusAllFinish = (Button) vHeader.findViewById(R.id.header_recent_row_status_allfinish);
        // header content E

        stickyList.addHeaderView(vHeader);
        stickyList.addFooterView(inflater.inflate(R.layout.list_footer, null));
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

    private void setHeaderContent(ArrayOrderList recentOrderInfo) {
        if (recentOrderInfo != null) {

            int status = recentOrderInfo.status;

            final String storeID = recentOrderInfo.store;
            final String orderKey = recentOrderInfo.orderkey;

            tvHeaderKey.setText(orderKey);
            tvHeaderName.setText(recentOrderInfo.nick);
            tvHeaderArriveTime.setText(TimeUtil.getNewSimpleDateFormat("a hh시 mm분", recentOrderInfo.arrtime));
            tvHeaderDistance.setText(ConvertData.getDisance(recentOrderInfo.dist));

            ReceiptInfoRow info = getSumPrice(recentOrderInfo.order);

            tvHeaderMenu.setText(info.sumMenu);
            tvHeaderPrice.setText(info.sumPrice);


            String date = TimeUtil.getSoulBrownOrderDateInfo(recentOrderInfo.regtime);

            String regTime = TimeUtil.getNewSimpleDateFormat("a hh시 mm분", recentOrderInfo.regtime);

            tvHeaderTime.setText(date + " " + regTime);


            Log.i("jony", "setHeaderContent : "+ status);

            if (status == 3) {

                llHeaderStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_s);
                llHeaderStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_p);

                llHeaderStatusIng.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        apiChgOrderMenu(storeID, orderKey, 2 , 0);


                    }
                });
                llHeaderStatusFinish.setOnClickListener(null);

                btnHeaderStatusAllFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        apiChgOrderMenu(storeID, orderKey, 1 , 0);


                    }
                });


            } else if (status == 2) {

                llHeaderStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_s);

                llHeaderStatusIng.setOnClickListener(null);

                llHeaderStatusFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        apiChgOrderMenu(storeID, orderKey, 1 , 0);

                    }
                });

                btnHeaderStatusAllFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        apiChgOrderMenu(storeID, orderKey, 1 , 0);


                    }
                });

            }
        }

    }

    private ReceiptInfoRow getSumPrice(ArrayList<ArrayOrderData> orderData) {

        ReceiptInfoRow receiptInfoRow = new ReceiptInfoRow();

        int sum = 0;
        String sumMenu = "";

        if (orderData != null) {


            for (int i = 0; i < orderData.size(); i++) {
                int count = orderData.get(i).count;
                int price = Integer.parseInt(orderData.get(i).price);

                sum += count * price;

                if (i == orderData.size() - 1)
                    sumMenu += orderData.get(i).name + "x" + count;
                else
                    sumMenu += orderData.get(i).name + "x" + count + ", ";

            }
        }

        receiptInfoRow.sumPrice = ConvertData.getPrice(sum);
        receiptInfoRow.sumMenu = sumMenu;

        return receiptInfoRow;
    }

    private void initData() {

        PrefUserInfo prefUserInfo = new PrefUserInfo(context);

        String userID = prefUserInfo.getUserID();

        if (!TextUtils.isEmpty(userID))
            apiOrderList("STOREUI", userID, SELECT_FLAG_USER);
        else {

        }
    }

    private void refreshDataSet(RetOrderList orderListData) {

        setHeaderContent(orderListData.orders.get(0));

        mOrderListData = orderListData;

        listAapter = new OrderListAdapter(context, orderListData.orders, this);

        stickyList.setAdapter(listAapter);

    }


    // apiOrderList
    public void apiOrderList(String source, String userCode, int flag) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiOrderList userCode " + userCode);

        if (api != null && !TextUtils.isEmpty(userCode)) {

            if( !mBaseProgressDialog.isShowing() )
                mBaseProgressDialog.show();

            api.apiGetOrderList(context, source, null, userCode, flag, new Response.Listener<RetOrderList>() {
                @Override
                public void onResponse(RetOrderList retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

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


        /*
        if( position == 0 )
        {
            // header content
        }
        else {

            if (mOrderListData != null && mOrderListData.orderlist != null) {
                int select = position - 1;

                if( select < mOrderListData.orderlist.size())
                    setOrderMenu(mOrderListData.orderlist.get(position - 1));

            }
        }
        */

        //Toast.makeText(context, "Item " + position + " clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
        //Toast.makeText(context, "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();


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
        ArrayList<ArrayOrderData> orderData = orderMenu.order;


        List<Menu> ListMenu = new ArrayList<Menu>();

        for (int i = 0; i < orderData.size(); i++) {
            Menu menu = new Menu();

            menu.count = orderData.get(i).count;
            menu.price = Integer.parseInt(orderData.get(i).price);
            menu.name = orderData.get(i).name;

            ListMenu.add(menu);
        }

        showDialog(storeID, ListMenu);
    }

    private OrderDialog orderDialog;

    private void showDialog(final String storeID, final List<Menu> ListMenu) {

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

                orderMenuList += getString(R.string.order_sum_price)+" : " + ConvertData.getPrice(sumPrice);

                String storeName = getResources().getString(StoreInfo.getStoreName(storeID));

                orderDialog = new OrderDialog(context, getString(R.string.reorder)+" ( " + storeName + " )", orderMenuList);

                orderDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String arriveTime = (String) ((TextView) orderDialog.getArriveTime()).getText();

                        apiOrderMenu(storeID, ListMenu, arriveTime);

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


                orderDialog.getButtonAccept().setText(getString(R.string.reorder));
                orderDialog.getButtonCancel().setText(getString(R.string.cancel));

            } else {
                showToast(getString(R.string.order_select));
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


                    } else {
                        // fail
                        LOG.d("apiOrderMenu Fail " + retCode.ret);

                        showToast(getString(R.string.order_fail)+" : " + retCode.msg + "[" + retCode.ret + "]");

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiOrderMenu VolleyError " + volleyError.getMessage());

                    showToast(getString(R.string.network_fail)+" : " + volleyError.getMessage());

                }
            });
        }
    }

    // apiOrderList
    public void apiChgOrderMenu(String storeID, final String orderKey, final int status, final int position) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiChgOrderMenu orderKey " + orderKey);

        if (api != null && !TextUtils.isEmpty(orderKey)) {

            if( !mBaseProgressDialog.isShowing() )
                mBaseProgressDialog.show();

            api.apiChgOrderMenu(context,storeID, orderKey, status, new Response.Listener<RetCode>() {
                @Override
                public void onResponse(RetCode retCode) {


                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);


                    if (retCode.ret == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiChgOrderMenu Succ");

                        changeStatusOrderMenu(orderKey, position, status);




                    } else {
                        // fail
                        LOG.d("apiChgOrderMenu Fail " + retCode.ret);

                        //showToast("주문 이력 오류 : "+ retCode.errormsg+"["+retCode.result+"]");

                    }

                }
            }, new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    LOG.d("apiChgOrderMenu VolleyError " + volleyError.getMessage());

                    //showToast("네트워크 오류 : "+ volleyError.getMessage());

                }
            });
        }
    }

    @Override
    public void onChangeStatus(String orderKey, int position,  int status) {

        changeStatusOrderMenu(orderKey,  position, status);
    }

    private void changeStatusOrderMenu(String orderKey, int position, int status)
    {

        if( orderKey.equals(mOrderListData.orders.get(position).orderkey)) {
            mOrderListData.orders.get(position).status = status;

            if( status == 1 ) {

                mOrderListData.orders.clear();
                listAapter.notifyDataSetChanged();
                initData();

            }
            else {

                setHeaderContent(mOrderListData.orders.get(0));
                listAapter.notifyDataSetChanged();
            }
        }

    }


    private void setSchLocation(RetOrderMenu orderMenuInfo) {

        initData();

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

            showToast(msg);

            initData();

        }
    };


}
