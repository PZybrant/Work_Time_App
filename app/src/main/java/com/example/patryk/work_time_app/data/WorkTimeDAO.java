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
    long insertWorkTime(WorkTimeRecord workTimeRecord);

    @Update(entity = WorkTimeRecord.class, onConflict = OnConflictStrategy.REPLACE)
    int updateWorkTime(WorkTimeRecord workTimeRecord);

    @Delete
    int deleteWorkTime(WorkTimeRecord workTimeRecord);

    @Delete
    int deleteWorkTime(WorkTimeRecord workTimeRecord, List<PauseTimeRecord> pauseTimeRecords);

    @Query("SELECT * FROM workTimes ORDER BY shiftBegin ASC")
    LiveData<List<WorkTimeRecord>> getAll();

    @Query("SELECT * FROM workTimes WHERE workId = :id")
    WorkTimeRecord getOne(long id);

    @Query("SELECT * FROM workTimes  WHERE shiftEnd < :beginDate ORDER BY shiftEnd DESC LIMIT 1")
    WorkTimeRecord getOneBefore(String beginDate);

    @Query("SELECT * FROM workTimes WHERE shiftBegin > :endDate ORDER BY shiftBegin ASC LIMIT 1")
    WorkTimeRecord getOneAfter(String endDate);

    @Query("SELECT * FROM workTimes WHERE shiftBegin >= :d1 AND shiftBegin <= :d2 AND shiftEnd >= :d1 AND shiftEnd <= :d2 ORDER BY shiftBegin ASC")
    LiveData<List<WorkTimeRecord>> getWorkWithSpecifiedDateLiveData(String d1, String d2);

    @Query("SELECT * FROM workTimes WHERE shiftBegin >= :d1 AND shiftBegin <= :d2 AND shiftEnd >= :d1 AND shiftEnd <= :d2 ORDER BY shiftBegin ASC")
    List<WorkTimeRecord> getWorkWithSpecifiedDate(String d1, String d2);

    @Transaction
    @Query("SELECT * FROM workTimes")
    List<WorkAndPauseTime> getWorkAndPauseTimes();

}
