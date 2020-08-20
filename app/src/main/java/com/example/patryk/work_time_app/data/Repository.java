package com.example.patryk.work_time_app.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Repository {

    private WorkTimeDAO mWorkTimeDAO;
    private PauseTimeDAO mPauseTimeDAO;
    private WorkTimeRecord mWorkTimeRecord;
    private LiveData<List<PauseTimeRecord>> mAllPauseTimeList, mPauseTimesWithWorkIdLiveData;
    private PauseTimeRecord mPauseTimeRecord;
    private WorkTimeDatabase db;


    public Repository(Application application) {
        db = WorkTimeDatabase.getDatabase(application);
        mWorkTimeDAO = db.workTimeDAO();
        mPauseTimeDAO = db.pauseTimeDAO();
    }

    public void clearDatabase() {
        Runnable runnable = () -> db.clearAllTables();
        WorkTimeDatabase.databaseExecutor.submit(runnable);
    }

    public long insertWorkTime(WorkTimeRecord workTimeRecord) {
        Callable<Long> insertCallable = () -> mWorkTimeDAO.insertWorkTime(workTimeRecord);
        long rowId = 0;

        Future<Long> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            rowId = future.get();
            workTimeRecord.setId(rowId);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return rowId;
    }

    public int updateWorkTime(WorkTimeRecord workTimeRecord) {
        Callable<Integer> insertCallable = () -> mWorkTimeDAO.updateWorkTime(workTimeRecord);
        int rowIUpdated = 0;

        Future<Integer> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            rowIUpdated = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return rowIUpdated;
    }

    public int deleteWorkTime(WorkTimeRecord workTimeRecord) {
        Callable<Integer> insertCallable = () -> {
            int workTimeRowDeleted = mWorkTimeDAO.deleteWorkTime(workTimeRecord);
            int pauseTimeRowDeleted = mPauseTimeDAO.deletePauseTimesWithWorkId(workTimeRecord.getId());
            return workTimeRowDeleted + pauseTimeRowDeleted;
        };

        int rowDeleted = 0;

        Future<Integer> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            rowDeleted = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return rowDeleted;
    }

    public LiveData<List<WorkTimeRecord>> getAllWorkTimes() {

        return mWorkTimeDAO.getAll();

    }

    public WorkTimeRecord getOneWorkTime(long id) {
        Callable<WorkTimeRecord> callable = () -> mWorkTimeDAO.getOne(id);

        Future<WorkTimeRecord> future = WorkTimeDatabase.databaseExecutor.submit(callable);

        try {
            mWorkTimeRecord = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mWorkTimeRecord;
    }

    public WorkTimeRecord getOneWorkTimeBefore(String d1) {
        Callable<WorkTimeRecord> insertCallable = () -> mWorkTimeDAO.getOneBefore(d1);

        Future<WorkTimeRecord> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mWorkTimeRecord = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mWorkTimeRecord;
    }

    public WorkTimeRecord getOneWorkTimeAfter(String d1) {
        Callable<WorkTimeRecord> insertCallable = () -> mWorkTimeDAO.getOneAfter(d1);

        Future<WorkTimeRecord> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mWorkTimeRecord = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mWorkTimeRecord;
    }

    public LiveData<List<WorkTimeRecord>> getWorkWithSpecifiedDateLiveData(String d1, String d2) {
        return mWorkTimeDAO.getWorkWithSpecifiedDateLiveData(d1, d2);
    }

    public List<WorkTimeRecord> getWorkWithSpecifiedDate(String d1, String d2) {
        Callable<List<WorkTimeRecord>> callable = () -> mWorkTimeDAO.getWorkWithSpecifiedDate(d1, d2);
        List<WorkTimeRecord> workTimeRecordList = new ArrayList<>();

        Future<List<WorkTimeRecord>> future = WorkTimeDatabase.databaseExecutor.submit(callable);
        try {
            workTimeRecordList = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return workTimeRecordList;
    }

    public List<WorkAndPauseTime> getWorkAndPauseTimes() {
        Callable<List<WorkAndPauseTime>> callable = () -> mWorkTimeDAO.getWorkAndPauseTimes();
        List<WorkAndPauseTime> workAndPauseTimeList = new ArrayList<>();

        Future<List<WorkAndPauseTime>> future = WorkTimeDatabase.databaseExecutor.submit(callable);

        try {
            workAndPauseTimeList = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return workAndPauseTimeList;
    }

    public long insertPauseTime(PauseTimeRecord pauseTimeRecord) {
        Callable<Long> insertCallable = () -> mPauseTimeDAO.insertPauseTime(pauseTimeRecord);
        long rowId = 0;

        Future<Long> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            rowId = future.get();
            pauseTimeRecord.setId(rowId);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return rowId;
    }

    public int updatePauseTime(PauseTimeRecord pauseTimeRecord) {
        Callable<Integer> insertCallable = () -> mPauseTimeDAO.updatePauseTime(pauseTimeRecord);
        int rowUpdated = 0;

        Future<Integer> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            rowUpdated = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return rowUpdated;
    }

    public int deletePauseTime(PauseTimeRecord pauseTimeRecord) {
        Callable<Integer> insertCallable = () -> mPauseTimeDAO.deletePauseTime(pauseTimeRecord);
        int rowDeleted = 0;

        Future<Integer> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            rowDeleted = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return rowDeleted;
    }

    public int deletePauseTimesWithWorkId(long workId) {
        Callable<Integer> insertCallable = () -> mPauseTimeDAO.deletePauseTimesWithWorkId(workId);
        int rowDeleted = 0;

        Future<Integer> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            rowDeleted = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return rowDeleted;
    }

    public LiveData<List<PauseTimeRecord>> getAllPauseTimes() {
        this.mAllPauseTimeList = mPauseTimeDAO.getAll();
        return this.mAllPauseTimeList;
    }

    public LiveData<List<PauseTimeRecord>> getPauseTimesLiveData(long workId) {
        mPauseTimesWithWorkIdLiveData = mPauseTimeDAO.getPauseTimesWithWorkIdLiveData(workId);
        return mPauseTimesWithWorkIdLiveData;
    }

    public List<PauseTimeRecord> getPauseTimes(long workId) {
        Callable<List<PauseTimeRecord>> callable = () -> mPauseTimeDAO.getPauseTimesWithWorkId(workId);
        List<PauseTimeRecord> pauseTimeRecords = null;

        Future<List<PauseTimeRecord>> future = WorkTimeDatabase.databaseExecutor.submit(callable);

        try {
            pauseTimeRecords = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return pauseTimeRecords;
    }

    public PauseTimeRecord getOnePauseTime(long id) {
        Callable<PauseTimeRecord> insertCallable = () -> mPauseTimeDAO.getOne(id);

        Future<PauseTimeRecord> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mPauseTimeRecord = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mPauseTimeRecord;
    }

    public PauseTimeRecord getOnePauseTimeBefore(String d1) {
        Callable<PauseTimeRecord> insertCallable = () -> mPauseTimeDAO.getOneBefore(d1);

        Future<PauseTimeRecord> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mPauseTimeRecord = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mPauseTimeRecord;
    }

    public PauseTimeRecord getOnePauseTimeAfter(String d1) {
        Callable<PauseTimeRecord> insertCallable = () -> mPauseTimeDAO.getOneAfter(d1);

        Future<PauseTimeRecord> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mPauseTimeRecord = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mPauseTimeRecord;
    }

}
