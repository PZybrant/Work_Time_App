package com.example.patryk.work_time_app.broadcast_receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.patryk.work_time_app.R;

import java.util.Calendar;

public class OnDeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String PREF_HOUR = "pref_hour";
        final String PREF_MINUTE = "pref_minute";
        final int REQUEST_CODE = Integer.getInteger(context.getString(R.string.reminder_intent_request_code));

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isWorkReminderOn = defaultSharedPreferences.getBoolean("work_reminder", false);

        int hour = defaultSharedPreferences.getInt(PREF_HOUR, 8);
        int minute = defaultSharedPreferences.getInt(PREF_MINUTE, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Calendar temp = calendar;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        if (calendar.before(temp)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent reminderIntent = new Intent(context, ReminderReceiver.class);
        PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = context.getSystemService(AlarmManager.class);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, reminderPendingIntent);
    }
}
