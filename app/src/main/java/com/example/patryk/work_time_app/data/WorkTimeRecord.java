package com.example.patryk.work_time_app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.patryk.work_time_app.Support;

import java.util.Calendar;
import java.util.Locale;

@Entity(tableName = "workTimes")
public class WorkTimeRecord {

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

    public WorkTimeRecord() {
        this.mShiftBegin = Calendar.getInstance(Locale.getDefault());
        this.mFinished = false;
    }

    @Ignore
    public WorkTimeRecord(@NonNull Calendar mShiftBegin, Calendar mShiftEnd, long mWorkTime, boolean mFinished) {
        this.mShiftBegin = mShiftBegin;
        this.mShiftEnd = mShiftEnd;
        this.mWorkTime = mWorkTime;
        this.mFinished = mFinished;
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

    public void makeShiftEndTimestamp() {
        this.mShiftEnd = Calendar.getInstance(Locale.getDefault());
        this.mWorkTime = Support.calculateDifference(this.mShiftBegin.getTimeInMillis(), this.mShiftEnd.getTimeInMillis());
    }

    public String getShiftBeginText() {
        return Support.convertDateToString(mShiftBegin.getTime());
    }

    public String getShiftEndText() {
        return Support.convertDateToString(mShiftEnd.getTime());
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
