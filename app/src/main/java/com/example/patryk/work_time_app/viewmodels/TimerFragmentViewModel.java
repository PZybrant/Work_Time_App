package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.example.patryk.work_time_app.CountUpTimer;
import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.data.Repository;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class TimerFragmentViewModel extends AndroidViewModel {

    private MutableLiveData<Integer> hoursValueLD = new MutableLiveData<>();
    private MutableLiveData<Integer> minutesValueLD = new MutableLiveData<>();
    private MutableLiveData<Integer> secondsValueLD = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> recordListLD;

    private int hoursValue, minutesValue, secondsValue;
    private ArrayList<String> recordList = new ArrayList<>();

    private Repository mRepository;

    private CountUpTimer countUpTimer;
    private SharedPreferences sharedPreferences;

    public TimerFragmentViewModel(Application application) {
        super(application);
        mRepository = new Repository(application);
        sharedPreferences = application.getSharedPreferences(application.getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        initializeData();
    }

    public long insertWorkTime(WorkTime workTime) {
        return mRepository.insertWorkTime(workTime);
    }

    public int updateWorkTime(WorkTime workTime) {
        return mRepository.updateWorkTime(workTime);
    }

    public WorkTime getOneWorkTime(long id) {
        return mRepository.getOneWorkTime(id);
    }

    public long insertPauseTime(PauseTime pauseTime) {
        return mRepository.insertPauseTime(pauseTime);
    }

    public int updatePauseTime(PauseTime pauseTime) {
        return mRepository.updatePauseTime(pauseTime);
    }

    public PauseTime getOnePauseTime(long id) {
        return mRepository.getOnePauseTime(id);
    }

    public LiveData<Integer> getHoursValue() {
        return hoursValueLD;
    }

    public LiveData<Integer> getMinutesValue() {
        return minutesValueLD;
    }

    public LiveData<Integer> getSecondsValue() {
        return secondsValueLD;
    }

    public LiveData<ArrayList<String>> getRecordList() {
        if (recordListLD == null) {
            recordListLD = new MutableLiveData<>();
            loadList();
        }
        return recordListLD;
    }

    public void clearRecordList() {
        recordList.clear();
        recordListLD.setValue(recordList);
    }

    public void addToRecordList(String newValue) {
        recordList.add(newValue);
        recordListLD.setValue(recordList);
    }

    public void startClock(Boolean resetValues) {
        if (resetValues) {
            hoursValue = 0;
            minutesValue = 0;
            secondsValue = -1;
        }
        countUpTimer = new CountUpTimer(1000) {
            @Override
            public void onTick(long elapsedTime) {
                secondsValue += 1;
                secondsValueLD.postValue(secondsValue);

                if (secondsValue > 59) {
                    secondsValue = 0;
                    minutesValue += 1;
                    secondsValueLD.postValue(secondsValue);
                    minutesValueLD.postValue(minutesValue);
                }

                if (minutesValue > 59) {
                    minutesValue = 0;
                    hoursValue += 1;
                    minutesValueLD.postValue(minutesValue);
                    hoursValueLD.postValue(hoursValue);
                }
            }
        };
        countUpTimer.start();
    }

    public void stopClock() {
        if (countUpTimer != null) {
            countUpTimer.stop();
        }
    }

    public void setClock(long timeInMilis) {
        int sec = (int) timeInMilis / 1000;

        int min = sec / 60;
        sec = sec % 60;

        int hour = min / 60;
        min = min % 60;

        secondsValue += sec;
        minutesValue += min;
        hoursValue += hour;
    }

    public ArrayList<String> sortList(ArrayList<String> list) {
        ArrayList<String> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); ) {
            String latestDate = list.get(i);
            Calendar date1 = Support.convertFromString(list.get(i));
            for (int j = 0; j <= list.size() - 1; j++) {
                Calendar date2 = Support.convertFromString(list.get(j));
                if (date1.compareTo(date2) < 0) {
                    latestDate = list.get(j);
                    date1 = Support.convertFromString(latestDate);
                }
            }
            list.remove(latestDate);
            newList.add(0, latestDate);
        }
        return newList;
    }

    private void initializeData() {
        hoursValue = sharedPreferences.getInt("hours", 0);
        minutesValue = sharedPreferences.getInt("minutes", 0);
        secondsValue = sharedPreferences.getInt("seconds", 0);

        hoursValueLD.postValue(hoursValue);
        minutesValueLD.postValue(minutesValue);
        secondsValueLD.postValue(secondsValue);
    }

    private void loadList() {
        Set<String> set = sharedPreferences.getStringSet("stringSet", null);
        if (set != null) {
            ArrayList<String> tempList = new ArrayList<>(set);
            recordList = sortList(tempList);
        }
        recordListLD.setValue(recordList);
    }

    public void saveList(SharedPreferences.Editor editor1) {
        Set<String> set = new HashSet<>(recordList);
        editor1.putStringSet("stringSet", set);
    }
}
