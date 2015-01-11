package com.tws.soul.soulbrown;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.tws.common.listview.adapter.GenericAdapter;
import com.tws.common.listview.adapter.StickyListAdapter;
import com.tws.common.listview.domain.OrderList;
import com.tws.common.listview.viewmapping.OrderListView;
import com.tws.network.data.CoreGetPublicKey;
import com.tws.network.lib.ApiAgent;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class MenuFragment01_sticky extends Fragment implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener{

    private StickyListAdapter mAdapter;
    private boolean fadeHeader = true;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;



    ListView mOrderListView;

    private ApiAgent api;

    private void initApiAgent()
    {
        if( api == null)
        {
            api = new ApiAgent();
        }
    }

    ArrayList<String> data;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initApiAgent();



        data = new ArrayList<String>();

        for(int i = 0; i < 30;i++)
        {
            if( i < 5)
            {
                data.add(i,"CAB"+ i);
            }
            else if( i < 10)
            {
                data.add(i,"DCD"+ i);
            }
            else if( i < 20)
            {
                data.add(i,"EF"+ i);
            }
            else
            {
                data.add(i,"GH"+ i);
            }

         }

        mAdapter = new StickyListAdapter(getActivity(), data);



        // control
        /*
        mAdapter.restore();
        mAdapter.notifyDataSetChanged();
        mAdapter.clear();
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initApiAgent();

        // 네트워크 테스트 S
        apiGetPublicKey();
        // 네트워크 테스트 E

        // Get the view from fragment_viewpager_01.xml
        View rootLayout = inflater.inflate(R.layout.fragment_orderlist_sticky, container, false);

        /* 일반 listview
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
        */

        refreshLayout = (SwipeRefreshLayout) rootLayout.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                data.add("ATT");

                mAdapter.restore(data);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        stickyList = (StickyListHeadersListView) rootLayout.findViewById(R.id.list);
        stickyList.setOnItemClickListener(this);
        stickyList.setOnHeaderClickListener(this);
        stickyList.setOnStickyHeaderChangedListener(this);
        stickyList.setOnStickyHeaderOffsetChangedListener(this);
        stickyList.addHeaderView(inflater.inflate(R.layout.list_header, null));
        stickyList.addFooterView(inflater.inflate(R.layout.list_footer, null));
        stickyList.setEmptyView(rootLayout.findViewById(R.id.empty));
        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);
        stickyList.setAdapter(mAdapter);

        // option
        stickyList.setStickyHeaderTopOffset(-20);
        stickyList.setDrawingListUnderStickyHeader(true);
        boolean setFastScroll = false;
        stickyList.setFastScrollEnabled(setFastScroll);
        stickyList.setFastScrollAlwaysVisible(setFastScroll);

        return rootLayout;
    }


    // get Publickey
    public void apiGetPublicKey() {

        LOG.d("apiGetPublicKey");

        if (api != null) {
            api.apiPublicKey(getActivity(), new Response.Listener<CoreGetPublicKey>() {
                @Override
                public void onResponse(CoreGetPublicKey coreGetPublicKey) {

                    LOG.d("apiPublicKey result " + coreGetPublicKey.result);
                    LOG.d("apiPublicKey status " + coreGetPublicKey.status);

                    // save DB : public key
                    // return data check
                    LOG.d("r_public.result : " + coreGetPublicKey.result);
                    LOG.d("r_public.errormsg : " + coreGetPublicKey.errormsg);
                    LOG.d("r_public.result : " + coreGetPublicKey.status);
                    LOG.d("r_public.resMsg : " + coreGetPublicKey.resMsg);
                    LOG.d("r_public.errMessage : " + coreGetPublicKey.errMessage);

                    if (coreGetPublicKey.result == 0) {

                        // success
                        String publickey = coreGetPublicKey.key;

                        LOG.d("publickey : " + publickey);


                    } else {
                        // fail
                        int code = coreGetPublicKey.result;
                        String errormsg = coreGetPublicKey.errormsg;

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    LOG.d("apiPublicKey VolleyError " + volleyError.getMessage());

                }
            });
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Item " + position + " clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
        Toast.makeText(getActivity(), "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();
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


}
