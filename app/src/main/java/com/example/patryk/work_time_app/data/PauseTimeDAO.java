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

    @Query("SELECT * FROM pauseTimes")
    LiveData<List<PauseTime>> getAll();

    @Query("SELECT * FROM pauseTimes WHERE workPauseId = :workId")
    List<PauseTime> getPauseTimesWithSpecifiedWorkId(long workId);

    @Query("SELECT * FROM pauseTimes WHERE pauseId = :id")
    PauseTime getOne(long id);
}
