/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tws.soul.soulbrown.gcm;

import com.app.define.LOG;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tws.common.lib.gms.LocationDefines;
import com.tws.soul.soulbrown.R;
import com.tws.soul.soulbrown.broadcast.AlarmManagerBroadcastReceiver;
import com.tws.soul.soulbrown.geofence.GeofenceClient;
import com.tws.soul.soulbrown.pref.PrefOrderInfo;
import com.tws.soul.soulbrown.ui.SoulBrownMainActivity;
import com.tws.soul.soulbrown.ui.SplashActivity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.URLDecoder;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {

    public static final String GCM_BROADCAST = "Gcm_refresh";

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM soul brown";

    private GeofenceClient geofenceClient;

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                /*
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                */

                String msg = extras.getString("msg");
                String pushflag = extras.getString("pushflag");
                String status = extras.getString("status");

                LOG.d("GcmIntentService msg : "+ msg +" pushflag : "+pushflag+" status : "+status);

                String decMsg = "";
                try
                {
                    decMsg =  URLDecoder.decode(msg,"utf-8");

                }catch (Exception e)
                {
                    decMsg = "";
                }


                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.

                if(!TextUtils.isEmpty(decMsg)) {
                    sendNotification(decMsg);

                    Intent intentGcm = new Intent(GCM_BROADCAST);

                    intentGcm.putExtra("msg",decMsg);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(intentGcm);


                    // alarm , geofence off
                    if(pushflag.equals("chgorder") && status.equals("1")) {

                        LOG.d("GcmIntentService alarm , geofence off");

                        PrefOrderInfo prefOrderInfo = new PrefOrderInfo(this);
                        prefOrderInfo.setArriveTime(0);

                        AlarmManagerBroadcastReceiver alarmManagerBroadcastReceiver = new AlarmManagerBroadcastReceiver();
                        alarmManagerBroadcastReceiver.cancelAlarm(this);

                        geofenceClient = new GeofenceClient(this, GeofenceResultHandler );
                    }

                }
                Log.i(TAG, "Received: " + decMsg);

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    public Handler GeofenceResultHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case LocationDefines.GMS_CONNECT_SUCC:
                    Log.i("LocationResultHandler", "GMS_CONNECT_SUCC");

                    if (geofenceClient != null)
                        geofenceClient.stopGeofence();


                    break;
            }

        }
    };

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.icon_coffeecup_white)
        .setContentTitle("Soul Brown")
        .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(msg))
        .setAutoCancel(true)
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
