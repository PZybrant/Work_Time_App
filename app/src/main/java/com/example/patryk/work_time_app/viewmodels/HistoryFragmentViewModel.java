package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.data.Repository;

import java.util.List;

public class HistoryFragmentViewModel extends AndroidViewModel {

    private Repository mRepository;
    private WorkTime mWorkTime;
    private LiveData<List<WorkTime>> mWorkTimeList;
    private LiveData<List<WorkTime>> mWorkTimeListFilterdByDateLiveData;
    private List<WorkTime> mWorkTimeListFilterdByDate;
    private List<PauseTime> mPauseTimesWithWorkId;
    private LiveData<List<PauseTime>> mPauseTimesWithWorkIdLiveData;

    public HistoryFragmentViewModel(Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public long insertWorkTime(WorkTime workTime) {
        return mRepository.insertWorkTime(workTime);
    }

    public int updateWorkTime(WorkTime workTime) {
        return mRepository.updateWorkTime(workTime);
    }

    public LiveData<List<WorkTime>> getWorkTimeList() {
        this.mWorkTimeList = mRepository.getAllWorkTimes();
        return this.mWorkTimeList;
    }

    public WorkTime getOneWorkTime(long id) {
        mWorkTime = mRepository.getOneWorkTime(id);
        return mWorkTime;
    }

    public LiveData<List<WorkTime>> getWorkWithSpecifiedDateLiveData(String d1, String d2) {
        this.mWorkTimeListFilterdByDateLiveData = mRepository.getWorkWithSpecifiedDateLiveData(d1, d2);
        return this.mWorkTimeListFilterdByDateLiveData;
    }

    public List<WorkTime> getWorkWithSpecifiedDate(String d1, String d2) {
        this.mWorkTimeListFilterdByDate = mRepository.getWorkWithSpecifiedDate(d1, d2);
        return this.mWorkTimeListFilterdByDate;
    }

    public LiveData<List<PauseTime>> getPauseTimesWithWorkIdLiveData(long workId) {
        mPauseTimesWithWorkIdLiveData = mRepository.getPauseTimesLiveData(workId);
        return mPauseTimesWithWorkIdLiveData;
    }

    public List<PauseTime> getPauseTimesWithWorkId(long workId) {
        mPauseTimesWithWorkId = mRepository.getPauseTimes(workId);
        return mPauseTimesWithWorkId;
    }

    public int deleteWorkTime(WorkTime workTime) {
        return mRepository.deleteWorkTime(workTime);
    }

    public int deletePauseTime(PauseTime pauseTime) {
        return mRepository.deletePauseTime(pauseTime);
    }
}
