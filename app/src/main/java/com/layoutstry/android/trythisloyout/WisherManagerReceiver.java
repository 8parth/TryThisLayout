package com.layoutstry.android.trythisloyout;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.format.Time;

/**
 * Created by user on 26-07-2015.
 * BroadcastReceiver that will that receive Intents
 */

public class WisherManagerReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Time now = new Time();
        now.setToNow();

        String action = intent.getAction();
        if(HomeFragment.PACKAGENAME_ACTION.equals(action)){

            Intent serviceIntent = new Intent(context, WisherManagerService.class);
            //context.startService(serviceIntent);
            startWakefulService(context, serviceIntent);
            //WisherAlarmSetterReceiver.scheduleWisherAlarm(context);
            if(!WisherAlarmSetterReceiver.isScheduled){
                WisherAlarmSetterReceiver.scheduleWisherAlarm(context);
            }
        }
    }
}

