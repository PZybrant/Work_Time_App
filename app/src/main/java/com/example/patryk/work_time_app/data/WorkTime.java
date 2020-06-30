package com.example.patryk.work_time_app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity(tableName = "workTimes")
public class WorkTime {

    @ColumnInfo(name = "workId")
    @PrimaryKey(autoGenerate = true)
    private long mId;

    @ColumnInfo(name = "shiftBegin")
    @NonNull
    private Calendar mShiftBegin;

    @ColumnInfo(name = "shiftEnd")
    private Calendar mShiftEnd;

    @ColumnInfo(name = "workTime")
    private long mWorkTime;

    @ColumnInfo(name = "finished")
    private boolean mFinished;

    public WorkTime(@NonNull Calendar shiftBegin, boolean finished) {
        this.mShiftBegin = shiftBegin;
        this.mFinished = finished;
    }

    public long getId() {
        return this.mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public Calendar getShiftBegin() {
        return this.mShiftBegin;
    }

    public Calendar getShiftEnd() {
        return this.mShiftEnd;
    }

    public void setShiftBegin(Calendar shiftBegin) {
        this.mShiftBegin = shiftBegin;
    }

    public void setShiftEnd(Calendar ShiftEnd) {
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
