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
import com.example.patryk.work_time_app.data.PauseTimeRecord;
import com.example.patryk.work_time_app.data.WorkTimeRecord;
import com.example.patryk.work_time_app.viewmodels.EditFragmentViewModel;

import java.util.Calendar;

public class EditDialog extends DialogFragment {

    public interface OnApplyButtonClickListener {
        void onClick(PauseTimeRecord pauseTimeRecord, int type);
    }

    // 0 = work/pause begin, 1 = work/pause end
    private int mType;

    private WorkTimeRecord workTimeRecord;
    private PauseTimeRecord pauseTimeRecord;
    private OnApplyButtonClickListener mListener;

    private Button buttonApply;
    private Calendar actualDate, dateBefore, dateAfter;

    EditDialog(OnApplyButtonClickListener listener, EditFragmentViewModel viewModel, WorkTimeRecord workTimeRecord, int type) {
        this.mListener = listener;
        this.workTimeRecord = workTimeRecord;
        this.mType = type;
        actualDate = Calendar.getInstance();
        if (mType == 0) {
            actualDate.setTime(workTimeRecord.getShiftBegin().getTime());
            WorkTimeRecord workTimeRecordBefore = viewModel.getOneWorkTimeBefore(Support.convertDateToString(workTimeRecord.getShiftBegin().getTime()));
            if (workTimeRecordBefore != null) {
                dateBefore = workTimeRecordBefore.getShiftEnd();
            }
            dateAfter = workTimeRecord.getShiftEnd();
        } else if (mType == 1) {
            actualDate.setTime(workTimeRecord.getShiftEnd().getTime());
            dateBefore = workTimeRecord.getShiftBegin();
            WorkTimeRecord workTimeRecordAfter = viewModel.getOneWorkTimeAfter(Support.convertDateToString(workTimeRecord.getShiftEnd().getTime()));
            System.out.println(workTimeRecordAfter);
            if (workTimeRecordAfter != null) {
                dateAfter = workTimeRecordAfter.getShiftBegin();
            } else {
                dateAfter = null;
            }
        }
    }

