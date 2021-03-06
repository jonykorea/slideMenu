package com.brewbrew.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.app.define.LOG;
import com.tws.common.lib.utils.FileLOG;
import com.brewbrew.managers.pref.PrefOrderInfo;
import com.brewbrew.managers.pref.PrefUserInfo;
import com.brewbrew.service.LocationService;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    public AlarmManagerBroadcastReceiver() {
    }

    final public static String ALARM_REPEAT = "repeat";


    @Override
    public void onReceive(Context context, Intent intent) {

        FileLOG.writeLog("AlarmManagerBroadcastReceiver : onReceive");

        //PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "soulbrown");
        //Acquire the lock
        //wl.acquire();

        //You can do the processing here.
        Bundle extras = intent.getExtras();

        boolean isRepeat = true;

        if (extras != null) {
            //Make sure this intent has been sent by the one-time timer button.

            //isRepeat = extras.getBoolean(ALARM_REPEAT, Boolean.FALSE);

            LOG.d("onReceive isRepeat : "+ isRepeat);


            // check pref!
            PrefOrderInfo prefOrderInfo = new PrefOrderInfo(context);
            long arriveUnixTime = prefOrderInfo.getArriveTime();
            long calcUnixTime = arriveUnixTime - System.currentTimeMillis();
            if (calcUnixTime >= 0) {

                // check id
                PrefUserInfo prefUserInfo = new PrefUserInfo(context);
                String userID = prefUserInfo.getUserID();
                if (TextUtils.isEmpty(userID))
                    return;

                //repeat 60 sec
                if (isRepeat) {
                    setRepeatTimer(context, 60);
                }

                // test Service

                Intent intentSvc = new Intent(context, LocationService.class);

                context.startService(intentSvc);

                //Intent intentSvc = new Intent(context,LocationIntentService.class);

                //ComponentName comp = new ComponentName(context.getPackageName(),
                //       LocationIntentService.class.getName());
                // Start the service, keeping the device awake while it is launching.

                FileLOG.writeLog("AlarmManagerBroadcastReceiver : startWakefulService");

                //startWakefulService(context, (intent.setComponent(comp)));
                //setResultCode(Activity.RESULT_OK);
            }
            else
            {

            }


        }

        //Release the lock
        //wl.release();
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ALARM_REPEAT, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 60 * 20seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 20, pi);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


    public void setRepeatTimer(Context context, long time) {

        LOG.d("setRepeatTimer time : "+ time);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ALARM_REPEAT, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * time, pi);

    }

    /*
    public void setFirstTimer(Context context, long time) {

        LOG.d("setFirstTimer time : "+ time);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ALARM_REPEAT, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1004, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * time, pi);
    }
    */
}
