package com.tws.soul.soulbrown;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tws.common.lib.dialog.MyCustomDialog;
import com.tws.common.listview.adapter.CardViewListAdapter;
import com.tws.common.listview.domain.CountryManager;
import com.tws.soul.twscommonlib.TwsCommonLibMainActivity;


public class ViewPagerFragment01 extends Fragment {

    private RecyclerView mRecyclerView;
    private CardViewListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragment_viewpager_01.xml
        View view = inflater.inflate(R.layout.fragment_viewpager_01, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new CardViewListAdapter(CountryManager.getInstance().getCountries(), R.layout.row_country, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }
}