package com.example.patryk.work_time_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.patryk.work_time_app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class FilterDialog extends DialogFragment {

    public interface FilterDialogFragmentListener {
        void onApplyButtonClick(DialogFragment dialogFragment, long date1, long date2);
    }

    private FilterDialogFragmentListener listener;
    private SharedPreferences sharedPreferences;

    private CalendarView calendarView, tempCalendarView;
    private TextView fromTextView, toTextView;
    private Button applyButton, resetButton;
    private Calendar date1, date2;
    private boolean selectFirstDate = true;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (FilterDialogFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement FilterDialogFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_filter, container, false);
        fromTextView = view.findViewById(R.id.dialog_filter_tv_date_from);
        toTextView = view.findViewById(R.id.dialog_filter_tv_date_to);
        calendarView = view.findViewById(R.id.dialog_filter_calendar_view);
        applyButton = view.findViewById(R.id.dialog_filter_button_apply);
        applyButton.setEnabled(false);
        applyButton.setTextColor(Color.BLACK);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                if (selectFirstDate) {
                    if (date2 != null) {
                        date2 = null;
                        toTextView.setText("");
                        applyButton.setEnabled(false);
                        applyButton.setTextColor(Color.LTGRAY);
                    }
                    date1 = Calendar.getInstance(Locale.getDefault());
                    date1.set(year, month, dayOfMonth);
                    selectFirstDate = false;
                    if (date2 != null && date1.compareTo(date2) > 0) {
                        Calendar temp = date2;
                        date2 = date1;
                        date1 = temp;
                        fromTextView.setText(dateFormat.format((date2.getTimeInMillis())));
                    }
                    fromTextView.setText(dateFormat.format((date1.getTimeInMillis())));
                } else {
                    date2 = Calendar.getInstance(Locale.getDefault());
                    date2.set(year, month, dayOfMonth);
                    selectFirstDate = true;
                    if (date1.compareTo(date2) > 0) {
                        Calendar temp = date2;
                        date2 = date1;
                        date1 = temp;
                        fromTextView.setText(dateFormat.format(date1.getTimeInMillis()));
                    }
                    toTextView.setText(dateFormat.format(date2.getTimeInMillis()));
                    applyButton.setEnabled(true);
                    applyButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
                }
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date1.set(Calendar.HOUR_OF_DAY, 0);
                date1.set(Calendar.MINUTE, 0);
                date1.set(Calendar.SECOND, 0);
                date1.set(Calendar.MILLISECOND, 0);

                date2.set(Calendar.HOUR_OF_DAY, 23);
                date2.set(Calendar.MINUTE, 59);
                date2.set(Calendar.SECOND, 59);
                date2.set(Calendar.MILLISECOND, 999);
                listener.onApplyButtonClick(FilterDialog.this, date1.getTimeInMillis(), date2.getTimeInMillis());
                dismiss();
            }
        });
        return view;
    }
}
