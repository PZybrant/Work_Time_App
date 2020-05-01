package com.example.patryk.work_time_app.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class WorkAndPauseTime {
    @Embedded
    public WorkTime workTime;

    @Relation(
            parentColumn = "workId",
            entityColumn = "workPauseId"
    )
    public List<PauseTime> pauseTimes;

}
