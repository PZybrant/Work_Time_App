package com.example.patryk.work_time_app.fragments;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.broadcast_receivers.ReminderReceiver;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private static final String PREF_HOUR = "pref_hour";
    private static final String PREF_MINUTE = "pref_minute";
    private SharedPreferences defaultSharedPreferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        final Calendar c = Calendar.getInstance();
        int hour = defaultSharedPreferences.getInt(PREF_HOUR, c.get(Calendar.HOUR_OF_DAY));
        int minute = defaultSharedPreferences.getInt(PREF_MINUTE, c.get(Calendar.MINUTE));

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final int REQUEST_CODE = Integer.parseInt(getString(R.string.reminder_intent_request_code));

        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putInt(PREF_HOUR, hourOfDay);
        edit.putInt(PREF_MINUTE, minute);
        edit.apply();

        Calendar temp = Calendar.getInstance();
        temp.setTimeInMillis(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(temp)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent reminderIntent = new Intent(getContext(), ReminderReceiver.class);
        PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                getContext(),
                REQUEST_CODE,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = getContext().getSystemService(AlarmManager.class);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, reminderPendingIntent);

        String hourText = hourOfDay < 10 ? "0" + hourOfDay : String.valueOf(hourOfDay);
        String minuteText = minute < 10 ? "0" + minute : String.valueOf(minute);

        Toast.makeText(getContext(), "Alarm set to: " + hourText + ":" + minuteText, Toast.LENGTH_SHORT).show();
    }
}
