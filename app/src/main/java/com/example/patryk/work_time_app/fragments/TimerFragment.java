package com.example.patryk.work_time_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
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

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.adapters.TimerFragmentOverviewAdapter;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.viewmodels.TimerFragmentViewModel;
import com.example.patryk.work_time_app.data.WorkTime;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private GregorianCalendar shiftBeginTime, shiftEndTime, pauseBegin, pauseEnd;
    private ProgressBar hoursProgressBar, minutesProgressBar, secondsProgressBar;
    private TextView hoursTextView, minutesTextView, secondsTextView;
    private Button startButton, pauseResumeButton, stopButton;
    private Group buttonsGroup;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView overviewRecyclerView;
    private TimerFragmentOverviewAdapter recyclerviewAdapter;
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
        view = inflater.inflate(R.layout.new_timer_fragment, container, false);
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
                pauseBegin = convertFromString(pauseTime.getPauseBegin());
                isPaused = true;
            }
            resumeTimer();
        }
    }

    private void resumeTimer() {
        if (!isWorkFinished && isPaused) {
            secondsTextView.setText(String.format(Locale.getDefault(), "%d " + getString(R.string.seconds_text), secondsValue));
            minutesTextView.setText(String.format(Locale.getDefault(), "%d " + getString(R.string.minutes_text), minutesValue));
            hoursTextView.setText(String.format(Locale.getDefault(), "%d " + getString(hours_text), hoursValue));
            hoursProgressBar.setProgress(hoursValue);
            minutesProgressBar.setProgress(minutesValue);
            secondsProgressBar.setProgress(secondsValue);
            pauseResumeButton.setText(R.string.resume);
        } else if (!isWorkFinished && !isPaused) {
            GregorianCalendar now = new GregorianCalendar(TimeZone.getDefault());
            long fragmentPauseTime = sharedPreferences.getLong("fragmentPauseTime", -1);
            setTimer(calculateDifference(fragmentPauseTime, now.getTimeInMillis()));
            runProgressBar(hoursValue, minutesValue, secondsValue);
        }
        shiftBeginTime = convertFromString(workTime.getShiftBegin());
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

        secondsTextView.setText(String.format(Locale.getDefault(), "%d " + getString(R.string.seconds_text), secondsValue));
        minutesTextView.setText(String.format(Locale.getDefault(), "%d " + getString(R.string.minutes_text), minutesValue));
        hoursTextView.setText(String.format(Locale.getDefault(), "%d " + getString(hours_text), hoursValue));
        hoursProgressBar.setProgress(hoursValue);
        minutesProgressBar.setProgress(minutesValue);
        secondsProgressBar.setProgress(secondsValue);
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
        saveList(editor);
        editor.apply();
    }

    private void init() {
        String[] strings = getResources().getStringArray(R.array.start_work_button);
        hoursProgressBar = view.findViewById(R.id.hours_progress_bar);
        minutesProgressBar = view.findViewById(R.id.minutes_progress_bar);
        secondsProgressBar = view.findViewById(R.id.seconds_progress_bar);

        hoursTextView = view.findViewById(R.id.hours_text_view);
        minutesTextView = view.findViewById(R.id.minutes_text_view);
        secondsTextView = view.findViewById(R.id.seconds_text_view);

        startButton = view.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButtonClick();
            }
        });
        pauseResumeButton = view.findViewById(R.id.pause_resume_button);
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseResumeButtonClick();
            }
        });
        stopButton = view.findViewById(R.id.stop_button);
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
        overviewRecyclerView = view.findViewById(R.id.overview_recycler_view);
        overviewRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        overviewRecyclerView.setLayoutManager(layoutManager);

        recyclerviewAdapter = new TimerFragmentOverviewAdapter(stringsList);
        overviewRecyclerView.setAdapter(recyclerviewAdapter);
    }

    private void startButtonClick() {
        stringsList.clear();
        shiftBeginTime = new GregorianCalendar(TimeZone.getDefault());
        String shiftBeginTimeString = convertToString(this.shiftBeginTime.getTime());

        if (shiftBeginTimeString != null) {
            isWorkFinished = false;
            stringsList.add(shiftBeginTimeString);
            recyclerviewAdapter.notifyRecyclerview();
            workTime = new WorkTime(shiftBeginTimeString, false);
            mWorkId = fragmentViewModel.insertWorkTime(workTime);
            runProgressBar(0, 0, -1);
            System.out.println(mWorkId);
            System.out.println(workTime.toString());

            Snackbar.make(getActivity().findViewById(R.id.fragmentContainer), "Success insert.", Snackbar.LENGTH_LONG).show();
            startButton.setVisibility(View.INVISIBLE);
            pauseResumeButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
        }
    }

    private void pauseResumeButtonClick() {
        if (!isPaused) {
            pauseBegin = new GregorianCalendar(TimeZone.getDefault());
            String pauseBeginString = convertToString(pauseBegin.getTime());
            if (pauseBeginString != null) {
                pauseTime = new PauseTime(mWorkId, pauseBeginString, false);

                stringsList.add(pauseBeginString);
                recyclerviewAdapter.notifyRecyclerview();
                mPauseId = fragmentViewModel.insertPauseTime(pauseTime);
                progressBarThread = null;
                isPaused = true;
                pauseResumeButton.setText(R.string.resume);
            }
        } else {
            pauseEnd = new GregorianCalendar(TimeZone.getDefault());
            String pauseEndString = convertToString(pauseEnd.getTime());
            pauseTime.setPauseEnd(pauseEndString);
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
            pauseResumeButton.setText(R.string.pause);
        }
    }

    private void stopButtonClick() {
        shiftEndTime = new GregorianCalendar(TimeZone.getDefault());
        String shiftEndTimeString = convertToString(shiftEndTime.getTime());
        long timeDifference = calculateDifference(shiftBeginTime.getTimeInMillis(), shiftEndTime.getTimeInMillis());
        timeDifference -= actualPauseTime;
        isWorkFinished = true;

        if (isPaused) {
            pauseEnd = new GregorianCalendar(TimeZone.getDefault());
            String pauseEndString = convertToString(pauseEnd.getTime());
            pauseTime.setPauseEnd(pauseEndString);
            pauseTime.setPauseTime(calculateDifference(pauseBegin.getTimeInMillis(), pauseEnd.getTimeInMillis()));
            pauseTime.setFinished(true);
            int numberOfRowUpdated = fragmentViewModel.updatePauseTime(pauseTime);
            stringsList.add(pauseEndString);
            recyclerviewAdapter.notifyRecyclerview();
            mPauseId = 0;
            isPaused = false;
        }
        stringsList.add(shiftEndTimeString);
        recyclerviewAdapter.notifyRecyclerview();
        workTime.setShiftEnd(shiftEndTimeString);
        workTime.setWorkTime(timeDifference);
        workTime.setFinished(true);
        int numberOfRowUpdated = fragmentViewModel.updateWorkTime(workTime);

        mWorkId = 0;

        progressBarThread = null;
        hoursProgressBar.setProgress(0);
        minutesProgressBar.setProgress(0);
        secondsProgressBar.setProgress(0);
        secondsTextView.setText(R.string.seconds_text);
        minutesTextView.setText(R.string.minutes_text);
        hoursTextView.setText(hours_text);
        Snackbar.make(getActivity().findViewById(R.id.fragmentContainer), "Success update.", Snackbar.LENGTH_LONG).show();

        pauseResumeButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
    }

    private void runProgressBar(int hoursProgressValue, int minutesProgressValue, int secondsProgressValue) {
        hoursValue = hoursProgressValue;
        minutesValue = minutesProgressValue;
        secondsValue = secondsProgressValue;


        progressBarThread = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                while (progressBarThread != null) {
                    long startTime = System.currentTimeMillis();
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

                    view.post(new Runnable() {
                        @Override
                        public void run() {

                            secondsTextView.setText(String.format(Locale.getDefault(), "%d " + getString(R.string.seconds_text), secondsValue));
                            minutesTextView.setText(String.format(Locale.getDefault(), "%d " + getString(R.string.minutes_text), minutesValue));
                            hoursTextView.setText(String.format(Locale.getDefault(), "%d " + getString(hours_text), hoursValue));
                        }
                    });
                    long diffTime = System.currentTimeMillis() - startTime;

                    try {
                        Thread.sleep(1000 - diffTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        progressBarThread.start();

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
