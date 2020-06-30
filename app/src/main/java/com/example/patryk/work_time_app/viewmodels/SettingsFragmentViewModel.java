package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.patryk.work_time_app.data.Repository;

public class SettingsFragmentViewModel extends AndroidViewModel {

    private Repository mRepository;

    public SettingsFragmentViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public void clearDatabase() {
        mRepository.clearDatabase();
    }
}
