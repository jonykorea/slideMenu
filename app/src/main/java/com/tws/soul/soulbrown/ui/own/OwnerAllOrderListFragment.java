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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.tws.common.listview.adapter.OrderListAdapter;
import com.tws.network.data.ArrayOrderData;
import com.tws.network.data.ReceiptInfoRow;
import com.tws.network.data.RetOrderList;
import com.tws.network.data.ServerDefineCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.base.BaseFragment;
import com.tws.soul.soulbrown.gcm.GcmIntentService;
import com.tws.soul.soulbrown.lib.ConvertData;
import com.tws.soul.soulbrown.lib.Notice;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class OwnerAllOrderListFragment extends BaseFragment implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener , OrderListAdapter.onChangeStatusListener {

    private final String SELECT_FLAG_USER = "store-complete";

    private OrderListAdapter listAapter;
    private boolean fadeHeader = true;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;
    private LayoutInflater inflater;

    private RetOrderList mOrderListData;

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
        stickyList.setOnHeaderClickListener(this);
        stickyList.setOnStickyHeaderChangedListener(this);
        stickyList.setOnStickyHeaderOffsetChangedListener(this);

        stickyList.setSelector(new ColorDrawable(0x0));

        // header content S
        View vHeader = inflater.inflate(R.layout.list_owner_header_all, null);

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

    private ReceiptInfoRow getSumPrice(ArrayList<ArrayOrderData> orderData) {

        ReceiptInfoRow receiptInfoRow = new ReceiptInfoRow();

        int sum = 0;
        String sumMenu = "";

        if (orderData != null) {


            for (int i = 0; i < orderData.size(); i++) {
                int count = orderData.get(i).count;
                int price = Integer.parseInt(orderData.get(i).menuprice);

                sum += count * price;

                if (i == orderData.size() - 1)
                    sumMenu += orderData.get(i).menuname + "x" + count;
                else
                    sumMenu += orderData.get(i).menuname + "x" + count + ", ";

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

        mOrderListData = orderListData;

        listAapter = new OrderListAdapter(context, mOrderListData.orderlist, this);

        stickyList.setAdapter(listAapter);

    }


    // apiOrderList
    public void apiOrderList(String source, String userCode, String selectFlag) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiOrderList userCode " + userCode);

        if (api != null && !TextUtils.isEmpty(userCode)) {

            if( !mBaseProgressDialog.isShowing() )
                mBaseProgressDialog.show();

            api.apiGetOrderList(context, source, null, userCode, selectFlag, new Response.Listener<RetOrderList>() {
                @Override
                public void onResponse(RetOrderList retCode) {

                    if( mBaseProgressDialog.isShowing() )
                        mBaseProgressDialog.dismiss();

                    if (refreshLayout != null) {

                        if (refreshLayout.isRefreshing())
                            refreshLayout.setRefreshing(false);
                    }

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);


                    if (retCode.result == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiOrderList Succ");

                        if (retCode.orderlist != null)
                            refreshDataSet(retCode);
                        else {
                            // 주문 내역이 없다.


                        }


                    } else {
                        // fail
                        LOG.d("apiOrderList Fail " + retCode.result);

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

    @Override
    public void onChangeStatus(String orderKey, int position,  String status) {


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