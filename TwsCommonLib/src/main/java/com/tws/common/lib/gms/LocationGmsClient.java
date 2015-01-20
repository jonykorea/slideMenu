package com.tws.common.lib.gms;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by jonychoi on 15. 1. 20..
 */

/* readme. jony

 >sample main code.

 LocationGmsClient location = new LocationGmsClient(MainActivity.this,
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

public class LocationGmsClient implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Handler callbackHandler;

    protected static final String TAG = "location-updates-sample";

    private static final int EXPIRE_TIME = 12000;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 6000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    private Context context;

    public LocationGmsClient(Context context, Handler handler) {

        this.context = context;
        this.callbackHandler = handler;

        buildGoogleApiClient();

    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationRequest.setExpirationDuration(EXPIRE_TIME);

        mLocationRequest.setNumUpdates(1);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        mExpireHandler.postDelayed(timeoutRun, EXPIRE_TIME);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        callbackHandler.sendEmptyMessage(LocationDefines.GMS_CONNECT_SUCC);

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mGoogleApiClient != null) {

            startLocationUpdates();

            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            try {

                Message msg = callbackHandler.obtainMessage();

                msg.what = LocationDefines.GMS_LAST_LOCATION_SUCC;
                msg.obj = mCurrentLocation;

                callbackHandler.sendMessage(msg);

            } catch (NullPointerException npe) {

                callbackHandler.sendEmptyMessage(LocationDefines.GMS_LOCATION_NEED_SETTING);
            }

        }


    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i("jony", "onLocationChanged");

        mExpireHandler.removeCallbacks(timeoutRun);
        mCurrentLocation = location;

        Message msg = callbackHandler.obtainMessage();

        msg.what = LocationDefines.GMS_LOCATION_SUCC;
        msg.obj = mCurrentLocation;

        callbackHandler.sendMessage(msg);

        if (mGoogleApiClient != null && (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected())) {
            mGoogleApiClient.disconnect();
        }

        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        //Toast.makeText(this, getResources().getString(R.string.location_updated_message),
        //       Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        //mGoogleApiClient.connect();
        /*
        int CAUSE_SERVICE_DISCONNECTED = 1;
        int CAUSE_NETWORK_LOST = 2;
         */
        if( cause == CAUSE_SERVICE_DISCONNECTED)
            callbackHandler.sendEmptyMessage(LocationDefines.GMS_DISCONNECT_SUCC);
        else
            callbackHandler.sendEmptyMessage(LocationDefines.GMS_CONNECT_FAIL);

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        callbackHandler.sendEmptyMessage(LocationDefines.GMS_CONNECT_FAIL);

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    private Handler mExpireHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        };
    };

    private Runnable timeoutRun = new Runnable() {


        @Override
        public void run() {

            callbackHandler.sendEmptyMessage(LocationDefines.GMS_LOCATION_TIMEOUT);
            if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    };
}
