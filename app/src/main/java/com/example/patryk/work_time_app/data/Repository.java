package com.example.patryk.work_time_app.data;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Repository {

    private WorkTimeDAO mWorkTimeDAO;
    private PauseTimeDAO mPauseTimeDAO;
    private WorkTime mWorkTime;
    private LiveData<List<PauseTime>> mAllPauseTimeList, mPauseTimesWithWorkIdLiveData;
    private PauseTime mPauseTime;
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

    public long insertWorkTime(WorkTime workTime) {
        Callable<Long> insertCallable = () -> mWorkTimeDAO.insertWorkTime(workTime);
        long rowId = 0;

        Future<Long> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            rowId = future.get();
            workTime.setId(rowId);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return rowId;
    }

    public int updateWorkTime(WorkTime workTime) {
        Callable<Integer> insertCallable = () -> mWorkTimeDAO.updateWorkTime(workTime);
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

    public int deleteWorkTime(WorkTime workTime) {
        Callable<Integer> insertCallable = () -> {
            int workTimeRowDeleted = mWorkTimeDAO.deleteWorkTime(workTime);
            int pauseTimeRowDeleted = mPauseTimeDAO.deletePauseTimesWithWorkId(workTime.getId());
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

    public List<WorkTime> getAllWorkTimes() {
        Callable<List<WorkTime>> callable = () -> mWorkTimeDAO.getAll();

        Future<List<WorkTime>> future = WorkTimeDatabase.databaseExecutor.submit(callable);

        List<WorkTime> strings = new ArrayList<>();

        try {
            strings = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return strings;

    }

    public WorkTime getOneWorkTime(long id) {
        Callable<WorkTime> callable = () -> mWorkTimeDAO.getOne(id);

        Future<WorkTime> future = WorkTimeDatabase.databaseExecutor.submit(callable);

        try {
            mWorkTime = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mWorkTime;
    }

    public WorkTime getOneWorkTimeBefore(String d1) {
        Callable<WorkTime> insertCallable = () -> mWorkTimeDAO.getOneBefore(d1);

        Future<WorkTime> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mWorkTime = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mWorkTime;
    }

    public WorkTime getOneWorkTimeAfter(String d1) {
        Callable<WorkTime> insertCallable = () -> mWorkTimeDAO.getOneAfter(d1);

        Future<WorkTime> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mWorkTime = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mWorkTime;
    }

    public LiveData<List<WorkTime>> getWorkWithSpecifiedDateLiveData(String d1, String d2) {
        return mWorkTimeDAO.getWorkWithSpecifiedDateLiveData(d1, d2);
    }

    public List<WorkTime> getWorkWithSpecifiedDate(String d1, String d2) {
        Callable<List<WorkTime>> callable = () -> mWorkTimeDAO.getWorkWithSpecifiedDate(d1, d2);
        List<WorkTime> workTimeList = new ArrayList<>();

        Future<List<WorkTime>> future = WorkTimeDatabase.databaseExecutor.submit(callable);
        try {
            workTimeList = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return workTimeList;
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

    public long insertPauseTime(PauseTime pauseTime) {
        Callable<Long> insertCallable = () -> mPauseTimeDAO.insertPauseTime(pauseTime);
        long rowId = 0;

        Future<Long> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            rowId = future.get();
            pauseTime.setId(rowId);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return rowId;
    }

    public int updatePauseTime(PauseTime pauseTime) {
        Callable<Integer> insertCallable = () -> mPauseTimeDAO.updatePauseTime(pauseTime);
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

    public int deletePauseTime(PauseTime pauseTime) {
        Callable<Integer> insertCallable = () -> mPauseTimeDAO.deletePauseTime(pauseTime);
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

    public LiveData<List<PauseTime>> getAllPauseTimes() {
        this.mAllPauseTimeList = mPauseTimeDAO.getAll();
        return this.mAllPauseTimeList;
    }

    public LiveData<List<PauseTime>> getPauseTimesLiveData(long workId) {
        mPauseTimesWithWorkIdLiveData = mPauseTimeDAO.getPauseTimesWithWorkIdLiveData(workId);
        return mPauseTimesWithWorkIdLiveData;
    }

    public List<PauseTime> getPauseTimes(long workId) {
        Callable<List<PauseTime>> callable = () -> mPauseTimeDAO.getPauseTimesWithWorkId(workId);
        List<PauseTime> pauseTimes = null;

        Future<List<PauseTime>> future = WorkTimeDatabase.databaseExecutor.submit(callable);

        try {
            pauseTimes = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return pauseTimes;
    }

    public PauseTime getOnePauseTime(long id) {
        Callable<PauseTime> insertCallable = () -> mPauseTimeDAO.getOne(id);

        Future<PauseTime> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mPauseTime = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mPauseTime;
    }

    public PauseTime getOnePauseTimeBefore(String d1) {
        Callable<PauseTime> insertCallable = () -> mPauseTimeDAO.getOneBefore(d1);

        Future<PauseTime> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mPauseTime = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mPauseTime;
    }

    public PauseTime getOnePauseTimeAfter(String d1) {
        Callable<PauseTime> insertCallable = () -> mPauseTimeDAO.getOneAfter(d1);

        Future<PauseTime> future = WorkTimeDatabase.databaseExecutor.submit(insertCallable);

        try {
            mPauseTime = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        return this.mPauseTime;
    }

}
