package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.Repository;
import com.example.patryk.work_time_app.data.WorkTime;

import java.util.Calendar;
import java.util.List;

public class EditFragmentViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<List<PauseTime>> pauseTimesWithWorkIdLiveData;
    private List<PauseTime> pauseTimesWithWorkId;

    public EditFragmentViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public int updateWorkTime(WorkTime workTime) {
        return repository.updateWorkTime(workTime);
    }

    public int deleteWorkTime(WorkTime workTime) {
        return repository.deleteWorkTime(workTime);
    }

    public WorkTime getOneWorkTimeBefore(String d1) {
        return repository.getOneWorkTimeBefore(d1);
    }

    public WorkTime getOneWorkTimeAfter(String d1) {
        return repository.getOneWorkTimeAfter(d1);
    }

    public long insertPauseTime(PauseTime pauseTime) {
        return repository.insertPauseTime(pauseTime);
    }

    public int updatePauseTime(PauseTime pauseTime) {
        return repository.updatePauseTime(pauseTime);
    }

    public LiveData<List<PauseTime>> getPauseTimesWithWorkIdLiveData(long workId) {
        if (pauseTimesWithWorkIdLiveData == null) {
            pauseTimesWithWorkIdLiveData = repository.getPauseTimesLiveData(workId);
        }
        return pauseTimesWithWorkIdLiveData;
    }

    public List<PauseTime> getPauseTimesWithWorkId(long workId) {
        pauseTimesWithWorkId = repository.getPauseTimes(workId);
        return pauseTimesWithWorkId;
    }

    public int deletePauseTime(PauseTime pauseTime) {
        return repository.deletePauseTime(pauseTime);
    }

    public PauseTime getOnePauseTimeBefore(String d1) {
        return repository.getOnePauseTimeBefore(d1);
    }

    public PauseTime getOnePauseTimeAfter(String d1) {
        return repository.getOnePauseTimeAfter(d1);
    }

    public long recalculateWorkTime(WorkTime workTime) {
        long totalWorkTime;
        long totalPauseTime = 0;

        Calendar shiftBegin = workTime.getShiftBegin();
        Calendar shiftEnd = workTime.getShiftEnd();

        totalWorkTime = Support.calculateDifference(shiftBegin.getTimeInMillis(), shiftEnd.getTimeInMillis());

        List<PauseTime> pauseTimesWithWorkId = getPauseTimesWithWorkId(workTime.getId());
        for (PauseTime p : pauseTimesWithWorkId) {
            totalPauseTime += p.getPauseTime();
        }

        return totalWorkTime - totalPauseTime;
    }
}
