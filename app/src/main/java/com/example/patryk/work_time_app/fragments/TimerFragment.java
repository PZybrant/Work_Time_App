package com.example.patryk.work_time_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Process;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.CountUpTimer;
import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.adapters.TimerAdapter;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.viewmodels.TimerFragmentViewModel;
import com.example.patryk.work_time_app.data.WorkTime;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
    private WorkTime workTime;
    private PauseTime pauseTime;
    private ArrayList<String> stringsList;
    private LiveData<WorkTime> workTimeLiveData;
    private LiveData<List<PauseTime>> pauseTimeLiveDataList;

    private Thread progressBarThread;
    private CountUpTimer countUpTimer;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Calendar shiftBeginTime, shiftEndTime, pauseBegin, pauseEnd;
    private ProgressBar hoursProgressBar, minutesProgressBar, secondsProgressBar;
    private TextView hoursTextView, minutesTextView, secondsTextView;
    private Button startButton, pauseResumeButton, stopButton;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView overviewRecyclerView;
    private TimerAdapter recyclerviewAdapter;
    private View view;
    private TimerFragmentViewModel fragmentViewModel;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        fragmentViewModel = new ViewModelProvider(this).get(TimerFragmentViewModel.class);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        stringsList = loadList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_timer, container, false);
        init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mWorkId = sharedPreferences.getLong("work_id", -1);
        mPauseId = sharedPreferences.getLong("pause_id", -1);
        if (mWorkId > 0) {
            workTime = fragmentViewModel.getOneWorkTime(mWorkId);
            isWorkFinished = false;
            isPaused = false;
            hoursValue = sharedPreferences.getInt("hours", -1);
            minutesValue = sharedPreferences.getInt("minutes", -1);
            secondsValue = sharedPreferences.getInt("seconds", -1);
            actualPauseTime = sharedPreferences.getLong("totalPauseTimes", 0);
            if (mPauseId > 0) {
                pauseTime = fragmentViewModel.getOnePauseTime(mPauseId);
                pauseBegin = pauseTime.getPauseBegin();
                isPaused = true;
            }
            resumeTimer();
        }
    }

    private void resumeTimer() {
        if (!isWorkFinished && isPaused) {
            setTextViews();
            setProgressBars(hoursValue, minutesValue, secondsValue);
            pauseResumeButton.setText(R.string.timer_resume);
        } else if (!isWorkFinished && !isPaused) {
            GregorianCalendar now = new GregorianCalendar(TimeZone.getDefault());
            long fragmentPauseTime = sharedPreferences.getLong("fragmentPauseTime", -1);
            setTimer(calculateDifference(fragmentPauseTime, now.getTimeInMillis()));
            runProgressBar(hoursValue, minutesValue, secondsValue);
        }
        shiftBeginTime = workTime.getShiftBegin();
        startButton.setVisibility(View.INVISIBLE);
        pauseResumeButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
    }

    private void setTimer(long timeInMilis) {
        int sec = (int) timeInMilis / 1000;

        int min = sec / 60;
        sec = sec % 60;

        int hour = min / 60;
        min = min % 60;

        secondsValue += sec;
        minutesValue += min;
        hoursValue += hour;

        setTextViews();
        setProgressBars(hoursValue, minutesValue, secondsValue);
    }

    @Override
    public void onStop() {
        super.onStop();
        editor = sharedPreferences.edit();
        GregorianCalendar fragmentPauseTime = new GregorianCalendar(TimeZone.getDefault());
        editor.putLong("fragmentPauseTime", fragmentPauseTime.getTimeInMillis());
        editor.putLong("work_id", mWorkId);
        editor.putLong("pause_id", mPauseId);
        editor.putLong("totalPauseTime", actualPauseTime);
        editor.putInt("hours", hoursValue);
        editor.putInt("minutes", minutesValue);
        editor.putInt("seconds", secondsValue);
        if (isWorkFinished) {
            stringsList.clear();
        }
        if (countUpTimer != null) {
            countUpTimer.stop();
        }
        saveList(editor);
        editor.apply();
    }

    private void init() {
        String[] strings = getResources().getStringArray(R.array.start_work_button);
        hoursProgressBar = view.findViewById(R.id.fragment_timer_pb_hour);
        minutesProgressBar = view.findViewById(R.id.fragment_timer_pb_minute);
        secondsProgressBar = view.findViewById(R.id.fragment_timer_pb_second);

        hoursTextView = view.findViewById(R.id.fragment_timer_tv_hour);
        minutesTextView = view.findViewById(R.id.fragment_timer_tv_minute);
        secondsTextView = view.findViewById(R.id.fragment_timer_tv_second);
        setTextViews();

        startButton = view.findViewById(R.id.fragment_timer_button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButtonClick();
            }
        });
        pauseResumeButton = view.findViewById(R.id.fragment_timer_button_pause_resume);
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseResumeButtonClick();
            }
        });
        stopButton = view.findViewById(R.id.fragment_timer_button_stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopButtonClick();
            }
        });

        if (isWorkFinished) {
            startButton.setVisibility(View.INVISIBLE);
            pauseResumeButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
        }
        overviewRecyclerView = view.findViewById(R.id.fragment_timer_rv);
        overviewRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        overviewRecyclerView.setLayoutManager(layoutManager);

        recyclerviewAdapter = new TimerAdapter(stringsList);
        overviewRecyclerView.setAdapter(recyclerviewAdapter);
    }

    private void startButtonClick() {
        stringsList.clear();
        shiftBeginTime = new GregorianCalendar(TimeZone.getDefault());
        shiftBeginTime.set(Calendar.MILLISECOND, 0);
        String shiftBeginTimeString = convertToString(this.shiftBeginTime.getTime());

        if (shiftBeginTimeString != null) {
            isWorkFinished = false;
            stringsList.add(shiftBeginTimeString);
            recyclerviewAdapter.notifyRecyclerview();
            workTime = new WorkTime(shiftBeginTime, false);
            mWorkId = fragmentViewModel.insertWorkTime(workTime);
            runProgressBar(0, 0, -1);

            Snackbar.make(getActivity().findViewById(R.id.fragmentContainer), "Success insert.", Snackbar.LENGTH_LONG).show();
            startButton.setVisibility(View.INVISIBLE);
            pauseResumeButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
        }
    }

    private void pauseResumeButtonClick() {
        if (!isPaused) {
            pauseBegin = new GregorianCalendar(TimeZone.getDefault());
            pauseBegin.set(Calendar.MILLISECOND, 0);
            String pauseBeginString = convertToString(pauseBegin.getTime());
            if (pauseBeginString != null) {
                pauseTime = new PauseTime(mWorkId, pauseBegin, false);

                stringsList.add(pauseBeginString);
                recyclerviewAdapter.notifyRecyclerview();
                mPauseId = fragmentViewModel.insertPauseTime(pauseTime);
                progressBarThread = null;
                countUpTimer.stop();
                isPaused = true;
                pauseResumeButton.setText(R.string.timer_resume);
            }
        } else {
            pauseEnd = new GregorianCalendar(TimeZone.getDefault());
            pauseEnd.set(Calendar.MILLISECOND, 0);
            String pauseEndString = convertToString(pauseEnd.getTime());
            pauseTime.setPauseEnd(pauseEnd);
            long diff = calculateDifference(pauseBegin.getTimeInMillis(), pauseEnd.getTimeInMillis());
            actualPauseTime = +diff;
            pauseTime.setPauseTime(diff);
            pauseTime.setFinished(true);
            int numberOfRowUpdated = fragmentViewModel.updatePauseTime(pauseTime);
            stringsList.add(pauseEndString);
            recyclerviewAdapter.notifyRecyclerview();
            mPauseId = 0;
            isPaused = false;
            runProgressBar(hoursValue, minutesValue, secondsValue);
            pauseResumeButton.setText(R.string.timer_pause);
        }
    }

    private void stopButtonClick() {
        shiftEndTime = new GregorianCalendar(TimeZone.getDefault());
        shiftEndTime.set(Calendar.MILLISECOND, 0);
        String shiftEndTimeString = convertToString(shiftEndTime.getTime());
        long timeDifference = calculateDifference(shiftBeginTime.getTimeInMillis(), shiftEndTime.getTimeInMillis());
        isWorkFinished = true;

        if (isPaused) {
            pauseEnd = new GregorianCalendar(TimeZone.getDefault());
            pauseEnd.set(Calendar.MILLISECOND, 0);
            String pauseEndString = convertToString(pauseEnd.getTime());
            pauseTime.setPauseEnd(pauseEnd);
            long diff = calculateDifference(pauseBegin.getTimeInMillis(), pauseEnd.getTimeInMillis());
            actualPauseTime += diff;
            pauseTime.setPauseTime(diff);
            pauseTime.setFinished(true);
            int numberOfRowUpdated = fragmentViewModel.updatePauseTime(pauseTime);
            stringsList.add(pauseEndString);
            recyclerviewAdapter.notifyRecyclerview();
            mPauseId = 0;
            isPaused = false;
        }
        stringsList.add(shiftEndTimeString);
        recyclerviewAdapter.notifyRecyclerview();
        timeDifference -= actualPauseTime;
        workTime.setShiftEnd(shiftEndTime);
        workTime.setWorkTime(timeDifference);
        workTime.setFinished(true);
        int numberOfRowUpdated = fragmentViewModel.updateWorkTime(workTime);

        mWorkId = 0;

        progressBarThread = null;
        countUpTimer.stop();
        setProgressBars(0, 0, 0);
        setTextViews();
        Snackbar.make(getActivity().findViewById(R.id.fragmentContainer), "Success update.", Snackbar.LENGTH_LONG).show();

        pauseResumeButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
    }

    private void runProgressBar(int hoursProgressValue, int minutesProgressValue, int secondsProgressValue) {
        hoursValue = hoursProgressValue;
        minutesValue = minutesProgressValue;
        secondsValue = secondsProgressValue;

        countUpTimer = new CountUpTimer(1000) {
            @Override
            public void onTick(long elapsedTime) {
                secondsValue += 1;
                secondsProgressBar.setProgress(secondsValue);

                if (secondsValue > 59) {
                    secondsValue = 0;
                    secondsProgressBar.setProgress(secondsValue);
                    minutesValue += 1;
                    minutesProgressBar.setProgress(minutesValue);
                }

                if (minutesValue > 59) {
                    minutesValue = 0;
                    minutesProgressBar.setProgress(minutesValue);
                    hoursValue += 1;
                    hoursProgressBar.setProgress(hoursValue);
                }
                setTextViews();
            }
        };
        countUpTimer.start();

    }

    private long calculateDifference(long time1, long time2) {
        return (time1 - time2) * (-1);
    }

    private static String convertToString(Date date) {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.format(date);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private void setTextViews() {
        if (secondsValue < 10) {
            if (secondsValue == 1) {
                secondsTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_second), secondsValue));
            } else {
                secondsTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_seconds), secondsValue));
            }
        } else {
            secondsTextView.setText(String.format(Locale.getDefault(), "%d " + getString(timer_seconds), secondsValue));
        }

        if (minutesValue < 10) {
            if (minutesValue == 1) {
                minutesTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_minute), minutesValue));
            } else {
                minutesTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_minutes), minutesValue));
            }
        } else {
            minutesTextView.setText(String.format(Locale.getDefault(), "%d " + getString(timer_minutes), minutesValue));
        }

        if (hoursValue < 10) {
            if (hoursValue == 1) {
                hoursTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_hour), hoursValue));
            } else {
                hoursTextView.setText(String.format(Locale.getDefault(), "0%d " + getString(timer_hours), hoursValue));
            }
        } else {
            hoursTextView.setText(String.format(Locale.getDefault(), "%d " + getString(timer_hours), hoursValue));
        }
    }

    private void setProgressBars(int h, int m, int s) {
        hoursProgressBar.setProgress(h);
        minutesProgressBar.setProgress(m);
        secondsProgressBar.setProgress(s);
    }

    private ArrayList<String> loadList() {
        Set<String> set = sharedPreferences.getStringSet("stringSet", null);
        ArrayList<String> tempList = new ArrayList<>();
        if (set != null) {
            tempList.addAll(set);
        }
        return sortList(tempList);
    }

    private ArrayList<String> sortList(ArrayList<String> list) {
        ArrayList<String> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); ) {
            String latestDate = list.get(i);
            GregorianCalendar date1 = convertFromString(list.get(i));
            for (int j = 0; j <= list.size() - 1; j++) {
                GregorianCalendar date2 = convertFromString(list.get(j));
                if (date1.compareTo(date2) < 0) {
                    latestDate = list.get(j);
                    date1 = convertFromString(latestDate);
                }
            }
            list.remove(latestDate);
            newList.add(0, latestDate);
        }
        return newList;
    }

    private GregorianCalendar convertFromString(String s) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            df.setLenient(false);
            Date tempDate = df.parse(s);
            calendar.setTime(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    private void saveList(SharedPreferences.Editor editor1) {
        Set<String> set = new HashSet<>(stringsList);
        editor1.putStringSet("stringSet", set);
    }
}
