package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.data.Repository;

import java.util.Calendar;
import java.util.List;

public class HistoryFragmentViewModel extends AndroidViewModel {

    private Repository repository;
    private MutableLiveData<List<WorkTime>> workTimeListLD = new MutableLiveData<>();
    private List<WorkTime> workTimeRecordList;
    private List<PauseTime> mPauseTimesWithWorkId;

    public HistoryFragmentViewModel(Application application) {
        super(application);
        repository = new Repository(application);
    }

    public long insertWorkTime(WorkTime workTime) {
        return repository.insertWorkTime(workTime);
    }

    public LiveData<List<WorkTime>> getWorkTimeRecordList() {
        return repository.getAllWorkTimes();
    }

    public LiveData<List<WorkTime>> setTimeRecordListWithSpecifiedDate(String d1, String d2) {
        return repository.getWorkWithSpecifiedDateLiveData(d1, d2);

    }

    public List<WorkTime> getTimeRecordListWithSpecifiedDate(String d1, String d2) {
        return repository.getWorkWithSpecifiedDate(d1, d2);
    }

    public List<PauseTime> getPauseTimeRecordListWithWorkId(long workId) {
        mPauseTimesWithWorkId = repository.getPauseTimes(workId);
        return mPauseTimesWithWorkId;
    }

    public int deleteWorkTime(WorkTime workTime) {
        return repository.deleteWorkTime(workTime);
    }

    public boolean canBeCreated(Calendar newTime, List<WorkTime> specifiedWorkTimeRecordList) {
        if (specifiedWorkTimeRecordList == null || specifiedWorkTimeRecordList.size() == 0) {
            return true;
        } else {
            for (int i = 0; i < specifiedWorkTimeRecordList.size(); i++) {
                WorkTime workTimeToCompare2 = i == specifiedWorkTimeRecordList.size() - 1 ? null : specifiedWorkTimeRecordList.get(i + 1);
                if (i == 0 && newTime.before(specifiedWorkTimeRecordList.get(i).getShiftBegin())) {
                    return true;
                }
                if (workTimeToCompare2 != null) {
                    if (newTime.after(specifiedWorkTimeRecordList.get(i).getShiftEnd()) && newTime.before(specifiedWorkTimeRecordList.get(i + 1).getShiftBegin())) {
                        return true;
                    }
                } else {
                    if (newTime.after(specifiedWorkTimeRecordList.get(i).getShiftEnd())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

}
