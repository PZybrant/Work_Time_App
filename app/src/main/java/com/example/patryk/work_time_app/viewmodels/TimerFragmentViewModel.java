package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.patryk.work_time_app.data.Repository;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;

public class TimerFragmentViewModel extends AndroidViewModel {

    private Repository mRepository;
    private WorkTime mWorkTime;
    private PauseTime mPauseTime;

    public TimerFragmentViewModel(Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public long insertWorkTime(WorkTime workTime) {
        return mRepository.insertWorkTime(workTime);
    }

    public int updateWorkTime(WorkTime workTime) {
        return mRepository.updateWorkTime(workTime);
    }

    public WorkTime getOneWorkTime(long id) {
        this.mWorkTime = mRepository.getOneWorkTime(id);
        return this.mWorkTime;
    }

    public long insertPauseTime(PauseTime pauseTime) {
        return mRepository.insertPauseTime(pauseTime);
    }

    public int updatePauseTime(PauseTime pauseTime) {
        return mRepository.updatePauseTime(pauseTime);
    }

    public PauseTime getOnePauseTime(long id) {
        this.mPauseTime = mRepository.getOnePauseTime(id);
        return this.mPauseTime;
    }
}
