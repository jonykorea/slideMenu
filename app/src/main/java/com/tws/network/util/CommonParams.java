package com.tws.network.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.app.define.LOG;

import org.json.JSONObject;

/**
 * Created by jonychoi on 14. 12. 10..
 */
public class CommonParams {

    public static JSONObject getCommonParams(Context context,JSONObject json) {

        try {
            json.put("source","USERUI");


        } catch (Exception e) {
            LOG.d("getCommonParams error:"+e.getMessage());
        }

        return json;
    }

}
