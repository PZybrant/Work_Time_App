package com.example.patryk.work_time_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.example.patryk.work_time_app.broadcast_receivers.ReminderReceiver;
import com.example.patryk.work_time_app.fragments.HistoryFragment;
import com.example.patryk.work_time_app.fragments.SettingsFragment;
import com.example.patryk.work_time_app.fragments.TimerFragment;

import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private HistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerNotificationChannels();
        checkFirstRun();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        TimerFragment timerFragment = new TimerFragment();

        fragmentTransaction.add(R.id.fragmentContainer, timerFragment);
        fragmentTransaction.commit();

        SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference_name), MODE_PRIVATE);
        if (preferences.getLong(getString(R.string.init_date), -1) == -1) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(getString(R.string.init_date), new GregorianCalendar().getTimeInMillis());
            editor.apply();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        System.out.println("onStop");

        Intent reminderIntent = new Intent(this, ReminderReceiver.class);
        reminderIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                reminderIntent,
                PendingIntent.FLAG_NO_CREATE);

        if (reminderPendingIntent == null) {
            System.out.println("onStop: Alarm doesn't exist");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentTransaction fragmentTransaction;

        switch (item.getItemId()) {
            case R.id.action_timer:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                TimerFragment timerFragment = new TimerFragment();
                fragmentTransaction.replace(R.id.fragmentContainer, timerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;

            case R.id.action_history:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                historyFragment = new HistoryFragment();
                fragmentTransaction.replace(R.id.fragmentContainer, historyFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;

            case R.id.action_settings:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                SettingsFragment settingsFragment = new SettingsFragment();
                fragmentTransaction.replace(R.id.fragmentContainer, settingsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void registerNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.reminder_channel_name);
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.reminder_channel_id), name, importance);
            channel.setDescription(description);
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkFirstRun() {
        final String PREF_VERSION_CODE_KEY = "version_code";

        final int DOESNT_EXIST = -1;

        int currentVersionCode = BuildConfig.VERSION_CODE;

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        int savedVersionCode = preferences.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        if (currentVersionCode == savedVersionCode) {
            // Normal run
//            registerNotificationAlarm();
        } else if (savedVersionCode == DOESNT_EXIST) {
            // First run
//            registerNotificationAlarm();
        } else if (currentVersionCode > savedVersionCode) {
            // Updated run
//            registerNotificationAlarm();
        }
        preferences.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }
}
