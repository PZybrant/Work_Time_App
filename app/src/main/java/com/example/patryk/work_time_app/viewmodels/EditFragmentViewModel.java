package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.data.PauseTimeRecord;
import com.example.patryk.work_time_app.data.Repository;
import com.example.patryk.work_time_app.data.WorkTimeRecord;

import java.util.Calendar;
import java.util.List;

public class EditFragmentViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<PauseTimeRecord>> pauseTimesWithWorkIdLiveData;
    private List<PauseTimeRecord> pauseTimesWithWorkIdRecord;

    public EditFragmentViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public int updateWorkTime(WorkTimeRecord workTimeRecord) {
        return repository.updateWorkTime(workTimeRecord);
    }

    public int deleteWorkTime(WorkTimeRecord workTimeRecord) {
        return repository.deleteWorkTime(workTimeRecord);
    }

    public WorkTimeRecord getOneWorkTimeBefore(String d1) {
        return repository.getOneWorkTimeBefore(d1);
    }

    public WorkTimeRecord getOneWorkTimeAfter(String d1) {
        return repository.getOneWorkTimeAfter(d1);
    }

    public long insertPauseTime(PauseTimeRecord pauseTimeRecord) {
        return repository.insertPauseTime(pauseTimeRecord);
    }

    public int updatePauseTime(PauseTimeRecord pauseTimeRecord) {
        return repository.updatePauseTime(pauseTimeRecord);
    }

    public LiveData<List<PauseTimeRecord>> getPauseTimesWithWorkIdLiveData(long workId) {
        if (pauseTimesWithWorkIdLiveData == null) {
            pauseTimesWithWorkIdLiveData = repository.getPauseTimesLiveData(workId);
        }
        return pauseTimesWithWorkIdLiveData;
    }

    public List<PauseTimeRecord> getPauseTimesWithWorkId(long workId) {
        pauseTimesWithWorkIdRecord = repository.getPauseTimes(workId);
        return pauseTimesWithWorkIdRecord;
    }

    public int deletePauseTime(PauseTimeRecord pauseTimeRecord) {
        return repository.deletePauseTime(pauseTimeRecord);
    }

    public PauseTimeRecord getOnePauseTimeBefore(String d1) {
        return repository.getOnePauseTimeBefore(d1);
    }

    public PauseTimeRecord getOnePauseTimeAfter(String d1) {
        return repository.getOnePauseTimeAfter(d1);
    }

    public long recalculateWorkTime(WorkTimeRecord workTimeRecord) {
        long totalWorkTime;
        long totalPauseTime = 0;

        Calendar shiftBegin = workTimeRecord.getShiftBegin();
        Calendar shiftEnd = workTimeRecord.getShiftEnd();

        totalWorkTime = Support.calculateDifference(shiftBegin.getTimeInMillis(), shiftEnd.getTimeInMillis());

        List<PauseTimeRecord> pauseTimesWithWorkIdRecord = getPauseTimesWithWorkId(workTimeRecord.getId());
        for (PauseTimeRecord p : pauseTimesWithWorkIdRecord) {
            totalPauseTime += p.getPauseTime();
        }

        return totalWorkTime - totalPauseTime;
    }
}
