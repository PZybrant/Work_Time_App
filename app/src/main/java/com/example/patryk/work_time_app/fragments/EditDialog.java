package com.example.patryk.work_time_app.fragments;

import android.content.DialogInterface;
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
import java.util.List;

public class EditDialog extends DialogFragment {

    public interface OnApplyButtonClickListener {
        void onClick(PauseTime pauseTime, int type);
    }

    // 0 = work/pause begin, 1 = work/pause end
    private int mType;
    private boolean isOk = false;

    private WorkTime mWorkTime;
    private PauseTime mPauseTime;
    private EditFragmentViewModel mViewModel;
    private OnApplyButtonClickListener mListener;

    private Button applyButton;
    private Calendar calendar, calendarBefore, calendarAfter;

    EditDialog(OnApplyButtonClickListener listener, EditFragmentViewModel viewModel, WorkTime workTime, int type) {
        this.mListener = listener;
        this.mViewModel = viewModel;
        this.mWorkTime = workTime;
        this.mType = type;
        if (mType == 0) {
            calendar = mWorkTime.getShiftBegin();
            WorkTime workTimeBefore = viewModel.getOneWorkTimeBefore(Support.convertToString(workTime.getShiftBegin().getTime()));
            if (workTimeBefore != null) {
                calendarBefore = workTimeBefore.getShiftEnd();
            }
            calendarAfter = workTime.getShiftEnd();
        } else {
            calendar = mWorkTime.getShiftEnd();
            calendarBefore = mWorkTime.getShiftBegin();
            WorkTime workTimeAfter = viewModel.getOneWorkTimeAfter(Support.convertToString(workTime.getShiftEnd().getTime()));
            if (workTimeAfter != null) {
                calendarAfter = workTimeAfter.getShiftBegin();
            }
        }
    }

    EditDialog(OnApplyButtonClickListener listener, EditFragmentViewModel viewModel, WorkTime workTime, PauseTime pauseTime, int type) {
        this.mListener = listener;
        this.mViewModel = viewModel;
        this.mWorkTime = workTime;
        this.mPauseTime = pauseTime;
        this.mType = type;

        if (mType == 0) {
            calendar = mPauseTime.getPauseBegin();
            PauseTime pauseTimeBefore = viewModel.getOnePauseTimeBefore(Support.convertToString(pauseTime.getPauseBegin().getTime()));
            if (pauseTimeBefore != null) {
                calendarBefore = pauseTimeBefore.getPauseEnd();
            } else {
                calendarBefore = workTime.getShiftBegin();
            }
            calendarAfter = pauseTime.getPauseEnd();
        } else {
            calendar = mPauseTime.getPauseEnd();
            calendarBefore = mPauseTime.getPauseBegin();
            PauseTime pauseTimeAfter = viewModel.getOnePauseTimeAfter(Support.convertToString(pauseTime.getPauseEnd().getTime()));
            if (pauseTimeAfter != null) {
                calendarAfter = pauseTimeAfter.getPauseBegin();
            } else {
                calendarAfter = workTime.getShiftEnd();
            }
        }
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
        applyButton = view.findViewById(R.id.dialog_edit_button_apply);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);
        if (!isOk) {
            applyButton.setEnabled(false);
            applyButton.setTextColor(Color.GRAY);
        }

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), onDateChangedListener);
        hourPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minutePicker.setValue(calendar.get(Calendar.MINUTE));
        secondPicker.setValue(calendar.get(Calendar.SECOND));

        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calendar.set(Calendar.HOUR_OF_DAY, newVal);
                compareDates(calendar);
            }
        });
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calendar.set(Calendar.MINUTE, newVal);
                compareDates(calendar);
            }
        });

        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calendar.set(Calendar.SECOND, newVal);
                compareDates(calendar);
            }
        });

        cancelButton.setOnClickListener(view1 -> {
            this.dismissAllowingStateLoss();
        });

        applyButton.setOnClickListener(view2 -> {
            if (isOk) {
                if (mPauseTime != null) {
                    if (mType == 0) {
                        mPauseTime.setPauseBegin(calendar);
                    } else if (mType == 1) {
                        mPauseTime.setPauseEnd(calendar);
                    }
                    mListener.onClick(mPauseTime, mType);
                } else {
                    if (mType == 0) {
                        mWorkTime.setShiftBegin(calendar);
                    } else if (mType == 1) {
                        mWorkTime.setShiftEnd(calendar);
                    }
                    mListener.onClick(null, mType);
                }
            }
        });

        return view;
    }

    private DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            compareDates(calendar);
        }
    };

    private void compareDates(Calendar cal) {
        if (calendarBefore != null && calendarAfter != null) {
            if (cal.compareTo(calendarBefore) > 0 && cal.compareTo(calendarAfter) < 0) {
                isOk = true;
                applyButton.setEnabled(true);
                applyButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                isOk = false;
                applyButton.setEnabled(false);
                applyButton.setTextColor(Color.GRAY);
                // set text in TextView
            }
        } else if (calendarBefore != null) {
            if (cal.compareTo(calendarBefore) > 0) {
                isOk = true;
                applyButton.setEnabled(true);
                applyButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                isOk = false;
                applyButton.setEnabled(false);
                applyButton.setTextColor(Color.GRAY);
                // set text in TextView
            }
        } else if (calendarAfter != null) {
            if (cal.compareTo(calendarAfter) < 0) {
                isOk = true;
                applyButton.setEnabled(true);
                applyButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                isOk = false;
                applyButton.setEnabled(false);
                applyButton.setTextColor(Color.GRAY);
                // set text in TextView
            }
        }
    }

    private long calculateDifference(long time1, long time2) {
        return Math.abs(time1 - time2);
    }

    private long recalculateWorkTime() {
        long totalWorkTime;
        long totalPauseTime = 0;

        Calendar x = mWorkTime.getShiftBegin();
        Calendar y = mWorkTime.getShiftEnd();

        totalWorkTime = calculateDifference(x.getTimeInMillis(), y.getTimeInMillis());

        List<PauseTime> pauseTimesWithWorkId = mViewModel.getPauseTimesWithWorkId(mWorkTime.getId());
        for (PauseTime p : pauseTimesWithWorkId) {
            totalPauseTime += p.getPauseTime();
        }

        return totalWorkTime - totalPauseTime;
    }

}
