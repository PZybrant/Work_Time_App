package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.data.WorkTimeRepository;

import java.util.List;

public class HistoryFragmentViewModel extends AndroidViewModel {

    private WorkTimeRepository repository;
    private WorkTime mWorkTime;
    private LiveData<List<WorkTime>> mWorkTimeList;
    private LiveData<List<WorkTime>> mWorkTimeListFilterdByDate;
    private List<PauseTime> mPauseTimesWithWorkId;

    public HistoryFragmentViewModel(Application application) {
        super(application);
        repository = new WorkTimeRepository(application);
    }

    public LiveData<List<WorkTime>> getWorkTimeList() {
        this.mWorkTimeList = repository.getAllWorkTimes();
        return this.mWorkTimeList;
    }

    public WorkTime getOneWorkTime(long id) {
        mWorkTime = repository.getOneWorkTime(id);
        return mWorkTime;
    }

    public LiveData<List<WorkTime>> getWorkWithSpecifiedDate(String d1, String d2) {
        this.mWorkTimeListFilterdByDate = repository.getWorkWithSpecifiedDate(d1, d2);
        return this.mWorkTimeListFilterdByDate;
    }

    public List<PauseTime> getPauseTimesWithWorkId(long workId) {
        mPauseTimesWithWorkId = repository.getPauseTimes(workId);
        return mPauseTimesWithWorkId;
    }

    public int deleteWorkTime(WorkTime workTime) {
        return repository.deleteWorkTime(workTime);
    }

    public int deletePauseTime(PauseTime pauseTime) {
        return repository.deletePauseTime(pauseTime);
    }
}
