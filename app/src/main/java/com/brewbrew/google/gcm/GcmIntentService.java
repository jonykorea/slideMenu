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

package com.brewbrew.google.gcm;

import com.app.define.LOG;
import com.brewbrew.broadcast.AlarmManagerBroadcastReceiver;
import com.brewbrew.google.geofence.GeofenceClient;
import com.brewbrew.managers.pref.PrefOrderInfo;
import com.brewbrew.managers.pref.PrefStoreInfo;
import com.brewbrew.service.AlarmNotiService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tws.common.lib.gms.LocationDefines;
import com.tws.common.lib.mgr.WakeupMgr;
import com.tws.common.lib.soulbrownlib.SoundManager;
import com.brewbrew.R;
import com.brewbrew.views.SplashActivity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
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
    public static final String TAG = "GCM soul brown";
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;
    private GeofenceClient geofenceClient;
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

    public GcmIntentService() {
        super("GcmIntentService");
    }

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
                sendNotification("Send error: " + extras.toString(),true);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString(),true);
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
                String pushFlag = extras.getString("pushflag");
                int status = extras.getInt("status");

                LOG.d("GcmIntentService msg : " + msg + " pushFlag : " + pushFlag + " status : " + status);

                String decMsg = "";
                try {
                    decMsg = URLDecoder.decode(msg, "utf-8");

                } catch (Exception e) {
                    decMsg = "";
                }


                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.

                if (!TextUtils.isEmpty(decMsg)) {



                   //sendNotification(decMsg,true);

                    Intent intentGcm = new Intent(GCM_BROADCAST);

                    intentGcm.putExtra("msg", decMsg);

                    LocalBroadcastManager.getInstance(this).sendBroadcast(intentGcm);


                    // alarm , geofence off
                    if (pushFlag.equals(GcmDefine.PUSH_CHG_ORDER) ) {

                        sendNotification(decMsg,true);
                        //if( status == 1 ) {
                            LOG.d("GcmIntentService alarm , geofence off");

                            PrefOrderInfo prefOrderInfo = new PrefOrderInfo(this);
                            prefOrderInfo.setArriveTime(0);
                            prefOrderInfo.setOrderStore("");

                            AlarmManagerBroadcastReceiver alarmManagerBroadcastReceiver = new AlarmManagerBroadcastReceiver();
                            alarmManagerBroadcastReceiver.cancelAlarm(this);

                            geofenceClient = new GeofenceClient(this, GeofenceResultHandler);
                        //}
                    }else if(pushFlag.equals(GcmDefine.PUSH_CANCEL_ORDER) || pushFlag.equals(GcmDefine.PUSH_APPROACH_USER) || pushFlag.equals(GcmDefine.PUSH_NEW_ORDER))
                    {
                        // alarm service call
                        sendNotification(decMsg,false);

                        Intent intentSvc = new Intent(this, AlarmNotiService.class);

                        this.startService(intentSvc);

                    }else if(pushFlag.equals(GcmDefine.PUSH_CHG_PUSHKEY))
                    {
                        sendNotification(decMsg,true);
                        setPushStatus(0);
                    }

                }
                Log.i(TAG, "Received: " + decMsg);

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private SoundManager mSoundManager;
    private Vibrator mVib;

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOG.d("GcmIntentService onDestroy");
    }

    private void initExtraAlarm()
    {

    }


    @Override
    public void onCreate() {
        super.onCreate();

        LOG.d("GcmIntentService onCreate");

    }


    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, boolean isUser) {

        WakeupMgr wakeupMgr;
        wakeupMgr = new WakeupMgr(this);

        wakeupMgr.setPowerWakeUp(4);

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.push_logo_s)
                        .setContentTitle(getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setContentText(msg);

        if(isUser) {
            long[] patten = {0, 1000};
            mBuilder.setVibrate(patten);
            mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/buru_user_100"));
        }
        mBuilder.setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        wakeupMgr.releaseWifiManager();
    }

    private void setPushStatus(int status)
    {
        Intent intentGcm = new Intent("push_status");

        intentGcm.putExtra("status", status);

        PrefStoreInfo prefStoreInfo = new PrefStoreInfo(this);
        prefStoreInfo.setPushStatus(status);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intentGcm);
    }




}
