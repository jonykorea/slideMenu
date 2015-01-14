package com.tws.soul.soulbrown;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tws.common.listview.adapter.MenuListAdapter;
import com.tws.soul.soulbrown.data.Menu;
import com.tws.soul.soulbrown.data.MenuDataManager;
import com.tws.soul.soulbrown.lib.ConvertPrice;
import com.tws.soul.soulbrown.lib.Notice;

import java.util.HashMap;
import java.util.List;


public class ViewPagerFragment03 extends Fragment {

    private RecyclerView mRecyclerView;
    private MenuListAdapter mAdapter;
    private TextView tvItemSumCount;
    private TextView tvItemSumPrice;

    private HashMap<String, String> hashMap;

    AnimatorSet animatorSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        animatorSet = new AnimatorSet();

        Notice.toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewpager_03, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        tvItemSumCount = (TextView) view.findViewById(R.id.store_item_sum_count);
        tvItemSumPrice = (TextView) view.findViewById(R.id.store_item_sum_price);


        hashMap = new HashMap<String, String>();

        mAdapter = new MenuListAdapter(MenuDataManager.getInstance().getMenu2FLAT(), R.layout.list_item_menu, getActivity(), new MenuListAdapter.CuzOnClickListener() {
            @Override
            public void onChangeItem(List<Menu> menu) {

                getMenuItem(menu);

            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void getMenuItem(List<Menu> listMenu) {
        int sumPrice = 0;
        int sumCount = 0;

        for (int i = 0; i < listMenu.size(); i++) {
            Menu menu = listMenu.get(i);

            sumCount += menu.count;
            sumPrice += menu.count * menu.price;

        }

        if (sumPrice > 100000) {
            if (Notice.toast != null) {
                Notice.toast.setText(R.string.price_warning);
                Notice.toast.show();
            }
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