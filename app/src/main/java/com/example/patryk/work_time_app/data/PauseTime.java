package com.example.patryk.work_time_app.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.patryk.work_time_app.Support;

import java.util.Calendar;
import java.util.Locale;

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

    public PauseTime(long workId) {
        this.mWorkId = workId;
        this.mPauseBegin = Calendar.getInstance(Locale.getDefault());
        this.mFinished = false;
    }

    @Ignore
    public PauseTime(long mWorkId, @NonNull Calendar mPauseBegin, Calendar mPauseEnd, long mPauseTime, boolean mFinished) {
        this.mWorkId = mWorkId;
        this.mPauseBegin = mPauseBegin;
        this.mPauseEnd = mPauseEnd;
        this.mPauseTime = mPauseTime;
        this.mFinished = mFinished;
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

    public void makePauseEndTimestamp() {
        this.mPauseEnd = Calendar.getInstance(Locale.getDefault());
        this.mPauseTime = Support.calculateDifference(this.mPauseBegin.getTimeInMillis(), this.mPauseEnd.getTimeInMillis());
    }

    public String getPauseBeginText() {
        return Support.convertDateToString(mPauseBegin.getTime());
    }

    public String getPauseEndText() {
        return Support.convertDateToString(mPauseEnd.getTime());
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
