package com.example.patryk.work_time_app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.GregorianCalendar;

@Entity(tableName = "workTimes")
public class WorkTime {

    @ColumnInfo(name = "workId")
    @PrimaryKey(autoGenerate = true)
    private long mId;

    @ColumnInfo(name = "shiftBegin")
    @NonNull
    private String mShiftBegin;

    @ColumnInfo(name = "shiftEnd")
    private String mShiftEnd;

    @ColumnInfo(name = "workTime")
    private long mWorkTime;

    @ColumnInfo(name = "finished")
    private boolean mFinished;

    public WorkTime(@NonNull String shiftBegin, boolean finished) {
        this.mShiftBegin = shiftBegin;
        this.mFinished = finished;
    }

    public long getId() {
        return this.mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getShiftBegin() {
        return this.mShiftBegin;
    }

    public String getShiftEnd() {
        return this.mShiftEnd;
    }

    public void setShiftEnd(String ShiftEnd) {
        this.mShiftEnd = ShiftEnd;
    }

    public long getWorkTime() {
        return this.mWorkTime;
    }

    public void setWorkTime(long WorkTime) {
        this.mWorkTime = WorkTime;
    }

    public boolean isFinished() {
        return this.mFinished;
    }

    public void setFinished(boolean Finished) {
        this.mFinished = Finished;
    }

    public String toString() {
        return "Id: " + this.mId + " | Begin: " + this.mShiftBegin + " | End: " + this.mShiftEnd
                + " | Time: " + this.mWorkTime + " | Finished?: " + this.mFinished;
    }

}
