package com.layoutstry.android.trythisloyout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Parth on 27-07-2015.
 * Alarm is set/scheduled whenever boot_completed
 */
public class WisherAlarmSetterReceiver extends BroadcastReceiver {
    //private PendingIntent pendingIntent;
    public static boolean isScheduled = false;
    //private AlarmManager alarmMgr;
    //private PendingIntent alarmIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            scheduleWisherAlarm(context);
        }

    }

    public static void scheduleWisherAlarm(Context context){
        final AlarmManager alarmMgr;
        final PendingIntent alarmIntent;

        // Set the alarm here.
        alarmMgr=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent wishIntent = new Intent(context , WisherManagerReceiver.class);

        wishIntent.setAction(HomeFragment.PACKAGENAME_ACTION);
        alarmIntent = PendingIntent.getBroadcast(context, 0, wishIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set the alarm to start at 4 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 5);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                alarmIntent);
        isScheduled = true;
    }

}
