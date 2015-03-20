package com.tws.common.listview.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.tws.common.lib.utils.TimeUtil;
import com.tws.network.data.ArrayOrderData;
import com.tws.network.data.ArrayOrderList;
import com.tws.network.data.ReceiptInfoRow;
import com.tws.network.data.RetCode;
import com.tws.network.data.RetOrderList;
import com.tws.network.data.ServerDefineCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.lib.ConvertData;

import java.util.ArrayList;
import java.util.HashMap;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class OrderListAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    // 커스텀 리스너
    private onChangeStatusListener customListener;

    // 커스텀 리스너의 인터페이스
    public interface onChangeStatusListener {
        public void onChangeStatus(String orderKey,int position, String status);
    }

    private Context context;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;
    private LayoutInflater mInflater;

    private HashMap<String,String> mOrderDate;
    private ArrayList<ArrayOrderList> mData;

    private final int INIT_INDEX = 0;

    public OrderListAdapter(Context context, ArrayList<ArrayOrderList> data, onChangeStatusListener listener) {

        this.context = context;
        this.customListener = listener;
        mInflater = LayoutInflater.from(context);

        mData = data;

        mOrderDate = new HashMap<String,String>();

        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();

    }

    private int[] getSectionIndices() {

        String timeYYYYMMDD = TimeUtil.getSimpleDateFormatYMD(mData.get(0).regtime);

        LOG.d("getSimpleDateFormatYMD : " + TimeUtil.getSimpleDateFormatYMD(mData.get(0).regtime));

        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();

        String headCompare = timeYYYYMMDD;

        int index = 0;
        mData.get(0).index = Integer.toString(index);

        sectionIndices.add(0);
        mOrderDate.put(Integer.toString(0),mData.get(0).regtime);

        for (int i = 1; i < mData.size(); i++) {

            timeYYYYMMDD =  TimeUtil.getSimpleDateFormatYMD(mData.get(i).regtime);

            if( !headCompare.equals(timeYYYYMMDD)){

                index++;

                headCompare = timeYYYYMMDD;

                mData.get(i).index = Integer.toString(index);

                sectionIndices.add(i);

                mOrderDate.put(Integer.toString(i),mData.get(i).regtime);
            }
            else
            {
                mData.get(i).index = Integer.toString(index);
                mOrderDate.put(Integer.toString(i),mData.get(i).regtime);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    private Character[] getSectionLetters() {
        Character[] letters = new Character[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] =  mData.get(mSectionIndices[i]).index.charAt(0);
        }
        return letters;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        Log.i("jony", "getView position" + position);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_owner_order_info, parent, false);
            holder.tvOrderKey = (TextView) convertView.findViewById(R.id.row_key);
            holder.tvMenu = (TextView) convertView.findViewById(R.id.row_menu);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.row_price);
            holder.tvTime = (TextView) convertView.findViewById(R.id.row_time);
            holder.tvName = (TextView) convertView.findViewById(R.id.row_name);
            holder.tvArriveTime = (TextView) convertView.findViewById(R.id.row_arrive_time);
            holder.tvDistance = (TextView) convertView.findViewById(R.id.row_distance);
            holder.llStatusIng = (LinearLayout) convertView.findViewById(R.id.row_status_ing);
            holder.llStatusFinish = (LinearLayout) convertView.findViewById(R.id.row_status_finish);
            holder.llStatusLayout = (LinearLayout) convertView.findViewById(R.id.row_status_layout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        String name = mData.get(position).nick;
        String distance = mData.get(position).status;

        String arriveUnixTime = mData.get(position).arrtime;

        String arrivalTime = TimeUtil.getNewSimpleDateFormat("a hh시 mm분", arriveUnixTime);

        ReceiptInfoRow receiptInfoRow;

        String regUnixTime = mData.get(position).regtime;

        receiptInfoRow = getSumPrice(mData.get(position).order);
        receiptInfoRow.store = mData.get(position).store;


        String regTime = TimeUtil.getNewSimpleDateFormat("a hh시 mm분", regUnixTime);

        holder.tvTime.setText(regTime);

        holder.tvPrice.setText(receiptInfoRow.sumPrice);

        holder.tvMenu.setText(receiptInfoRow.sumMenu);

        holder.tvName.setText(name);
        holder.tvArriveTime.setText(arrivalTime);
        holder.tvDistance.setText(ConvertData.getDisance(distance));

        final String storeID = mData.get(position).store;
        final String orderKey =  mData.get(position).orderkey;
        final String status =  mData.get(position).status;

        if(status.equals("3"))
        {
            holder.llStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_s);
            holder.llStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_p);

        }else if(status.equals("2"))
        {
            holder.llStatusIng.setBackgroundResource(R.drawable.icon_btn_bg_p);
            holder.llStatusFinish.setBackgroundResource(R.drawable.icon_btn_bg_s);
        }else
        {
            holder.llStatusLayout.setVisibility(View.GONE);
        }

        final int pos = position;

        holder.tvOrderKey.setText(orderKey);


        holder.llStatusIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("jony","llStatusIng orderKey "+ orderKey);

                if( status.equals("3") ) {
                    apiChgOrderMenu(storeID, orderKey, "2", pos);

                }
            }
        });

        holder.llStatusFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("jony","llStatusFinish orderKey "+ orderKey);

                if( status.equals("2") )
                    apiChgOrderMenu(storeID, orderKey, "1" , pos);

            }
        });

        return convertView;
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
                int price = Integer.parseInt(orderData.get(i).price);

                sum += count * price;

                if( i == orderData.size() - 1)
                    sumMenu += orderData.get(i).name +"x"+count;
                else
                    sumMenu += orderData.get(i).name +"x"+count+", ";

            }
        }

        receiptInfoRow.sumPrice = ConvertData.getPrice(sum);
        receiptInfoRow.sumMenu = sumMenu;


        return receiptInfoRow;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header_date, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.header_date);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        String header = mOrderDate.get(Integer.toString(position));


        LOG.d("header "+ header);

        if(!TextUtils.isEmpty(header)) {
            header = TimeUtil.getSoulBrownOrderDateInfo(header);
            holder.text.setText(header);
        }
        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        //return mCountries[position].subSequence(0, 1).charAt(0);
        return mData.get(position).index.subSequence(0, 1).charAt(0);
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }
        
        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    public void clear() {

        mSectionIndices = new int[0];
        mSectionLetters = new Character[0];
        notifyDataSetChanged();
    }

    public void restore(ArrayList<ArrayOrderList> data) {
        mData = data;
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
        notifyDataSetChanged();
    }

    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView tvOrderKey;
        TextView tvMenu;
        TextView tvTime;
        TextView tvPrice;
        TextView tvName;
        TextView tvArriveTime;
        TextView tvDistance;

        LinearLayout llStatusIng;
        LinearLayout llStatusFinish;
        LinearLayout llStatusLayout;

    }