    EditDialog(OnApplyButtonClickListener listener, EditFragmentViewModel viewModel, WorkTimeRecord workTimeRecord, PauseTimeRecord pauseTimeRecord, int type) {
        this.mListener = listener;
        this.workTimeRecord = workTimeRecord;
        this.pauseTimeRecord = pauseTimeRecord;
        this.mType = type;
        actualDate = Calendar.getInstance();
        if (mType == 0) {
            actualDate.setTime(pauseTimeRecord.getPauseBegin().getTime());
            PauseTimeRecord pauseTimeRecordBefore = viewModel.getOnePauseTimeBefore(Support.convertDateToString(pauseTimeRecord.getPauseBegin().getTime()));
            if (pauseTimeRecordBefore != null) {
                dateBefore = pauseTimeRecordBefore.getPauseEnd();
            } else {
                dateBefore = workTimeRecord.getShiftBegin();
            }
            dateAfter = pauseTimeRecord.getPauseEnd();
        } else if (mType == 1) {
            actualDate.setTime(pauseTimeRecord.getPauseEnd().getTime());
            dateBefore = pauseTimeRecord.getPauseBegin();
            PauseTimeRecord pauseTimeRecordAfter = viewModel.getOnePauseTimeAfter(Support.convertDateToString(pauseTimeRecord.getPauseEnd().getTime()));
            if (pauseTimeRecordAfter != null) {
                dateAfter = pauseTimeRecordAfter.getPauseBegin();
            } else {
                dateAfter = workTimeRecord.getShiftEnd();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_add_edit, container, false);
        DatePicker pickerDate = view.findViewById(R.id.dialog_edit_picker_date);
        NumberPicker pickerHour = view.findViewById(R.id.dialog_edit_picker_hour);
        NumberPicker pickerMinute = view.findViewById(R.id.dialog_edit_picker_minute);
//        NumberPicker pickerSecond = view.findViewById(R.id.dialog_edit_picker_second);
        Button buttonCancel = view.findViewById(R.id.dialog_edit_button_cancel);
        buttonApply = view.findViewById(R.id.dialog_edit_button_apply);
        buttonApply.setEnabled(false);
        buttonApply.setTextColor(Color.GRAY);

        pickerHour.setMinValue(0);
        pickerHour.setMaxValue(23);
        pickerMinute.setMinValue(0);
        pickerMinute.setMaxValue(59);
//        pickerSecond.setMinValue(0);
//        pickerSecond.setMaxValue(59);

        pickerDate.init(actualDate.get(Calendar.YEAR), actualDate.get(Calendar.MONTH), actualDate.get(Calendar.DAY_OF_MONTH), onDateChangedListener);
        pickerHour.setValue(actualDate.get(Calendar.HOUR_OF_DAY));
        pickerMinute.setValue(actualDate.get(Calendar.MINUTE));
//        pickerSecond.setValue(actualDate.get(Calendar.SECOND));

        pickerHour.setOnValueChangedListener((picker, oldVal, newVal) -> {
            actualDate.set(Calendar.HOUR_OF_DAY, newVal);
            compareDates(actualDate);
        });
        pickerMinute.setOnValueChangedListener((picker, oldVal, newVal) -> {
            actualDate.set(Calendar.MINUTE, newVal);
            compareDates(actualDate);
        });

//        pickerSecond.setOnValueChangedListener((picker, oldVal, newVal) -> {
//            actualDate.set(Calendar.SECOND, newVal);
//            compareDates(actualDate);
//        });

        buttonCancel.setOnClickListener(v -> {
            actualDate = null;
            this.dismiss();
        });

        buttonApply.setOnClickListener(v -> {
            if (pauseTimeRecord != null) {
                if (mType == 0) {
                    pauseTimeRecord.setPauseBegin(actualDate);
                } else if (mType == 1) {
                    pauseTimeRecord.setPauseEnd(actualDate);
                }
                mListener.onClick(pauseTimeRecord, mType);
            } else {
                if (mType == 0) {
                    workTimeRecord.setShiftBegin(actualDate);
                } else if (mType == 1) {
                    workTimeRecord.setShiftEnd(actualDate);
                }
                mListener.onClick(null, mType);
            }
            actualDate = null;
        });

        return view;
    }

    private DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            actualDate.set(Calendar.YEAR, year);
            actualDate.set(Calendar.MONTH, monthOfYear);
            actualDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            compareDates(actualDate);
        }
    };

    private void compareDates(Calendar cal) {
        if (dateBefore != null && dateAfter != null) {
            System.out.println(cal.compareTo(dateAfter) + " / " + cal.compareTo(dateBefore));
//             actualInMillis < afterInMillis && actualInMillis > beforeInMillis
            if (cal.compareTo(dateAfter) < 0 && cal.compareTo(dateBefore) > 0) {
                buttonApply.setEnabled(true);
                buttonApply.setTextColor(getResources().getColor(R.color.lightThemeColorPrimary, null));
            } else {
                buttonApply.setEnabled(false);
                buttonApply.setTextColor(Color.GRAY);
            }
        } else if (dateAfter == null) {
//            long beforeInMillis = dateBefore.getTimeInMillis();
//             actualInMillis > beforeInMillis
            if (cal.compareTo(dateBefore) > 0) {
                buttonApply.setEnabled(true);
                buttonApply.setTextColor(getResources().getColor(R.color.lightThemeColorPrimary, null));
            } else {
                buttonApply.setEnabled(false);
                buttonApply.setTextColor(Color.GRAY);
            }
        } else {
//            long afterInMillis = dateAfter.getTimeInMillis();
//             actualInMillis < afterInMillis
            if (cal.compareTo(dateAfter) < 0) {
                buttonApply.setEnabled(true);
                buttonApply.setTextColor(getResources().getColor(R.color.lightThemeColorPrimary, null));
            } else {
                buttonApply.setEnabled(false);
                buttonApply.setTextColor(Color.GRAY);
            }
        }
    }
}
