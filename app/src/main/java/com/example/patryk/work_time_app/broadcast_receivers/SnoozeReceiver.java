package com.example.patryk.work_time_app.broadcast_receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.format.DateUtils;

import androidx.core.app.AlarmManagerCompat;

import com.example.patryk.work_time_app.util.NotificationUtil;

public class SnoozeReceiver extends BroadcastReceiver {

    private static final int REQUEST_CODE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        long triggerTime = SystemClock.elapsedRealtime() + (DateUtils.MINUTE_IN_MILLIS * 10);

        Intent remindIntent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                remindIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent);

        NotificationUtil.cancelReminderNotification(context);
    }
}
