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
    long insertPauseTime(PauseTime pauseTime);

    @Update
    int updatePauseTime(PauseTime pauseTime);

    @Delete
    int deletePauseTime(PauseTime pauseTime);

    @Query("DELETE FROM pauseTimes WHERE workPauseId = :workId")
    int deletePauseTimesWithWorkId(long workId);

    @Query("SELECT * FROM pauseTimes")
    LiveData<List<PauseTime>> getAll();

    @Query("SELECT * FROM pauseTimes WHERE workPauseId = :workId ORDER BY pauseBegin ASC")
    LiveData<List<PauseTime>> getPauseTimesWithWorkIdLiveData(long workId);

    @Query("SELECT * FROM pauseTimes WHERE workPauseId = :workId")
    List<PauseTime> getPauseTimesWithWorkId(long workId);

    @Query("SELECT * FROM pauseTimes WHERE pauseId = :id")
    PauseTime getOne(long id);

    @Query("SELECT * FROM pauseTimes  WHERE pauseEnd < :beginDate ORDER BY pauseEnd DESC LIMIT 1")
    PauseTime getOneBefore(String beginDate);

    @Query("SELECT * FROM pauseTimes WHERE pauseBegin > :endDate ORDER BY pauseBegin ASC LIMIT 1")
    PauseTime getOneAfter(String endDate);
}
