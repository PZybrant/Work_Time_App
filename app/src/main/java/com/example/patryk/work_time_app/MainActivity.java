package com.example.patryk.work_time_app;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.patryk.work_time_app.broadcast_receivers.ReminderReceiver;
import com.example.patryk.work_time_app.fragments.FilterDialog;
import com.example.patryk.work_time_app.fragments.HistoryFragment;
import com.example.patryk.work_time_app.fragments.SettingsFragment;
import com.example.patryk.work_time_app.fragments.TimerFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements FilterDialog.FilterDialogFragmentListener {

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

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (preferences.getLong(getString(R.string.init_date), -1) == -1) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(getString(R.string.init_date), new GregorianCalendar().getTimeInMillis());
            editor.apply();
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

    @Override
    public void onApplyButtonClick(DialogFragment dialogFragment, long date1, long date2) {
        Bundle bundle = new Bundle();
        bundle.putLong("from", date1);
        bundle.putLong("to", date2);
        historyFragment.setArguments(bundle);
        historyFragment.update(bundle);
    }

    private void registerNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.reminder_channel_id);
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.reminder_channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
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
            registerNotificationAlarm();
        } else if (savedVersionCode == DOESNT_EXIST) {
            // First run
            registerNotificationAlarm();
        } else if (currentVersionCode > savedVersionCode) {
            // Updated run
        }
        preferences.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private void registerNotificationAlarm() {
        final String PREF_HOUR = "pref_hour";
        final String PREF_MINUTE = "pref_minute";
        final int REQUEST_CODE = Integer.parseInt(getString(R.string.reminder_intent_request_code));

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isWorkReminderOn = defaultSharedPreferences.getBoolean("work_reminder", false);

        if (isWorkReminderOn) {
            int hour = defaultSharedPreferences.getInt(PREF_HOUR, 8);
            int minute = defaultSharedPreferences.getInt(PREF_MINUTE, 0);
            Calendar temp = Calendar.getInstance();
            temp.setTimeInMillis(System.currentTimeMillis());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            SharedPreferences.Editor edit = defaultSharedPreferences.edit();
            edit.putInt(PREF_HOUR, hour);
            edit.putInt(PREF_MINUTE, minute);
            edit.apply();

            if (calendar.before(temp)) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            Intent reminderIntent = new Intent(this, ReminderReceiver.class);
            PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                    this,
                    REQUEST_CODE,
                    reminderIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = getSystemService(AlarmManager.class);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, reminderPendingIntent);
        }
    }

}
