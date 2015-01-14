package com.tws.soul.soulbrown.lib;

import java.text.DecimalFormat;

/**
 * Created by jonychoi on 15. 1. 12..
 */
public class ConvertPrice {

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
}
