package com.example.patryk.work_time_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class FilterDialogFragment extends DialogFragment {

    public interface FilterDialogFragmentListener {
        void onApplyButtonClick(DialogFragment dialogFragment, long date1, long date2);
    }

    private FilterDialogFragmentListener listener;
    private SharedPreferences sharedPreferences;

    private CalendarView calendarView, tempCalendarView;
    private TextView fromTextView, toTextView;
    private Button applyButton, resetButton;
    private long date1 = 0, date2 = 0;
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

        View view = inflater.inflate(R.layout.filter_dialog_fragment, container, false);
        fromTextView = view.findViewById(R.id.date_from_text_view);
        toTextView = view.findViewById(R.id.date_to_text_view);
        calendarView = view.findViewById(R.id.calendar_view);
        applyButton = view.findViewById(R.id.apply_button);
        resetButton = view.findViewById(R.id.reset_button);

        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        calendarView.setMinDate(preferences.getLong(getString(R.string.init_date), -1));
        calendarView.setMaxDate(new GregorianCalendar().getTimeInMillis());

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                GregorianCalendar gregorianCalendar = new GregorianCalendar(Locale.getDefault());
                gregorianCalendar.set(year, month, dayOfMonth);
                view.setDate(gregorianCalendar.getTimeInMillis());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                if (selectFirstDate) {
                    date1 = view.getDate();
                    selectFirstDate = false;
                    if (date2 != 0 && date1 > date2) {
                        long tempDate = date2;
                        date2 = date1;
                        date1 = tempDate;
                        fromTextView.setText(dateFormat.format(new Date(date1)));
                        toTextView.setText(dateFormat.format(new Date(date2)));
                    }
                    fromTextView.setText(dateFormat.format(new Date(date1)));
                } else {
                    date2 = view.getDate();
                    selectFirstDate = true;
                    toTextView.setText(dateFormat.format(new Date(date2)));
                    if (date1 > date2) {
                        long tempDate = date2;
                        date2 = date1;
                        date1 = tempDate;
                        fromTextView.setText(dateFormat.format(new Date(date1)));
                        toTextView.setText(dateFormat.format(new Date(date2)));
                    }
                }
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date1 == 0) {
                    date1 = preferences.getLong(getString(R.string.init_date), -1);
                }

                if (date1 != 0 && date2 == 0) {
                    date2 = new GregorianCalendar().getTimeInMillis();
                }
                listener.onApplyButtonClick(FilterDialogFragment.this, date1, date2);
                dismiss();
            }
        });
        return view;
    }
}
