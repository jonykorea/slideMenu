package com.tws.soul.soulbrown.lib;

import android.text.TextUtils;

import java.text.DecimalFormat;

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
}
