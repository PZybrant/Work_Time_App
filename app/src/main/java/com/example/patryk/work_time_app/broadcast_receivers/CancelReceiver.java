package com.example.patryk.work_time_app.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.patryk.work_time_app.util.NotificationUtil;

public class CancelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtil.cancelReminderNotification(context);
    }
}
