package com.example.patryk.work_time_app.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.patryk.work_time_app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FilterDialog extends DialogFragment {

    public interface FilterDialogFragmentListener {
        void onApplyButtonClick(Calendar date1, Calendar date2);
    }

    private FilterDialogFragmentListener listener;

    private TextView fromTextView, toTextView;
    private Button applyButton;
    private Calendar rangeFrom, rangeTo;
    private boolean selectFirstDate = true;

    public FilterDialog(FilterDialogFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_filter, container, false);
        fromTextView = view.findViewById(R.id.dialog_filter_tv_date_from);
        toTextView = view.findViewById(R.id.dialog_filter_tv_date_to);
        CalendarView calendarView = view.findViewById(R.id.dialog_filter_calendar_view);
        applyButton = view.findViewById(R.id.dialog_filter_button_apply);
        applyButton.setEnabled(false);
        applyButton.setTextColor(Color.BLACK);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            if (selectFirstDate) {

                // if rangeTo is not null, it means the date was already selected, but user want to change it.
                // therefor values needs to be reset
                if (rangeTo != null) {
                    rangeTo = null;
                    toTextView.setText("");
                    applyButton.setEnabled(false);
                    applyButton.setTextColor(Color.LTGRAY);
                }

                rangeFrom = Calendar.getInstance(Locale.getDefault());
                rangeFrom.set(year, month, dayOfMonth);
                selectFirstDate = false;
                fromTextView.setText(dateFormat.format((rangeFrom.getTimeInMillis())));
            } else {
                rangeTo = Calendar.getInstance(Locale.getDefault());
                rangeTo.set(year, month, dayOfMonth);
                selectFirstDate = true;
                if (rangeFrom.after(rangeTo)) {
                    Calendar temp = rangeTo;
                    rangeTo = rangeFrom;
                    rangeFrom = temp;
                    fromTextView.setText(dateFormat.format(rangeFrom.getTimeInMillis()));
                }
                toTextView.setText(dateFormat.format(rangeTo.getTimeInMillis()));
                applyButton.setEnabled(true);
                applyButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
            }
        });

        applyButton.setOnClickListener(v -> {
            rangeFrom.set(Calendar.HOUR_OF_DAY, 0);
            rangeFrom.set(Calendar.MINUTE, 0);
            rangeFrom.set(Calendar.SECOND, 0);
            rangeFrom.set(Calendar.MILLISECOND, 0);

            rangeTo.set(Calendar.HOUR_OF_DAY, 23);
            rangeTo.set(Calendar.MINUTE, 59);
            rangeTo.set(Calendar.SECOND, 59);
            rangeTo.set(Calendar.MILLISECOND, 999);
            listener.onApplyButtonClick(rangeFrom, rangeTo);
            dismiss();
        });
        return view;
    }
}
