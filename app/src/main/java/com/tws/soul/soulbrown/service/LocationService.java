package com.tws.soul.soulbrown.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.tws.common.lib.gms.LocationDefines;
import com.tws.common.lib.gms.LocationGmsClient;
import com.tws.common.lib.mgr.WakeupMgr;
import com.tws.common.lib.utils.FileLOG;
import com.tws.network.data.RetCode;
import com.tws.network.data.ServerDefineCode;
import com.tws.network.lib.ApiAgent;
import com.tws.soul.soulbrown.pref.PrefUserInfo;

public class LocationService extends Service {

    private WakeupMgr wakeupMgr;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        FileLOG.writeLog("LocationService : onStartCommand");

        wakeupMgr.setPowerWakeUp(1);

        uploadLocation();


        return Service.START_NOT_STICKY;

    }

    private void uploadLocation() {

        FileLOG.writeLog("LocationService : uploadLocation");

        LocationGmsClient location = new LocationGmsClient(this,
                LocationResultHandler);

        location.connect();
    }

    // apiSetUserLoc
    public void apiSetUserLoc(Location location) {

        FileLOG.writeLog("LocationService : apiSetUserLoc");

        ApiAgent api = new ApiAgent();

        LOG.d("apiSetUserLoc");

        String lon = Double.toString(location.getLongitude());
        String lat = Double.toString(location.getLatitude());

        PrefUserInfo prefUserInfo = new PrefUserInfo(this);

        String userID = prefUserInfo.getUserID();

        if (api != null && !TextUtils.isEmpty(userID)) {
            api.apiUserLoc(this, userID, lon, lat, new Response.Listener<RetCode>() {
                @Override
                public void onResponse(RetCode retCode) {

                    FileLOG.writeLog("LocationService : apiUserLoc retCode : " + retCode.result);

                    LOG.d("r_public.result : " + retCode.result);
                    LOG.d("r_public.errormsg : " + retCode.errormsg);

                    if (retCode.result == ServerDefineCode.NET_RESULT_SUCC) {

                        // success

                        LOG.d("apiSetUserLoc Succ");


                    } else {
                        // fail
                        LOG.d("apiSetUserLoc Fail " + retCode.result);

                    }

                    wakeupMgr.releaseWifiManager();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    FileLOG.writeLog("LocationService : apiUserLoc volleyError.getMessage()");

                    wakeupMgr.releaseWifiManager();

                    LOG.d("apiSetUserLoc VolleyError " + volleyError.getMessage());

                }
            });
        }
    }

    public Handler LocationResultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            FileLOG.writeLog("LocationService : LocationResultHandler : " + msg.what);

            switch (msg.what) {

                case LocationDefines.GMS_CONNECT_SUCC:
                    Log.i("LocationResultHandler", "GMS_CONNECT_SUCC");

                    break;
                case LocationDefines.GMS_CONNECT_FAIL:
                    Log.i("LocationResultHandler", "GMS_CONNECT_FAIL");

                    break;
                case LocationDefines.GMS_DISCONNECT_SUCC:
                    Log.i("LocationResultHandler", "GMS_DISCONNECT_SUCC");

                    break;
                case LocationDefines.GMS_LOCATION_NEED_SETTING:
                    Log.i("LocationResultHandler", "GMS_LOCATION_NEED_SETTING");
                    break;
                case LocationDefines.GMS_LOCATION_SUCC:
                    Log.i("LocationResultHandler", "GMS_LOCATION_SUCC");

                    if (msg.obj != null) {
                        Location location = (Location) msg.obj;

                        Log.i("LocationResultHandler", "location getAccuracy : "
                                + location.getAccuracy());
                        Log.i("LocationResultHandler", "location getLongitude : "
                                + location.getLongitude());
                        Log.i("LocationResultHandler", "location getLatitude : "
                                + location.getLatitude());

                        apiSetUserLoc(location);

                    } else {

                    }

                    break;
                case LocationDefines.GMS_LOCATION_FAIL:
                    Log.i("LocationResultHandler", "GMS_LOCATION_FAIL");
                    break;

            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LOG.d("LocationService onCreate ");

        FileLOG.writeLog("LocationService : onCreate");

        wakeupMgr = new WakeupMgr(this);

    }

    @Override
    public void onDestroy() {
        LOG.d("LocationService onDestroy ");

        FileLOG.writeLog("LocationService : onDestroy");

        super.onDestroy();
    }
}
