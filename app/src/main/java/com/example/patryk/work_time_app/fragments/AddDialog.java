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
import com.example.patryk.work_time_app.data.WorkTimeRecord;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddDialog extends DialogFragment {

    public interface AddDialogListener {
        void onApplyButtonClick(WorkTimeRecord newWorkTimeRecord);
    }

    private Button applyButton;
    private HistoryFragmentViewModel historyViewModel;
    private List<WorkTimeRecord> specifiedWorkTimeRecordRecordList;
    private Calendar newTime, rangeFrom, rangeTo;
    private WorkTimeRecord newWorkTimeRecord;
    private boolean canBeCreated;
    private AddDialogListener listener;

    public AddDialog(AddDialogListener listener, HistoryFragmentViewModel viewModel) {
        this.listener = listener;
        this.historyViewModel = viewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_add_edit, container, false);
        DatePicker datePicker = view.findViewById(R.id.dialog_edit_picker_date);
        NumberPicker hourPicker = view.findViewById(R.id.dialog_edit_picker_hour);
        NumberPicker minutePicker = view.findViewById(R.id.dialog_edit_picker_minute);
//        NumberPicker secondPicker = view.findViewById(R.id.dialog_edit_picker_second);
        Button cancelButton = view.findViewById(R.id.dialog_edit_button_cancel);
        applyButton = view.findViewById(R.id.dialog_edit_button_apply);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
//        secondPicker.setMinValue(0);
//        secondPicker.setMaxValue(59);


        rangeFrom = Calendar.getInstance();
        rangeFrom.set(Calendar.HOUR_OF_DAY, 0);
        rangeFrom.set(Calendar.MINUTE, 0);
        rangeFrom.set(Calendar.SECOND, 0);
        rangeFrom.set(Calendar.MILLISECOND, 0);
        rangeTo = Calendar.getInstance();
        rangeTo.set(Calendar.HOUR_OF_DAY, 23);
        rangeTo.set(Calendar.MINUTE, 59);
        rangeTo.set(Calendar.SECOND, 59);
        rangeTo.set(Calendar.MILLISECOND, 999);

        newTime = Calendar.getInstance(Locale.getDefault());

        updateTimeList();

        datePicker.init(newTime.get(Calendar.YEAR), newTime.get(Calendar.MONTH), newTime.get(Calendar.DAY_OF_MONTH), onDateChangedListener);
        hourPicker.setValue(newTime.get(Calendar.HOUR_OF_DAY));
        minutePicker.setValue(newTime.get(Calendar.MINUTE));
//        secondPicker.setValue(newTime.get(Calendar.SECOND));

        hourPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            newTime.set(Calendar.HOUR_OF_DAY, newVal);
            canBeCreated = historyViewModel.canBeCreated(newTime, specifiedWorkTimeRecordRecordList);
            enableApplyButton(canBeCreated);
        });

        minutePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            newTime.set(Calendar.MINUTE, newVal);
            canBeCreated = historyViewModel.canBeCreated(newTime, specifiedWorkTimeRecordRecordList);
            enableApplyButton(canBeCreated);
        });

//        secondPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
//            newTime.set(Calendar.SECOND, newVal);
//            canBeCreated = historyViewModel.canBeCreated(newTime, specifiedWorkTimeRecordList);
//            enableApplyButton(canBeCreated);
//        });

        cancelButton.setOnClickListener(v -> dismiss());

        applyButton.setOnClickListener(v -> {
            newTime.set(Calendar.SECOND, 1);
            newTime.set(Calendar.MILLISECOND, 1);
            Calendar newEndTime = Calendar.getInstance();
            newEndTime.setTimeInMillis(newTime.getTimeInMillis());
            newEndTime.add(Calendar.MINUTE, 1);
            newEndTime.set(Calendar.SECOND, 1);
            newEndTime.set(Calendar.MILLISECOND, 1);
            long difference = Support.calculateDifference(newTime.getTimeInMillis(), newEndTime.getTimeInMillis());
            newWorkTimeRecord = new WorkTimeRecord(newTime, newEndTime, difference, true);
            historyViewModel.insertWorkTime(newWorkTimeRecord);
            listener.onApplyButtonClick(newWorkTimeRecord);
            dismiss();
        });

        canBeCreated = historyViewModel.canBeCreated(newTime, specifiedWorkTimeRecordRecordList);
        enableApplyButton(canBeCreated);

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
        specifiedWorkTimeRecordRecordList = historyViewModel.getTimeRecordListWithSpecifiedDate(Support.convertDateToString(rangeFrom.getTime()), Support.convertDateToString(rangeTo.getTime()));
    }

    private void enableApplyButton(boolean enable) {
        if (enable) {
            applyButton.setEnabled(true);
            applyButton.setTextColor(getResources().getColor(R.color.lightThemeColorPrimary, null));
        } else {
            applyButton.setEnabled(false);
            applyButton.setTextColor(Color.GRAY);
        }
    }
}
