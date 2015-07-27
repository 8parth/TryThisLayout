package com.layoutstry.android.trythisloyout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

/**
 * Created by User on 26-07-2015.
 * BroadcastReceiver that will that receive Intents
 */

public class WisherManagerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Time now = new Time();
        now.setToNow();

        String action = intent.getAction();
        if(HomeFragment.PACKAGENAME_ACTION.equals(action)){
            Intent serviceIntent = new Intent(context, WisherManagerService.class);
            context.startService(serviceIntent);
        }
    }
}
//Receiver for resetting alarms whenever the phone has been shut down
/*
public class AlarmSetter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // get preferences
        SharedPreferences preferences = context.getSharedPreferences("name_of_your_pref", 0);
        Map<String, ?> scheduleData = preferences.getAll();

        // set the schedule time
        if(scheduleData.containsKey("fromHour") && scheduleData.containsKey("toHour")) {
            int fromHour = (Integer) scheduleData.get("fromHour");
            int fromMinute = (Integer) scheduleData.get("fromMinute");

            int toHour = (Integer) scheduleData.get("toHour");
            int toMinute = (Integer) scheduleData.get("toMinute");

            //Do some action
        }
    }

}*/
