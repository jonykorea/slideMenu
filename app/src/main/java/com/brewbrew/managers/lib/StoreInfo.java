package com.brewbrew.managers.lib;

import com.brewbrew.R;

/**
 * Created by jonychoi on 15. 1. 13..
 */
public class StoreInfo {

    public static double STORE_LATI = 37.402104;
    public static double STORE_LON = 127.110381;

    public static String CODE_HARU = "PGA000001";
    public static String CODE_1022 = "PGA000002";
    public static String CODE_2FLAT = "PGA000003";

    // test store
    public static String CODE_TWS = "PGA000004";

    public static int getStoreName(String code)
    {
        if( code.equals(CODE_HARU))
        {
            return R.string.store_haru;
        }
        else if( code.equals(CODE_1022))
        {
            return R.string.store_1022;
        }
        else if( code.equals(CODE_2FLAT))
        {
            return R.string.store_2flat;
        }
        else if( code.equals(CODE_TWS))
        {
            return R.string.store_tws;
        }

        return -1;
    }

}
