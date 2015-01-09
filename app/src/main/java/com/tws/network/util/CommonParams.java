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

    public static String mapVersion = "1.1.0";

    public static JSONObject getCommonParams(Context context,JSONObject json) {

        String appVersion = "1.0.0";
        String macAddr = "0";

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        macAddr = wifiManager.getConnectionInfo().getMacAddress();

        try
        {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            appVersion = info.versionName;


        }catch (PackageManager.NameNotFoundException e)
        {
            LOG.d("getCommonParams error:" + e.getMessage());
        }

        try {
            json.put("model",Build.MODEL);
            json.put("phone",DeviceInfo.getMDN(context));
            json.put("reqAppID", "TW_AIR_ANDROID");
            json.put("groupType","AIR");
            json.put("etcInfo", DeviceInfo.getIMEI(context));
            json.put("mac", macAddr);
            json.put("systemVersion", Build.VERSION.RELEASE);
            json.put("appVersion", appVersion);
            json.put("mapVersion", mapVersion);
            json.put("serialNumber", "NULL");

        } catch (Exception e) {
            LOG.d("getCommonParams error:"+e.getMessage());
        }

        return json;
    }


    public void setMapVersion(String version) {
        mapVersion = version;
    }


}
