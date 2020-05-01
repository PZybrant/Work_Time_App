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
    private LiveData<List<WorkTime>> mWorkTimeList;
    private LiveData<List<WorkTime>> mWorkTimeListFilterdByDate;
    private LiveData<List<PauseTime>> pauseTimesWithWorkId;

    public HistoryFragmentViewModel(Application application) {
        super(application);
        repository = new WorkTimeRepository(application);
    }

    public LiveData<List<WorkTime>> getWorkTimeList() {
        this.mWorkTimeList = repository.getAllWorkTimes();
        return this.mWorkTimeList;
    }

    public LiveData<List<WorkTime>> getWorkWithSpecifiedDate(String d1, String d2) {
        this.mWorkTimeListFilterdByDate = repository.getWorkWithSpecifiedDate(d1, d2);
        return this.mWorkTimeListFilterdByDate;
    }

    public List<PauseTime> getPauseTimesWithWorkId(long workId) {
        return repository.getPauseTimes(workId);
    }

    public int deleteWorkTime(WorkTime workTime, List<PauseTime> pauseTimes) {
        return repository.deleteWorkTime(workTime, pauseTimes);
    }

    public int deletePauseTime(PauseTime pauseTime) {
        return repository.deletePauseTime(pauseTime);
    }
}
