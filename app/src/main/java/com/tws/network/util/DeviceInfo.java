package com.tws.network.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by jonychoi on 14. 12. 10..
 */
public class DeviceInfo {

    public static String getIMEI(Context context)
    {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        String imei =tm.getDeviceId();

        return imei;

    }

    public static String getMDN(Context context)
    {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        String mdn =tm.getLine1Number();

        if(!TextUtils.isEmpty(mdn))
        {
            if(mdn.length() > 11)
                mdn = mdn.replace("+82","0");
        }

        return mdn;

    }

    public static String getAuth(Context context)
    {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        String mdn =tm.getLine1Number();
        String imei =tm.getDeviceId();

        // auth ver2
        com.thinkware.air.auth.INaviAirAuth auth = com.thinkware.air.auth.INaviAirAuth
                .getInstance(Build.MODEL, imei, mdn);

        String strAuth = "";
        if (auth != null) strAuth = auth.getAuthString();

       return strAuth;

    }
}
