package com.example.patryk.work_time_app.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WorkTimeRepository {

    private WorkTimeDAO mWorkTimeDAO;
    private PauseTimeDAO mPauseTimeDAO;
    private LiveData<List<WorkTime>> mWorkTimeList;
    private WorkTime mWorkTime;
    private List<WorkAndPauseTime> mWorkAndPauseTimeList;
    private LiveData<List<PauseTime>> mAllPauseTimeList;
    private List<PauseTime> mPauseTimeList;
    private PauseTime mPauseTime;


    public WorkTimeRepository(Application application) {
        WorkTimeDatabase db = WorkTimeDatabase.getDatabase(application);
        mWorkTimeDAO = db.workTimeDAO();
        mPauseTimeDAO = db.pauseTimeDAO();
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
        Callable<Integer> insertCallable = () -> mWorkTimeDAO.deleteWorkTime(workTime);
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

    public int deleteWorkTime(WorkTime workTime, List<PauseTime> pauseTimes) {
        int rowDeleted = 0;
        Callable<Integer> insertCallable;
        if (pauseTimes != null || !pauseTimes.isEmpty()) {
            insertCallable = () -> mWorkTimeDAO.deleteWorkTime(workTime, pauseTimes);
        } else {
            insertCallable = () -> mWorkTimeDAO.deleteWorkTime(workTime);
        }

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

    public LiveData<List<WorkTime>> getAllWorkTimes() {
        this.mWorkTimeList = mWorkTimeDAO.getAll();
        return this.mWorkTimeList;
    }

    public WorkTime getOneWorkTime(long id) {
        Callable<WorkTime> insertCallable = () -> mWorkTimeDAO.getOne(id);

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

    public LiveData<List<WorkTime>> getWorkWithSpecifiedDate(String d1, String d2) {
        return mWorkTimeDAO.getWorkWithSpecifiedDate(d1, d2);
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

    public LiveData<List<PauseTime>> getAllPauseTimes() {
        this.mAllPauseTimeList = mPauseTimeDAO.getAll();
        return this.mAllPauseTimeList;
    }

    public List<PauseTime> getPauseTimes(long workId) {
        Callable<List<PauseTime>> callable = () -> mPauseTimeDAO.getPauseTimesWithSpecifiedWorkId(workId);
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

}
