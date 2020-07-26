package com.example.patryk.work_time_app.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.patryk.work_time_app.MainActivity;
import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.broadcast_receivers.CancelReceiver;
import com.example.patryk.work_time_app.broadcast_receivers.SnoozeReceiver;

public abstract class NotificationUtil {

    private static final int REMINDER_NOTIFICATION_ID = 684;
    private static final int SNOOZE_REQUEST_CODE = 0;
    private static final int CONTENT_REQUEST_CODE = 1;
    private static final int CANCEL_REQUEST_CODE = 2;
    private static final int FLAGS = 0;

    public static void sendReminder(Context applicationContext) {

        Intent contentIntent = new Intent(applicationContext, MainActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                applicationContext,
                CONTENT_REQUEST_CODE,
                contentIntent,
                FLAGS);

        Intent cancelIntent = new Intent(applicationContext, CancelReceiver.class);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                CANCEL_REQUEST_CODE,
                cancelIntent,
                FLAGS
        );

        Intent snoozeIntent = new Intent(applicationContext, SnoozeReceiver.class);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                SNOOZE_REQUEST_CODE,
                snoozeIntent,
                FLAGS
        );
        final String CHANNEL_ID = applicationContext.getString(R.string.reminder_channel_id);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_access_time_24dp)
                .setContentTitle(applicationContext.getString(R.string.reminder_title))
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_access_time_24dp, "Cancel", cancelPendingIntent)
                .addAction(R.drawable.ic_access_time_24dp, "Remind later.", snoozePendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(applicationContext);
        notificationManager.notify(REMINDER_NOTIFICATION_ID, builder.build());
    }

    public static void cancelReminderNotification(Context applicationContext) {
        NotificationManagerCompat.from(applicationContext).cancel(REMINDER_NOTIFICATION_ID);
    }

    public static void cancelAll(Context applicationContext) {
        NotificationManagerCompat.from(applicationContext).cancelAll();

    }
}
