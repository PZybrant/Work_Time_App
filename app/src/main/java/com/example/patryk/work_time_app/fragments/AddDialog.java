package com.example.patryk.work_time_app.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddDialog extends DialogFragment {


    private Button applyButton;
    private HistoryFragmentViewModel historyViewModel;
    private List<WorkTime> workTimeList;
    private Calendar newTime, rangeFrom, rangeTo;

    public AddDialog(HistoryFragmentViewModel viewModel) {
        this.historyViewModel = viewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_add_edit, container, false);
        DatePicker datePicker = view.findViewById(R.id.dialog_edit_picker_date);
        NumberPicker hourPicker = view.findViewById(R.id.dialog_edit_picker_hour);
        NumberPicker minutePicker = view.findViewById(R.id.dialog_edit_picker_minute);
        NumberPicker secondPicker = view.findViewById(R.id.dialog_edit_picker_second);
        Button cancelButton = view.findViewById(R.id.dialog_edit_button_cancel);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        applyButton = view.findViewById(R.id.dialog_edit_button_apply);

        if (!canBeCreated()) {
            applyButton.setEnabled(false);
            applyButton.setTextColor(Color.GRAY);
        }

        rangeFrom = Calendar.getInstance();
        rangeTo = Calendar.getInstance();
        rangeFrom.set(Calendar.HOUR_OF_DAY, 0);
        rangeFrom.set(Calendar.MINUTE, 0);
        rangeFrom.set(Calendar.SECOND, 0);
        rangeFrom.set(Calendar.MILLISECOND, 0);
        rangeTo.setTimeInMillis(rangeFrom.getTimeInMillis());
        rangeTo.set(Calendar.HOUR_OF_DAY, 23);
        rangeTo.set(Calendar.MINUTE, 59);
        rangeTo.set(Calendar.SECOND, 59);
        rangeTo.set(Calendar.MILLISECOND, 999);

        updateTimeList();

        newTime = Calendar.getInstance(Locale.getDefault());

        datePicker.init(newTime.get(Calendar.YEAR), newTime.get(Calendar.MONTH), newTime.get(Calendar.DAY_OF_MONTH), onDateChangedListener);
        hourPicker.setValue(newTime.get(Calendar.HOUR_OF_DAY));
        minutePicker.setValue(newTime.get(Calendar.MINUTE));
        secondPicker.setValue(newTime.get(Calendar.SECOND));

        hourPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            newTime.set(Calendar.HOUR_OF_DAY, newVal);
            if (canBeCreated()) {
                applyButton.setEnabled(true);
                applyButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                applyButton.setEnabled(false);
                applyButton.setTextColor(Color.GRAY);
            }
        });

        minutePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            newTime.set(Calendar.MINUTE, newVal);
            if (canBeCreated()) {
                applyButton.setEnabled(true);
                applyButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                applyButton.setEnabled(false);
                applyButton.setTextColor(Color.GRAY);
            }
        });

        secondPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            newTime.set(Calendar.SECOND, newVal);
            if (canBeCreated()) {
                applyButton.setEnabled(true);
                applyButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                applyButton.setEnabled(false);
                applyButton.setTextColor(Color.GRAY);
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());

        applyButton.setOnClickListener(v -> {
            newTime.set(Calendar.MILLISECOND, 0);
//            Calendar newTimeEnd = Calendar.getInstance();
//            newTimeEnd.setTimeInMillis(newTime.getTimeInMillis());
//            newTimeEnd.add(Calendar.SECOND, 1);
            WorkTime newWorkTime = new WorkTime(newTime, true);
            newWorkTime.setShiftEnd(newTime);
            historyViewModel.insertWorkTime(newWorkTime);
            dismiss();
        });
        return view;
    }

    private DatePicker.OnDateChangedListener onDateChangedListener = (view, year, monthOfYear, dayOfMonth) -> {
        rangeFrom.set(Calendar.YEAR, year);
        rangeFrom.set(Calendar.MONTH, monthOfYear);
        rangeFrom.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        rangeTo.set(Calendar.YEAR, year);
        rangeTo.set(Calendar.MONTH, monthOfYear);
        rangeTo.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateTimeList();

        newTime.set(Calendar.YEAR, year);
        newTime.set(Calendar.MONTH, monthOfYear);
        newTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    };

    private void updateTimeList() {
        workTimeList = historyViewModel.getWorkWithSpecifiedDate(Support.convertToString(rangeFrom.getTime()), Support.convertToString(rangeTo.getTime()));
    }

    private boolean canBeCreated() {
        if (workTimeList == null || workTimeList.size() == 0) {
            return true;
        } else {
            for (int i = 0; i < workTimeList.size(); i++) {
                WorkTime workTimeToCompare = workTimeList.get(i);
                WorkTime workTimeToCompare2 = i == workTimeList.size() - 1 ? null : workTimeList.get(i + 1);
                if (i == 0 && newTime.before(workTimeList.get(i).getShiftBegin())) {
                    return true;
                }
                if (workTimeToCompare2 != null) {
                    if (newTime.after(workTimeList.get(i).getShiftEnd()) && newTime.before(workTimeList.get(i + 1).getShiftBegin())) {
                        return true;
                    }
                } else {
                    if (newTime.after(workTimeList.get(i).getShiftEnd())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
