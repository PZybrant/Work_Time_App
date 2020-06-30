package com.example.patryk.work_time_app.broadcast_receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.patryk.work_time_app.util.NotificationUtil;

import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isSaturdayNotificationOn = preferences.getBoolean("saturday_reminder_switch", false);
        boolean isSundayNotificationOn = preferences.getBoolean("sunday_reminder_switch", false);

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            if (isSaturdayNotificationOn) {
                NotificationUtil.sendReminder(context);
            }
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            if (isSundayNotificationOn) {
                NotificationUtil.sendReminder(context);
            }
        } else {
            NotificationUtil.sendReminder(context);
        }


    }
}
