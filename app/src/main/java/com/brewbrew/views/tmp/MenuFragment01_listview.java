package com.brewbrew.views.tmp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.brewbrew.listview.adapter.GenericAdapter;
import com.brewbrew.listview.domain.OrderList;
import com.brewbrew.listview.viewmapping.OrderListView;
import com.brewbrew.network.data.CoreGetPublicKey;
import com.brewbrew.network.lib.ApiAgent;
import com.brewbrew.R;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment01_listview extends Fragment {
    ListView mOrderListView;

    private ApiAgent api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new ApiAgent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 네트워크 테스트 S
        //apiGetPublicKey();
        // 네트워크 테스트 E

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

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

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


}
