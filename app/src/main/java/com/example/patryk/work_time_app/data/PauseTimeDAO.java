package com.example.patryk.work_time_app.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PauseTimeDAO {

    /*Find a way to insert PauseTime with relation to WorkTime*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertPauseTime(PauseTimeRecord pauseTimeRecord);

    @Update
    int updatePauseTime(PauseTimeRecord pauseTimeRecord);

    @Delete
    int deletePauseTime(PauseTimeRecord pauseTimeRecord);

    @Query("DELETE FROM pauseTimes WHERE workPauseId = :workId")
    int deletePauseTimesWithWorkId(long workId);

    @Query("SELECT * FROM pauseTimes")
    LiveData<List<PauseTimeRecord>> getAll();

    @Query("SELECT * FROM pauseTimes WHERE workPauseId = :workId ORDER BY pauseBegin ASC")
    LiveData<List<PauseTimeRecord>> getPauseTimesWithWorkIdLiveData(long workId);

    @Query("SELECT * FROM pauseTimes WHERE workPauseId = :workId")
    List<PauseTimeRecord> getPauseTimesWithWorkId(long workId);

    @Query("SELECT * FROM pauseTimes WHERE pauseId = :id")
    PauseTimeRecord getOne(long id);

    @Query("SELECT * FROM pauseTimes  WHERE pauseEnd < :beginDate ORDER BY pauseEnd DESC LIMIT 1")
    PauseTimeRecord getOneBefore(String beginDate);

    @Query("SELECT * FROM pauseTimes WHERE pauseBegin > :endDate ORDER BY pauseBegin ASC LIMIT 1")
    PauseTimeRecord getOneAfter(String endDate);
}
