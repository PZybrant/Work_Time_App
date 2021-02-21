package com.example.patryk.work_time_app.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.data.WorkTimeRecord;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    public interface HistoryAdapterListener {
        void onLongPressListener(WorkTimeRecord mWorkTimeRecord);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private HistoryAdapterListener mListener;

    private List<WorkTimeRecord> timeList;
    private int[] headersMap;
    private Context mContext;
    private Calendar separatorDate;
    int x = 1;

    public HistoryAdapter(Context context, HistoryAdapterListener listener) {
        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_fragment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (timeList != null) {
            holder.setRowID(timeList.get(position));
            holder.startDateTextView.setText(Support.makeTimeText(timeList.get(position).getShiftBegin()));
            if (timeList.get(position).isFinished()) {
                holder.stopDateTextView.setText(Support.makeTimeText(timeList.get(position).getShiftEnd()));
                long timeInMilis = timeList.get(position).getWorkTime();
                String timeInMilisString = (timeInMilis < 1000) ? "00:00" : Support.convertTimeToString(timeInMilis);
                holder.differenceTextView.setText(timeInMilisString);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (timeList != null) {
            return timeList.size();
        } else {
            return 0;
        }
    }

    public void setTimes(List<WorkTimeRecord> times, int[] headersMap) {
        this.timeList = times;
        this.headersMap = headersMap;
        notifyDataSetChanged();
    }

    public int isHeader(int pos) {
        return headersMap[pos];
    }

    public WorkTimeRecord getSwipedTime(int pos) {
        return timeList.get(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private WorkTimeRecord mWorkTimeRecord;

        private TextView startDateTextView;
        private TextView stopDateTextView;
        private TextView differenceTextView;

        private GestureDetectorCompat mDetector;

        private MyViewHolder(View itemView) {
            super(itemView);
            startDateTextView = itemView.findViewById(R.id.item_history_tv_shift_begin);
            stopDateTextView = itemView.findViewById(R.id.item_history_tv_shift_end);
            differenceTextView = itemView.findViewById(R.id.item_history_tv_total);

            mDetector = new GestureDetectorCompat(mContext, new MyGestureListener());

            itemView.setOnTouchListener((view, motionEvent) -> {
                view.performClick();
                return mDetector.onTouchEvent(motionEvent);
            });
        }

        public WorkTimeRecord getWorkTime() {
            return this.mWorkTimeRecord;
        }

        private void setRowID(WorkTimeRecord workTimeRecord) {
            this.mWorkTimeRecord = workTimeRecord;
        }

        private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public void onLongPress(MotionEvent event) {
                mListener.onLongPressListener(mWorkTimeRecord);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                System.out.println(x);
                return super.onSingleTapUp(e);
            }
        }
    }
}
