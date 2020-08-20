package com.example.patryk.work_time_app.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class WorkAndPauseTime {
    @Embedded
    public WorkTimeRecord workTimeRecord;

    @Relation(
            parentColumn = "workId",
            entityColumn = "workPauseId"
    )
    public List<PauseTimeRecord> pauseTimeRecords;

}
