package com.tws.soul.soulbrown.broadcast;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.tws.soul.soulbrown.LocationIntentService;
import com.tws.soul.soulbrown.gcm.GcmIntentService;
import com.tws.soul.soulbrown.pref.PrefOrderInfo;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmManagerBroadcastReceiver extends WakefulBroadcastReceiver {
    public AlarmManagerBroadcastReceiver() {
    }

    final public static String ONE_TIME = "onetime";


    @Override
    public void onReceive(Context context, Intent intent) {

        //PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "soulbrown");
        //Acquire the lock
        //wl.acquire();

        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ");
        }

        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));

        Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

        // check pref!
        PrefOrderInfo prefOrderInfo = new PrefOrderInfo(context);
        long arriveUnixTime = prefOrderInfo.getArriveTime();
        long calcUnixTime = arriveUnixTime - System.currentTimeMillis();
        if (calcUnixTime >= 0) {
            //repeat 60 sec
            setOnetimeTimer(context, 60);

            // test Service
            //Intent intentSvc = new Intent(context,LocationIntentService.class);

            ComponentName comp = new ComponentName(context.getPackageName(),
                    LocationIntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);

        }

        //Release the lock
        //wl.release();
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
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

    public void setOnetimeTimer(Context context, long time) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * time, pi);
    }
}
