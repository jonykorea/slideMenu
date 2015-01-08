package com.tws.soul.soulbrown;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tws.common.listview.adapter.GenericAdapter;
import com.tws.common.listview.domain.OrderList;
import com.tws.common.listview.viewmapping.OrderListView;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment01 extends Fragment {
    ListView mOrderListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragment_viewpager_01.xml
        View rootLayout = inflater.inflate(R.layout.fragment_orderlist, container, false);

        mOrderListView = (ListView) rootLayout.findViewById(R.id.order_listview);


        String[] orderHistoryList = new String[] { "Apple", "Avocado", "Banana",
                "Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
                "Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };

        List<OrderListView> list = new ArrayList<OrderListView>();
        for(int i = 0; i < orderHistoryList.length; i++){
            OrderListView mv = new OrderListView(new OrderList(orderHistoryList[i]), R.layout.list_order);
            list.add(mv);
        }

        mOrderListView.setAdapter(new GenericAdapter(list, getActivity()));




        return rootLayout;
    }
	
}
