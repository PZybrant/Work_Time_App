package com.example.patryk.work_time_app.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WorkTimeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWorkTime(WorkTime workTime);

    @Update(entity = WorkTime.class, onConflict = OnConflictStrategy.REPLACE)
    int updateWorkTime(WorkTime workTime);

    @Delete
    int deleteWorkTime(WorkTime workTime);

    @Delete
    int deleteWorkTime(WorkTime workTime, List<PauseTime> pauseTimes);

    @Query("SELECT * FROM workTimes")
    LiveData<List<WorkTime>> getAll();

    @Query("SELECT * FROM workTimes WHERE workId = :id")
    WorkTime getOne(long id);

    @Query("SELECT * FROM workTimes WHERE shiftBegin >= :d1 AND shiftBegin <= :d2 AND shiftEnd >= :d1 AND shiftEnd <= :d2")
    LiveData<List<WorkTime>> getWorkWithSpecifiedDate(String d1, String d2);

    @Transaction
    @Query("SELECT * FROM workTimes")
    List<WorkAndPauseTime> getWorkAndPauseTimes();

}