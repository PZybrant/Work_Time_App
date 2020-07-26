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

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat workReminderSwitch, saturdayReminderSwitch, sundayReminderSwitch, darkModeSwitch;
    private Preference workTimeBeginEditText, removeDb, feedback;

    private SharedPreferences settingPreferences, sharedPreferences;
    private SettingsFragmentViewModel viewModel;
    private boolean isWorkReminderOn;
    int REQUEST_CODE;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        init();
        settingPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(SettingsFragmentViewModel.class);
        isWorkReminderOn = settingPreferences.getBoolean(workReminderSwitch.getKey(), false);
        showHideEditText();

        workReminderSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            isWorkReminderOn = (Boolean) newValue;
            showHideEditText();

            if (isWorkReminderOn) {
                DialogFragment timeDialog = new TimePickerFragment();
                timeDialog.show(getActivity().getSupportFragmentManager(), "timePicker");
            } else {
                Intent reminderIntent = new Intent(getContext(), ReminderReceiver.class);
                PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                        getContext(),
                        REQUEST_CODE,
                        reminderIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.cancel(reminderPendingIntent);
                }
            }
            return true;
        });

        workTimeBeginEditText.setOnPreferenceClickListener(preference -> {
            DialogFragment timeDialog = new TimePickerFragment();
            timeDialog.show(getActivity().getSupportFragmentManager(), "timePicker");
            return false;
        });

        removeDb.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("The database will be deleted");
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                viewModel.clearDatabase();
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.remove("work_id");
                edit.remove("pause_id");
                edit.remove("stringSet");
                edit.apply();
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
