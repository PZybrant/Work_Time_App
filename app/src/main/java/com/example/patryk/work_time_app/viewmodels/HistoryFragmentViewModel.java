package com.example.patryk.work_time_app.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.patryk.work_time_app.data.PauseTimeRecord;
import com.example.patryk.work_time_app.data.WorkTimeRecord;
import com.example.patryk.work_time_app.data.Repository;

import java.util.Calendar;
import java.util.List;

public class HistoryFragmentViewModel extends AndroidViewModel {

    private Repository repository;
    private MutableLiveData<List<WorkTimeRecord>> workTimeListLD = new MutableLiveData<>();
    private List<WorkTimeRecord> workTimeRecordRecordList;
    private List<PauseTimeRecord> mPauseTimesWithWorkIdRecord;

    public HistoryFragmentViewModel(Application application) {
        super(application);
        repository = new Repository(application);
    }

    public long insertWorkTime(WorkTimeRecord workTimeRecord) {
        return repository.insertWorkTime(workTimeRecord);
    }

    public LiveData<List<WorkTimeRecord>> getWorkTimeRecordRecordList() {
        return repository.getAllWorkTimes();
    }

    public LiveData<List<WorkTimeRecord>> setTimeRecordListWithSpecifiedDate(String d1, String d2) {
        return repository.getWorkWithSpecifiedDateLiveData(d1, d2);

    }

    public List<WorkTimeRecord> getTimeRecordListWithSpecifiedDate(String d1, String d2) {
        return repository.getWorkWithSpecifiedDate(d1, d2);
    }

    public List<PauseTimeRecord> getPauseTimeRecordListWithWorkId(long workId) {
        mPauseTimesWithWorkIdRecord = repository.getPauseTimes(workId);
        return mPauseTimesWithWorkIdRecord;
    }

    public int deleteWorkTime(WorkTimeRecord workTimeRecord) {
        return repository.deleteWorkTime(workTimeRecord);
    }

    public boolean canBeCreated(Calendar newTime, List<WorkTimeRecord> specifiedWorkTimeRecordRecordList) {
        if (specifiedWorkTimeRecordRecordList == null || specifiedWorkTimeRecordRecordList.size() == 0) {
            return true;
        } else {
            for (int i = 0; i < specifiedWorkTimeRecordRecordList.size(); i++) {
                WorkTimeRecord workTimeRecordToCompare2 = i == specifiedWorkTimeRecordRecordList.size() - 1 ? null : specifiedWorkTimeRecordRecordList.get(i + 1);
                if (i == 0 && newTime.before(specifiedWorkTimeRecordRecordList.get(i).getShiftBegin())) {
                    return true;
                }
                if (workTimeRecordToCompare2 != null) {
                    if (newTime.after(specifiedWorkTimeRecordRecordList.get(i).getShiftEnd()) && newTime.before(specifiedWorkTimeRecordRecordList.get(i + 1).getShiftBegin())) {
                        return true;
                    }
                } else {
                    if (newTime.after(specifiedWorkTimeRecordRecordList.get(i).getShiftEnd())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

}
