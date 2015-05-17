package com.brewbrew.managers.lib;

import android.text.TextUtils;

import com.app.define.LOG;
import com.brewbrew.network.data.ArrayOrderData;
import com.brewbrew.network.data.ReceiptInfoRow;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by jonychoi on 15. 1. 12..
 */
public class ConvertData {

    public static String getPrice(int price) {
        String cvtPrice = "￦ ";

        if (price < 1000) {
            cvtPrice = cvtPrice + price + "원";
        } else {
            DecimalFormat df = new DecimalFormat("#,###");

            cvtPrice = cvtPrice + df.format(price) + "원";
        }


        return cvtPrice;
    }

    public static String getDisance(String distance)
    {
        String retDiatance = "";

        if(TextUtils.isEmpty(distance))
        {
            retDiatance = "정보없음";
        }
        else
        {
            int dis = Integer.parseInt(distance);

            if( dis < 0)
            {
                retDiatance = "정보없음";
            }
            else if( dis < 1000)
            {
                retDiatance = dis + "m";
            }
            else
            {
                retDiatance = (dis/100)/10F + "km";
            }
        }

        return retDiatance;
    }

    public static ReceiptInfoRow getSumPrice(ArrayList<ArrayOrderData> orderData) {

        ReceiptInfoRow receiptInfoRow = new ReceiptInfoRow();

        int sum = 0;
        String sumMenu = "";

        if( orderData != null)
        {

            for(int i = 0 ; i< orderData.size() ; i++)
            {
                int price = orderData.get(i).totalprice;
                String name = orderData.get(i).name;

                sum += price;

                for(int j = 0; j < orderData.get(i).option.size();j++)
                {
                    int cnt = orderData.get(i).option.get(j).count;

                    if( cnt != 0) {

                        LOG.d("i: "+i +" j :"+j+"orderData.size() + "+ orderData.size() +" orderData.get(i).option.size() "+ orderData.get(i).option.size());

                        String nameOpt = orderData.get(i).option.get(j).name;
                        if(nameOpt.equals("주문 개수"))
                        {
                            nameOpt = "Time Sale";
                        }

                        if (i == (orderData.size() - 1) && j == (orderData.get(i).option.size() - 1)) {
                            sumMenu += name + "(" + nameOpt + ")x" + orderData.get(i).option.get(j).count;
                        } else {
                            sumMenu += name + "(" +nameOpt + ")x" + orderData.get(i).option.get(j).count + ",";
                        }
                    }

                }

            }
        }

        receiptInfoRow.sumPrice = ConvertData.getPrice(sum);
        receiptInfoRow.sumMenu = sumMenu;

        return receiptInfoRow;
    }
}
