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
import com.example.patryk.work_time_app.viewmodels.EditFragmentViewModel;

import java.util.Calendar;

public class EditDialog extends DialogFragment {

    public interface OnApplyButtonClickListener {
        void onClick(PauseTime pauseTime, int type);
    }

    // 0 = work/pause begin, 1 = work/pause end
    private int mType;

    private WorkTime workTime;
    private PauseTime pauseTime;
    private OnApplyButtonClickListener mListener;

    private Button buttonApply;
    private Calendar actualDate, dateBefore, dateAfter;

    EditDialog(OnApplyButtonClickListener listener, EditFragmentViewModel viewModel, WorkTime workTime, int type) {
        this.mListener = listener;
        this.workTime = workTime;
        this.mType = type;
        actualDate = Calendar.getInstance();
        if (mType == 0) {
            actualDate.setTime(workTime.getShiftBegin().getTime());
            WorkTime workTimeBefore = viewModel.getOneWorkTimeBefore(Support.convertDateToString(workTime.getShiftBegin().getTime()));
            if (workTimeBefore != null) {
                dateBefore = workTimeBefore.getShiftEnd();
            }
            dateAfter = workTime.getShiftEnd();
        } else if (mType == 1) {
            actualDate.setTime(workTime.getShiftEnd().getTime());
            dateBefore = workTime.getShiftBegin();
            WorkTime workTimeAfter = viewModel.getOneWorkTimeAfter(Support.convertDateToString(workTime.getShiftEnd().getTime()));
            if (workTimeAfter != null) {
                dateAfter = workTimeAfter.getShiftBegin();
            }
        }
    }

    EditDialog(OnApplyButtonClickListener listener, EditFragmentViewModel viewModel, WorkTime workTime, PauseTime pauseTime, int type) {
        this.mListener = listener;
        this.workTime = workTime;
        this.pauseTime = pauseTime;
        this.mType = type;
        actualDate = Calendar.getInstance();
        if (mType == 0) {
            actualDate.setTime(pauseTime.getPauseBegin().getTime());
            PauseTime pauseTimeBefore = viewModel.getOnePauseTimeBefore(Support.convertDateToString(pauseTime.getPauseBegin().getTime()));
            if (pauseTimeBefore != null) {
                dateBefore = pauseTimeBefore.getPauseEnd();
            } else {
                dateBefore = workTime.getShiftBegin();
            }
            dateAfter = pauseTime.getPauseEnd();
        } else if (mType == 1) {
            actualDate.setTime(pauseTime.getPauseEnd().getTime());
            dateBefore = pauseTime.getPauseBegin();
            PauseTime pauseTimeAfter = viewModel.getOnePauseTimeAfter(Support.convertDateToString(pauseTime.getPauseEnd().getTime()));
            if (pauseTimeAfter != null) {
                dateAfter = pauseTimeAfter.getPauseBegin();
            } else {
                dateAfter = workTime.getShiftEnd();
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
        NumberPicker pickerSecond = view.findViewById(R.id.dialog_edit_picker_second);
        Button buttonCancel = view.findViewById(R.id.dialog_edit_button_cancel);
        buttonApply = view.findViewById(R.id.dialog_edit_button_apply);
        buttonApply.setEnabled(false);
        buttonApply.setTextColor(Color.GRAY);

        pickerHour.setMinValue(0);
        pickerHour.setMaxValue(23);
        pickerMinute.setMinValue(0);
        pickerMinute.setMaxValue(59);
        pickerSecond.setMinValue(0);
        pickerSecond.setMaxValue(59);

        pickerDate.init(actualDate.get(Calendar.YEAR), actualDate.get(Calendar.MONTH), actualDate.get(Calendar.DAY_OF_MONTH), onDateChangedListener);
        pickerHour.setValue(actualDate.get(Calendar.HOUR_OF_DAY));
        pickerMinute.setValue(actualDate.get(Calendar.MINUTE));
        pickerSecond.setValue(actualDate.get(Calendar.SECOND));

        pickerHour.setOnValueChangedListener((picker, oldVal, newVal) -> {
            actualDate.set(Calendar.HOUR_OF_DAY, newVal);
            compareDates(actualDate);
        });
        pickerMinute.setOnValueChangedListener((picker, oldVal, newVal) -> {
            actualDate.set(Calendar.MINUTE, newVal);
            compareDates(actualDate);
        });

        pickerSecond.setOnValueChangedListener((picker, oldVal, newVal) -> {
            actualDate.set(Calendar.SECOND, newVal);
            compareDates(actualDate);
        });

        buttonCancel.setOnClickListener(v -> {
            actualDate = null;
            this.dismiss();
        });

        buttonApply.setOnClickListener(v -> {
            if (pauseTime != null) {
                if (mType == 0) {
                    pauseTime.setPauseBegin(actualDate);
                } else if (mType == 1) {
                    pauseTime.setPauseEnd(actualDate);
                }
                mListener.onClick(pauseTime, mType);
            } else {
                if (mType == 0) {
                    workTime.setShiftBegin(actualDate);
                } else if (mType == 1) {
                    workTime.setShiftEnd(actualDate);
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
            if (cal.compareTo(dateBefore) > 0 && cal.compareTo(dateAfter) < 0) {
                buttonApply.setEnabled(true);
                buttonApply.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                buttonApply.setEnabled(false);
                buttonApply.setTextColor(Color.GRAY);
            }
        } else if (dateBefore != null) {
            if (cal.compareTo(dateBefore) > 0) {
                buttonApply.setEnabled(true);
                buttonApply.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                buttonApply.setEnabled(false);
                buttonApply.setTextColor(Color.GRAY);
            }
        } else if (dateAfter != null) {
            if (cal.compareTo(dateAfter) < 0) {
                buttonApply.setEnabled(true);
                buttonApply.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                buttonApply.setEnabled(false);
                buttonApply.setTextColor(Color.GRAY);
            }
        }
    }
}
