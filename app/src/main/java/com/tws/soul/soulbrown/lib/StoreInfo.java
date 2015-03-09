package com.tws.soul.soulbrown.lib;

import android.content.Context;

import com.tws.soul.soulbrown.R;

/**
 * Created by jonychoi on 15. 1. 13..
 */
public class StoreInfo {

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