/*
    private void changeStatusOrderMenu(String orderKey, int position, String status)
    {

        if( orderKey.equals(mData.get(position).orderkey)) {
            mData.get(position).status = status;

            if( status.equals("1"))
                mData.remove(position);

            notifyDataSetChanged();
        }

    }
    */
    // apiOrderList
    public void apiChgOrderMenu(String storeID, final String orderKey, final String status, final int position) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiChgOrderMenu orderKey " + orderKey);

        if (api != null && !TextUtils.isEmpty(orderKey)) {
            api.apiChgOrderMenu(context,storeID, orderKey, status, new Response.Listener<RetCode>() {
                @Override
                public void onResponse(RetCode retCode) {


                    LOG.d("retCode.result : " + retCode.ret);
                    LOG.d("retCode.errormsg : " + retCode.msg);


                    if (retCode.ret == ServerDefineCode.NET_RESULT_SUCC) {

                        // success
                        LOG.d("apiChgOrderMenu Succ");

                        //changeStatusOrderMenu(orderKey, position, status);

                        //if( position == 0 )
                        customListener.onChangeStatus(orderKey,position,status);




                    } else {
                        // fail
                        LOG.d("apiChgOrderMenu Fail " + retCode.ret);

                        //showToast("주문 이력 오류 : "+ retCode.errormsg+"["+retCode.result+"]");

                    }

                }
            }, new Response.ErrorListener() {


                @Override
                public void onErrorResponse(VolleyError volleyError) {


                    LOG.d("apiChgOrderMenu VolleyError " + volleyError.getMessage());

                    //showToast("네트워크 오류 : "+ volleyError.getMessage());

                }
            });
        }
    }


}
