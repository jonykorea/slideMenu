package com.tws.soul.soulbrown;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.define.LOG;
import com.tws.common.lib.gms.LocationDefines;
import com.tws.common.lib.gms.LocationGMS;
import com.tws.network.data.CoreGetPublicKey;
import com.tws.network.data.RetCode;
import com.tws.network.lib.ApiAgent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class LocationIntentService extends IntentService {

    PowerManager.WakeLock wl;
    @Override
    protected void onHandleIntent(Intent intent) {

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "sb service");
        //Acquire the lock
        wl.acquire();

        uploadLocation();
    }

    public LocationIntentService() {
        super("LocationIntentService");
    }


    private void uploadLocation() {



        LocationGMS location = new LocationGMS(this,
                LocationResultHandler);

        location.connect();
    }

    public Handler LocationResultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


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

    // apiSetUserLoc
    public void apiSetUserLoc(Location location) {

        ApiAgent api = new ApiAgent();

        LOG.d("apiSetUserLoc");

        String lon = Double.toString(location.getLongitude());
        String lat = Double.toString(location.getLatitude());

        if (api != null) {
            api.apiUserLoc(this, "userid", "lon", "lat", new Response.Listener<RetCode>() {
                @Override
                public void onResponse(RetCode retCode) {

                    wl.release();

                    LOG.d("r_public.result : " + retCode.result);
                    LOG.d("r_public.errormsg : " + retCode.errormsg);


                    if (retCode.result == 1) {

                        // success

                        LOG.d("apiSetUserLoc Succ");


                    } else {
                        // fail
                        LOG.d("apiSetUserLoc Fail "+retCode.result );

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    wl.release();

                    LOG.d("apiSetUserLoc VolleyError " + volleyError.getMessage());

                }
            });
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LOG.d("LocationIntentService onCreate ");
    }

    @Override
    public void onDestroy() {
        LOG.d("LocationIntentService onDestroy ");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
