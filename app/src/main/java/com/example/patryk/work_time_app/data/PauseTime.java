package com.example.patryk.work_time_app.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Calendar;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "pauseTimes")
public class PauseTime {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pauseId")
    private long mId;

    @ForeignKey(entity = WorkTime.class, parentColumns = "workId", childColumns = "workPauseId", onDelete = CASCADE, onUpdate = CASCADE)
    @ColumnInfo(name = "workPauseId")
    private long mWorkId;

    @NonNull
    @ColumnInfo(name = "pauseBegin")
    private Calendar mPauseBegin;

    @ColumnInfo(name = "pauseEnd")
    private Calendar mPauseEnd;

    @ColumnInfo(name = "pauseTime")
    private long mPauseTime;

    @ColumnInfo(name = "finished")
    private boolean mFinished;

    public PauseTime(long workId, @NonNull Calendar pauseBegin, boolean finished) {
        this.mWorkId = workId;
        this.mPauseBegin = pauseBegin;
        this.mFinished = finished;

    }

    public long getId() {
        return this.mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public long getWorkId() {
        return this.mWorkId;
    }

    public void setWorkId(long id) {
        this.mWorkId = id;
    }

    public Calendar getPauseBegin() {
        return this.mPauseBegin;
    }

    public Calendar getPauseEnd() {
        return this.mPauseEnd;
    }

    public void setPauseBegin(Calendar pauseBegin) {
        this.mPauseBegin = pauseBegin;
    }

    public void setPauseEnd(Calendar pauseEnd) {
        this.mPauseEnd = pauseEnd;
    }

    public long getPauseTime() {
        return this.mPauseTime;
    }

    public void setPauseTime(long pauseTime) {
        this.mPauseTime = pauseTime;
    }

    public boolean isFinished() {
        return this.mFinished;
    }

    public void setFinished(boolean Finished) {
        this.mFinished = Finished;
    }

    public String toString() {
        return "Id: " + this.mId + " | Work Id: " + mWorkId + " | Begin: " + this.mPauseBegin + " | End: " + this.mPauseEnd
                + " | Time: " + this.mPauseTime + " | Finished?: " + this.mFinished;
    }


}
