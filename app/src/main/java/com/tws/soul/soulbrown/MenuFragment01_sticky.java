package com.tws.soul.soulbrown;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.tws.common.lib.soulbrownlib.OrderDialog;
import com.tws.common.lib.utils.TimeUtil;
import com.tws.common.listview.adapter.GenericAdapter;
import com.tws.common.listview.adapter.StickyListAdapter;
import com.tws.common.listview.domain.OrderList;
import com.tws.common.listview.viewmapping.OrderListView;
import com.tws.network.data.ArrayOrderData;
import com.tws.network.data.ArrayOrderList;
import com.tws.network.data.CoreGetPublicKey;
import com.tws.network.data.ReceiptInfoRow;
import com.tws.network.data.RetOrderList;
import com.tws.network.data.RetOrderMenu;
import com.tws.network.data.ServerDefineCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.broadcast.AlarmManagerBroadcastReceiver;
import com.tws.soul.soulbrown.data.Menu;
import com.tws.soul.soulbrown.lib.ConvertPrice;
import com.tws.soul.soulbrown.lib.Notice;
import com.tws.soul.soulbrown.lib.StoreInfo;
import com.tws.soul.soulbrown.pref.PrefOrderInfo;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MenuFragment01_sticky extends Fragment implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener {

    private final String SELECT_FLAG_USER = "user-all";

    private StickyListAdapter listAapter;
    private boolean fadeHeader = true;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;
    private LayoutInflater inflater;

    private RetOrderList mOrderListData;

    private TextView tvHeaderPrice;
    private TextView tvHeaderTime;
    private TextView tvHeaderMenu;
    private TextView tvHeaderStore;
    private LinearLayout llHeaderStatusReady;
    private LinearLayout llHeaderStatusIng;
    private LinearLayout llHeaderStatusFinish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // control
        /*
        mAdapter.restore();
        mAdapter.notifyDataSetChanged();
        mAdapter.clear();
        */
        Notice.toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
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

                // refresh
                //data.add("ATT");

                //listAapter.restore(data);

                /*
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
                */
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
        tvHeaderMenu = (TextView) vHeader.findViewById(R.id.header_recent_row_menu);
        tvHeaderStore = (TextView) vHeader.findViewById(R.id.header_recent_row_store);
        llHeaderStatusReady = (LinearLayout) vHeader.findViewById(R.id.header_recent_row_status_ready);
        llHeaderStatusIng = (LinearLayout) vHeader.findViewById(R.id.header_recent_row_status_ing);
        llHeaderStatusFinish = (LinearLayout) vHeader.findViewById(R.id.header_recent_row_status_finish);
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

    private void setHeaderContent(ArrayOrderList recentOrderInfo)
    {
        if( recentOrderInfo != null )
        {

            String status = recentOrderInfo.status;

            ReceiptInfoRow info = getSumPrice(recentOrderInfo.orderdata);

            tvHeaderStore.setText(StoreInfo.getStoreName(recentOrderInfo.storeid));
            tvHeaderMenu.setText(info.sumMenu);
            tvHeaderPrice.setText(info.sumPrice);

            String date = TimeUtil.getSoulBrownOrderDateInfo(recentOrderInfo.regdate);

            String regTime = TimeUtil.getNewSimpleDateFormat("a hh시 mm분", recentOrderInfo.regdate);

            tvHeaderTime.setText(date + " " +regTime);

            if(status.equals("3"))
            {
                llHeaderStatusReady.setBackgroundResource(R.drawable.icon_btn_bg_s);
                llHeaderStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_p);

            }else if(status.equals("2"))
            {
                llHeaderStatusReady.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_s);
                llHeaderStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_p);

            }else if(status.equals("1"))
            {
                llHeaderStatusReady.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_p);
                llHeaderStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_s);

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
                int count = orderData.get(i).count;
                int price = Integer.parseInt(orderData.get(i).menuprice);

                sum += count * price;

                if( i == orderData.size() - 1)
                    sumMenu += orderData.get(i).menuname +"x"+count;
                else
                    sumMenu += orderData.get(i).menuname +"x"+count+", ";

            }
        }

        receiptInfoRow.sumPrice = ConvertPrice.getPrice(sum);
        receiptInfoRow.sumMenu = sumMenu;

        return receiptInfoRow;
    }
    private void initData() {
        PrefUserInfo prefUserInfo = new PrefUserInfo(getActivity());

        String userID = prefUserInfo.getUserID();

        if (!TextUtils.isEmpty(userID))
            apiOrderList("USERUI", userID, SELECT_FLAG_USER);
        else {

        }
    }

    private void refreshDataSet(RetOrderList orderListData) {

        mOrderListData = orderListData;

        listAapter = new StickyListAdapter(getActivity(), orderListData.orderlist);

        stickyList.setAdapter(listAapter);

        setHeaderContent(orderListData.orderlist.get(0));

    }


    // apiOrderList
    public void apiOrderList(String source, String userCode, String selectFlag) {

        ApiAgent api = new ApiAgent();

        PrefUserInfo prefUserInfo = new PrefUserInfo(getActivity());

        String userID = prefUserInfo.getUserID();

        LOG.d("apiOrderList userID " + userID);

        if (api != null && !TextUtils.isEmpty(userID)) {
            api.apiGetOrderList(getActivity(), source, userCode, null, selectFlag, new Response.Listener<RetOrderList>() {
                @Override
                public void onResponse(RetOrderList retCode) {

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

            if (mOrderListData != null && mOrderListData.orderlist != null) {
                int select = position - 1;

                if( select < mOrderListData.orderlist.size())
                    setOrderMenu(mOrderListData.orderlist.get(position - 1));

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

        String storeID = orderMenu.storeid;
        ArrayList<ArrayOrderData> orderData = orderMenu.orderdata;


        List<Menu> ListMenu = new ArrayList<Menu>();

        for (int i = 0; i < orderData.size(); i++) {
            Menu menu = new Menu();

            menu.count = orderData.get(i).count;
            menu.price = Integer.parseInt(orderData.get(i).menuprice);
            menu.name = orderData.get(i).menuname;

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

                orderMenuList += "총 합계 : " + ConvertPrice.getPrice(sumPrice);

                String storeName = getResources().getString(StoreInfo.getStoreName(storeID));

                orderDialog = new OrderDialog(getActivity(), "재주문 ( " + storeName + " )", orderMenuList);

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
                PrefOrderInfo prefOrderInfo = new PrefOrderInfo(getActivity());

                String settingTime = prefOrderInfo.getSettingTime();

                orderDialog.setTvArriveTime(settingTime);

                // get setting time E


                orderDialog.getButtonAccept().setText("재주문");
                orderDialog.getButtonCancel().setText("취소");

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

        PrefUserInfo prefUserInfo = new PrefUserInfo(getActivity());

        String userID = prefUserInfo.getUserID();

        LOG.d("apiOrderMenu userID " + userID);

        if (api != null && !TextUtils.isEmpty(userID)) {
            api.apiOrderMenu(getActivity(), userID, storeID, calcTime, listMenu, new Response.Listener<RetOrderMenu>() {
                @Override
                public void onResponse(RetOrderMenu retCode) {

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);
                    LOG.d("retCode.orderkey : " + retCode.orderkey);
                    LOG.d("retCode.arrivaltime : " + retCode.arrivaltime);

                    if (retCode.result == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiOrderMenu Succ");

                        // 성공시 메뉴 리셋. > 알람 setting.

                        // save setting time S
                        PrefOrderInfo prefOrderInfo = new PrefOrderInfo(getActivity());

                        prefOrderInfo.setSettingTime(arriveTime);
                        // save setting time E

                        setSchLocation(retCode);


                    } else {
                        // fail
                        LOG.d("apiOrderMenu Fail " + retCode.result);

                        showToast("주문 오류 : " + retCode.errormsg + "[" + retCode.result + "]");

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {


                    LOG.d("apiOrderMenu VolleyError " + volleyError.getMessage());

                    showToast("네트워크 오류 : " + volleyError.getMessage());

                }
            });
        }
    }

    private void setSchLocation(RetOrderMenu orderMenuInfo) {

        initData();

        showToast("정상적으로 재주문되었습니다.");

        String time = orderMenuInfo.arrivaltime;

        long arriveUnixTime = Long.parseLong(time);
        LOG.d("setSchLocation arriveUnixTime : " + arriveUnixTime);

        // save arriveTime
        PrefOrderInfo prefOrderInfo = new PrefOrderInfo(getActivity());
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
        alarmManagerBroadcastReceiver.setOnetimeTimer(getActivity(), calcUnixTime);

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


}
