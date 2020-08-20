package com.example.patryk.work_time_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.adapters.TimerAdapter;
import com.example.patryk.work_time_app.data.PauseTimeRecord;
import com.example.patryk.work_time_app.data.WorkTimeRecord;
import com.example.patryk.work_time_app.viewmodels.TimerFragmentViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static com.example.patryk.work_time_app.R.string.*;

public class TimerFragment extends Fragment {

    private int hoursValue;
    private int minutesValue;
    private int secondsValue;
    private long mWorkId;
    private long mPauseId;
    private long actualPauseTime;
    private boolean isWorkFinished = false;
    private boolean isPaused = false;
    private WorkTimeRecord workTimeRecord;
    private PauseTimeRecord pauseTimeRecord;

    private SharedPreferences sharedPreferences;
    private ProgressBar hoursProgressBar, minutesProgressBar, secondsProgressBar;
    private TextView hoursTextView, minutesTextView, secondsTextView;
    private ImageView registryTypeImageView;
    private Button startButton, pauseResumeButton, stopButton;
    private TimerAdapter recyclerviewAdapter;
    private TimerFragmentViewModel fragmentViewModel;
    private Drawable enterWorkIcon, endWorkIcon, startPauseIcon, stopPauseIcon;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        fragmentViewModel = new ViewModelProvider(this).get(TimerFragmentViewModel.class);
        sharedPreferences = getActivity().getSharedPreferences(getString(shared_preference_name), Context.MODE_PRIVATE);
        fragmentViewModel.getHoursValue().observe(this, hours -> {
            hoursValue = hours;
            setClockHours();
        });
        fragmentViewModel.getMinutesValue().observe(this, minutes -> {
            minutesValue = minutes;
            setClockMinutes();
        });
        fragmentViewModel.getSecondsValue().observe(this, seconds -> {
            secondsValue = seconds;
            setClockSeconds();
        });
        enterWorkIcon = requireContext().getDrawable(R.drawable.ic_exit_to_app_24dp);
        enterWorkIcon.setTint(getResources().getColor(R.color.green, null));

        endWorkIcon = requireContext().getDrawable(R.drawable.ic_exit_to_app_24dp);
        endWorkIcon.setTint(getResources().getColor(R.color.red, null));

        startPauseIcon = requireContext().getDrawable(R.drawable.ic_pause_24dp);
        startPauseIcon.setTint(getResources().getColor(R.color.red, null));

