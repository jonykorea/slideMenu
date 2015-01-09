package com.tws.common.lib.gms;

/**
 * Created by jonychoi on 15. 1. 9..
 */
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/* readme. jony

 >sample main code.

 LocationGMS location = new LocationGMS(MainActivity.this,
 LocationResultHandler);

 location.connect();

 > sample handler.

 public Handler LocationResultHandler = new Handler() {
 @Override
 public void handleMessage(Message msg) {

 if (isFinishing())
 return;

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
 } else {

 }

 break;
 case LocationDefines.GMS_LOCATION_FAIL:
 Log.i("LocationResultHandler", "GMS_LOCATION_FAIL");
 break;

 }

 }
 };
 */

public class LocationGMS implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final int EXPIRE_TIME = 5000;
    // locations objects
    LocationClient mLocationClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;

    private Handler handler;

    Context context;
    private Runnable timeoutRun = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(LocationDefines.GMS_LOCATION_TIMEOUT);
            if(mLocationClient.isConnecting() || mLocationClient.isConnected()){
                mLocationClient.disconnect();
            }
        }
    };

    private Handler mExpireHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        };
    };

    public LocationGMS(Context context, Handler handler) {
        // TODO Auto-generated constructor stub

        this.context = context;
        this.handler = handler;

        mLocationClient = new LocationClient(context, this, this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(1000 * 2);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(1000 * 1);

//		mLocationRequest.setExpirationDuration(1000 * EXPIRE_TIME);

        mLocationRequest.setNumUpdates(1);
    }

    public void connect() {
        mLocationClient.connect();
    }

    public void disconnect() {
        mLocationClient.disconnect();
    }

    @Override
    public void onDisconnected() {

        handler.sendEmptyMessage(LocationDefines.GMS_DISCONNECT_SUCC);
    }

    // LocationListener
    @Override
    public void onLocationChanged(Location location) {

        mExpireHandler.removeCallbacks(timeoutRun);
        mCurrentLocation = mLocationClient.getLastLocation();

        Message msg = handler.obtainMessage();

        msg.what = LocationDefines.GMS_LOCATION_SUCC;
        msg.obj = mCurrentLocation;

        handler.sendMessage(msg);

        mLocationClient.disconnect();
    }

    // GooglePlayServicesClient.ConnectionCallbacks
    @Override
    public void onConnected(Bundle arg0) {

        handler.sendEmptyMessage(LocationDefines.GMS_CONNECT_SUCC);


        if (mLocationClient != null)
        {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
            mExpireHandler.postDelayed(timeoutRun, EXPIRE_TIME);
        }

        if (mLocationClient != null) {
            // get location
            mCurrentLocation = mLocationClient.getLastLocation();
            try {

                Message msg = handler.obtainMessage();

                msg.what = LocationDefines.GMS_LAST_LOCATION_SUCC;
                msg.obj = mCurrentLocation;

                handler.sendMessage(msg);

            } catch (NullPointerException npe) {

                handler.sendEmptyMessage(LocationDefines.GMS_LOCATION_NEED_SETTING);
            }
        } else {
            handler.sendEmptyMessage(LocationDefines.GMS_CONNECT_FAIL);
        }

    }
    // GooglePlayServicesClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        handler.sendEmptyMessage(LocationDefines.GMS_CONNECT_FAIL);

    }


}
