package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.patryk.work_time_app.data.WorkTimeRepository;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkAndPauseTime;
import com.example.patryk.work_time_app.data.WorkTime;

import java.util.List;

public class TimerFragmentViewModel extends AndroidViewModel {

    private WorkTimeRepository mWorkTimeRepository;
    private WorkTime mWorkTime;
    private PauseTime mPauseTime;

    public TimerFragmentViewModel(Application application) {
        super(application);
        mWorkTimeRepository = new WorkTimeRepository(application);
    }

    public long insertWorkTime(WorkTime workTime) {
        return mWorkTimeRepository.insertWorkTime(workTime);
    }

    public int updateWorkTime(WorkTime workTime) {
        return mWorkTimeRepository.updateWorkTime(workTime);
    }

    public WorkTime getOneWorkTime(long id) {
        this.mWorkTime = mWorkTimeRepository.getOneWorkTime(id);
        return this.mWorkTime;
    }

    public long insertPauseTime(PauseTime pauseTime) {
        return mWorkTimeRepository.insertPauseTime(pauseTime);
    }

    public int updatePauseTime(PauseTime pauseTime) {
        return mWorkTimeRepository.updatePauseTime(pauseTime);
    }

    public PauseTime getOnePauseTime(long id) {
        this.mPauseTime = mWorkTimeRepository.getOnePauseTime(id);
        return this.mPauseTime;
    }
}