        stopPauseIcon = requireContext().getDrawable(R.drawable.ic_play_arrow_24dp);
        stopPauseIcon.setTint(getResources().getColor(R.color.yellow, null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        init(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mWorkId = sharedPreferences.getLong("work_id", -1);
        mPauseId = sharedPreferences.getLong("pause_id", -1);
        if (mWorkId > 0) {
            workTimeRecord = fragmentViewModel.getOneWorkTime(mWorkId);
            isWorkFinished = false;
            isPaused = false;
            actualPauseTime = sharedPreferences.getLong("totalPauseTimes", 0);
            if (mPauseId > 0) {
                pauseTimeRecord = fragmentViewModel.getOnePauseTime(mPauseId);
                setClockValue();
                setProgressBars(hoursValue, minutesValue, secondsValue);
                pauseResumeButton.setText(R.string.timer_resume);
                isPaused = true;
            }
            resumeTimer();
        }
    }

    private void resumeTimer() {
        if (!isPaused) {
            GregorianCalendar now = new GregorianCalendar(TimeZone.getDefault());
            long fragmentPauseTime = sharedPreferences.getLong("fragmentPauseTime", -1);
            fragmentViewModel.setClock(Support.calculateDifference(fragmentPauseTime, now.getTimeInMillis()));
            setClockValue();
            setProgressBars(hoursValue, minutesValue, secondsValue);
            fragmentViewModel.startClock(false);
        }
        startButton.setVisibility(View.INVISIBLE);
        pauseResumeButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onStop() {
        super.onStop();

        fragmentViewModel.stopClock();
        if (isWorkFinished) {
            hoursValue = 0;
            minutesValue = 0;
            secondsValue = 0;
            fragmentViewModel.clearRecordList();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        GregorianCalendar fragmentPauseTime = new GregorianCalendar(TimeZone.getDefault());
        editor.putLong("fragmentPauseTime", fragmentPauseTime.getTimeInMillis());
        editor.putLong("work_id", mWorkId);
        editor.putLong("pause_id", mPauseId);
        editor.putLong("totalPauseTime", actualPauseTime);
        editor.putInt("hours", hoursValue);
        editor.putInt("minutes", minutesValue);
        editor.putInt("seconds", secondsValue);
        fragmentViewModel.saveList(editor);
        editor.apply();
    }

    private void init(View view) {
        String[] strings = getResources().getStringArray(R.array.start_work_button);
        hoursProgressBar = view.findViewById(R.id.fragment_timer_pb_hour);
        minutesProgressBar = view.findViewById(R.id.fragment_timer_pb_minute);
        secondsProgressBar = view.findViewById(R.id.fragment_timer_pb_second);

        hoursTextView = view.findViewById(R.id.fragment_timer_tv_hour);
        minutesTextView = view.findViewById(R.id.fragment_timer_tv_minute);
        secondsTextView = view.findViewById(R.id.fragment_timer_tv_second);
        setClockValue();

        startButton = view.findViewById(R.id.fragment_timer_button_start);
        startButton.setOnClickListener(v -> startButtonClick());

        pauseResumeButton = view.findViewById(R.id.fragment_timer_button_pause_resume);
        pauseResumeButton.setOnClickListener(v -> pauseResumeButtonClick());

        stopButton = view.findViewById(R.id.fragment_timer_button_stop);
        stopButton.setOnClickListener(v -> stopButtonClick());

        RecyclerView overviewRecyclerView = view.findViewById(R.id.fragment_timer_rv);
        overviewRecyclerView.setHasFixedSize(true);
        overviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        recyclerviewAdapter = new TimerAdapter();
        fragmentViewModel.getRecordList().observe(getViewLifecycleOwner(), list -> recyclerviewAdapter.setRecordList(list));
        overviewRecyclerView.setAdapter(recyclerviewAdapter);
    }

    private void startButtonClick() {
        fragmentViewModel.clearRecordList();
        workTimeRecord = new WorkTimeRecord();
        mWorkId = fragmentViewModel.insertWorkTime(workTimeRecord);
        fragmentViewModel.addToRecordList(workTimeRecord.getShiftBeginText());
        isWorkFinished = false;
        fragmentViewModel.startClock(true);
        Snackbar.make(getActivity().findViewById(R.id.fragmentContainer), "Success insert.", Snackbar.LENGTH_LONG).show();
        startButton.setVisibility(View.INVISIBLE);
        pauseResumeButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
    }

    private void pauseResumeButtonClick() {
        if (!isPaused) {
            pauseTimeRecord = new PauseTimeRecord(mWorkId);
            mPauseId = fragmentViewModel.insertPauseTime(pauseTimeRecord);
            fragmentViewModel.addToRecordList(pauseTimeRecord.getPauseBeginText());
            isPaused = true;
            fragmentViewModel.stopClock();
            pauseResumeButton.setText(R.string.timer_resume);
        } else {
            finishPause();
            fragmentViewModel.startClock(false);
            pauseResumeButton.setText(R.string.timer_pause);
        }
    }

    private void stopButtonClick() {
        workTimeRecord.makeShiftEndTimestamp();
        long timeDifference = workTimeRecord.getWorkTime();
        isWorkFinished = true;

        if (isPaused) {
            finishPause();
        }
        fragmentViewModel.addToRecordList(workTimeRecord.getShiftEndText());
        timeDifference -= actualPauseTime;
        workTimeRecord.setWorkTime(timeDifference);
        workTimeRecord.setFinished(true);
        fragmentViewModel.updateWorkTime(workTimeRecord);

        mWorkId = 0;

        fragmentViewModel.stopClock();

        setProgressBars(0, 0, 0);
        setClockValue();
        Snackbar.make(getActivity().findViewById(R.id.fragmentContainer), "Success update.", Snackbar.LENGTH_LONG).show();

        pauseResumeButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
    }

    private void finishPause() {
        pauseTimeRecord.makePauseEndTimestamp();
        actualPauseTime += pauseTimeRecord.getPauseTime();
        pauseTimeRecord.setFinished(true);
        fragmentViewModel.updatePauseTime(pauseTimeRecord);
        fragmentViewModel.addToRecordList(pauseTimeRecord.getPauseEndText());
        mPauseId = 0;
        isPaused = false;
    }

    private void setClockValue() {
        setClockHours();
        setClockMinutes();
        setClockSeconds();
    }

    private void setClockHours() {
        if (hoursValue < 10) {
            if (hoursValue == 1) {
                hoursTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_hour), hoursValue));
            } else {
                hoursTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_hours), hoursValue));
            }
        } else {
            hoursTextView.setText(String.format(Locale.getDefault(), "%d " + getString(timer_hours), hoursValue));
        }
        hoursProgressBar.setProgress(hoursValue);
    }

    private void setClockMinutes() {
        if (minutesValue < 10) {
            if (minutesValue == 1) {
                minutesTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_minute), minutesValue));
            } else {
                minutesTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_minutes), minutesValue));
            }
        } else {
            minutesTextView.setText(String.format(Locale.getDefault(), "%d " + getString(timer_minutes), minutesValue));
        }
        minutesProgressBar.setProgress(minutesValue);
    }

    private void setClockSeconds() {
        if (secondsValue < 10) {
            if (secondsValue == 1) {
                secondsTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_second), secondsValue));
            } else {
                secondsTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_seconds), secondsValue));
            }
        } else {
            secondsTextView.setText(String.format(Locale.getDefault(), "%d " + getString(timer_seconds), secondsValue));
        }
        secondsProgressBar.setProgress(secondsValue);
    }

    private void setProgressBars(int h, int m, int s) {
        hoursProgressBar.setProgress(h);
        minutesProgressBar.setProgress(m);
        secondsProgressBar.setProgress(s);
    }
}