package com.tws.common.listview.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.app.define.LOG;
import com.google.android.gms.drive.internal.p;
import com.tws.common.lib.utils.TimeUtil;
import com.tws.common.listview.domain.OrderList;
import com.tws.network.data.ArrayOrderData;
import com.tws.network.data.ArrayOrderList;
import com.tws.network.data.ReceiptInfoRow;
import com.tws.network.data.RetOrderList;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.lib.ConvertPrice;

import java.util.ArrayList;
import java.util.HashMap;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class StickyListAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;
    private LayoutInflater mInflater;

    private HashMap<String,String> mOrderDate;
    private ArrayList<ArrayOrderList> mData;

    private final int INIT_INDEX = 0;

    public StickyListAdapter(Context context, ArrayList<ArrayOrderList> data) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        mData = data;

        mOrderDate = new HashMap<String,String>();

        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();

    }

    private int[] getSectionIndices() {

        String timeYYYYMMDD = TimeUtil.getSimpleDateFormatYMD(mData.get(0).regdate);

        LOG.d("getSimpleDateFormatYMD : " + TimeUtil.getSimpleDateFormatYMD(mData.get(0).regdate));

        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();

        String headCompare = timeYYYYMMDD;

        int index = 0;
        mData.get(0).index = Integer.toString(index);

        sectionIndices.add(0);
        mOrderDate.put(Integer.toString(0),mData.get(0).regdate);

        for (int i = 1; i < mData.size(); i++) {

            timeYYYYMMDD =  TimeUtil.getSimpleDateFormatYMD(mData.get(i).regdate);

            if( !headCompare.equals(timeYYYYMMDD)){

                index++;

                headCompare = timeYYYYMMDD;

                mData.get(i).index = Integer.toString(index);

                sectionIndices.add(i);

                mOrderDate.put(Integer.toString(i),mData.get(i).regdate);
            }
            else
            {
                mData.get(i).index = Integer.toString(index);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_order_info, parent, false);
            holder.tvMenu = (TextView) convertView.findViewById(R.id.row_menu);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.row_price);
            holder.tvTime = (TextView) convertView.findViewById(R.id.row_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReceiptInfoRow receiptInfoRow;

        String regUnixTime = mData.get(position).regdate;

        receiptInfoRow = getSumPrice(mData.get(position).orderdata);
        receiptInfoRow.store = mData.get(position).storeid;


        String regTime = TimeUtil.getNewSimpleDateFormat("a hh시 mm분", regUnixTime);

        holder.tvTime.setText(regTime);

        holder.tvPrice.setText(receiptInfoRow.sumPrice);

        holder.tvMenu.setText(receiptInfoRow.sumMenu);




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
                int price = Integer.parseInt(orderData.get(i).menuprice);

                sum += count * price;

                if( i == orderData.size() - 1)
                    sumMenu += orderData.get(i).menuname ;
                else
                    sumMenu += orderData.get(i).menuname + ", ";

            }
        }

        receiptInfoRow.sumPrice = ConvertPrice.getPrice(sum);
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

        header = TimeUtil.getSoulBrownOrderDateInfo(header);

        LOG.d("header "+ header);

        if(!TextUtils.isEmpty(header))
            holder.text.setText(header);

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
        TextView tvMenu;
        TextView tvTime;
        TextView tvPrice;
    }

}
