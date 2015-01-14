package com.tws.soul.soulbrown;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.tws.common.lib.soulbrownlib.OrderDialog;
import com.tws.common.listview.adapter.MenuListAdapter;
import com.tws.network.data.RetCode;
import com.tws.network.data.RetOrderMenu;
import com.tws.network.data.ServerDefineCode;
import com.tws.network.data.StoreCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.broadcast.AlarmManagerBroadcastReceiver;
import com.tws.soul.soulbrown.data.Menu;
import com.tws.soul.soulbrown.data.MenuDataManager;
import com.tws.soul.soulbrown.lib.ConvertPrice;
import com.tws.soul.soulbrown.lib.Notice;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewPagerFragment01 extends Fragment {

    List<Menu> orderMenu;
    AnimatorSet animatorSet;
    OrderDialog orderDialog;
    private RecyclerView mRecyclerView;
    private MenuListAdapter mAdapter;
    private TextView tvItemSumCount;
    private TextView tvItemSumPrice;
    private RelativeLayout rlItemBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        animatorSet = new AnimatorSet();

        Notice.toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewpager_01, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

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

        mAdapter = new MenuListAdapter(MenuDataManager.getInstance().getMenuHARU(), R.layout.list_item_menu, getActivity(), new MenuListAdapter.CuzOnClickListener() {
            @Override
            public void onChangeItem(List<Menu> menu) {

                getMenuItem(menu);

            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void showDialog(final List<Menu> ListMenu) {

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

                orderDialog = new OrderDialog(getActivity(), "주문", orderMenuList);

                orderDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String arriveTime = (String) ((TextView) orderDialog.getArriveTime()).getText();

                        arriveTime = arriveTime.replace("분", "");

                        apiOrderMenu(StoreCode.CODE_HARU, ListMenu, arriveTime);

                    }
                });

                orderDialog.setOnCancelButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                orderDialog.show();

                orderDialog.getButtonAccept().setText("주문");
                orderDialog.getButtonCancel().setText("취소");

            } else {
                showToast("주문 선택을 해주세요.");
            }
        }


    }

    private String getArriveTime(String arriveTime)
    {
        LOG.d("requestOrder arriveTime : " + arriveTime);
        long time = Long.parseLong(arriveTime) * 60;
        LOG.d("requestOrder time : " + time);

        long nowUnixTime =  System.currentTimeMillis();

        LOG.d("requestOrder nowUnixTime : " + nowUnixTime);

        long arriveUnixTime = nowUnixTime +  (time *1000);

        LOG.d("requestOrder arriveUnixTime : " + arriveUnixTime);

        arriveUnixTime = arriveUnixTime / 1000;

        String calcTime = Long.toString(arriveUnixTime);

        LOG.d("requestOrder calcTime : " + calcTime);

        return calcTime;

    }

    // apiSetUserLoc
    public void apiOrderMenu(String storeID, List<Menu> listMenu, String arriveTime) {

        ApiAgent api = new ApiAgent();

        String calcTime = getArriveTime(arriveTime);

        PrefUserInfo prefUserInfo = new PrefUserInfo(getActivity());

        String userID = prefUserInfo.getUserID();

        LOG.d("apiOrderMenu userID "+ userID);

        if (api != null && !TextUtils.isEmpty(userID)) {
            api.apiOrderMenu(getActivity(), userID, storeID, calcTime , listMenu, new Response.Listener<RetOrderMenu>() {
                @Override
                public void onResponse(RetOrderMenu retCode) {

                    LOG.d("retCode.result : " + retCode.result);
                    LOG.d("retCode.errormsg : " + retCode.errormsg);

                    if (retCode.result == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiOrderMenu Succ");

                        // 성공시 메뉴 리셋. > 알람 setting.


                    } else {
                        // fail
                        LOG.d("apiOrderMenu Fail "+retCode.result );

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {



                    LOG.d("apiOrderMenu VolleyError " + volleyError.getMessage());

                }
            });
        }
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
        tvItemSumPrice.setText(ConvertPrice.getPrice(sumPrice));

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