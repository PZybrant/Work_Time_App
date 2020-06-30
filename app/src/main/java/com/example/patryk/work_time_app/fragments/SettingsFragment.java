package com.example.patryk.work_time_app.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.broadcast_receivers.ReminderReceiver;
import com.example.patryk.work_time_app.viewmodels.SettingsFragmentViewModel;

import java.util.Calendar;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat workReminderSwitch, saturdayReminderSwitch, sundayReminderSwitch, darkModeSwitch;
    private Preference workTimeBeginEditText, removeDb, feedback;

    private SharedPreferences settingPreferences, sharedPreferences;
    private SettingsFragmentViewModel viewModel;
    private boolean isWorkReminderOn;
    private long workId;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        settingPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        init();
        viewModel = new ViewModelProvider(this).get(SettingsFragmentViewModel.class);
        isWorkReminderOn = settingPreferences.getBoolean(workReminderSwitch.getKey(), false);
        showHideEditText();

        workReminderSwitch.setOnPreferenceChangeListener((preference, newValue) -> {

            final String PREF_HOUR = "pref_hour";
            final String PREF_MINUTE = "pref_minute";
            final int REQUEST_CODE = Integer.parseInt(getString(R.string.reminder_intent_request_code));
            int hour = settingPreferences.getInt(PREF_HOUR, 8);
            int minute = settingPreferences.getInt(PREF_MINUTE, 0);

            Calendar temp = Calendar.getInstance();
            temp.setTimeInMillis(System.currentTimeMillis());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            if (calendar.before(temp)) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            isWorkReminderOn = (Boolean) newValue;
            showHideEditText();

            Intent reminderIntent = new Intent(getContext(), ReminderReceiver.class);
            PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                    getContext(),
                    REQUEST_CODE,
                    reminderIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = getContext().getSystemService(AlarmManager.class);

            if (isWorkReminderOn) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, reminderPendingIntent);
            } else {
                alarmManager.cancel(reminderPendingIntent);
            }

            return true;
        });

        workTimeBeginEditText.setOnPreferenceClickListener(preference -> {
            DialogFragment timeDialog = new TimePickerFragment();
            timeDialog.show(getActivity().getSupportFragmentManager(), "timePicker");
            return false;
        });

        removeDb.setOnPreferenceClickListener(preference -> {
            workId = sharedPreferences.getLong("work_id", -1);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("The database will be deleted");
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                viewModel.clearDatabase();
                if (workId > 0) {
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putLong("work_id", -1);
                    edit.putLong("pause_id", -1);
                    edit.remove("stringSet");
                    edit.apply();
                }
                dialog.dismiss();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });
            builder.show();
            return false;
        });
    }

    private void init() {
        workReminderSwitch = findPreference("work_reminder");
        workTimeBeginEditText = findPreference("work_begin_preference");
        saturdayReminderSwitch = findPreference("saturday_reminder_switch");
        sundayReminderSwitch = findPreference("sunday_reminder_switch");
        removeDb = findPreference("remove_db");
        darkModeSwitch = findPreference("dark_mode_switch");
    }

    private void showHideEditText() {
        if (isWorkReminderOn) {
            workTimeBeginEditText.setVisible(true);
            saturdayReminderSwitch.setVisible(true);
            sundayReminderSwitch.setVisible(true);
        } else {
            workTimeBeginEditText.setVisible(false);
            saturdayReminderSwitch.setVisible(false);
            sundayReminderSwitch.setVisible(false);
        }
    }
}
