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
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    public interface HistoryAdapterListener {
        void onLongPressListener(WorkTime mWorkTime);
    }

    private HistoryAdapterListener mListener;

    private List<WorkTime> timeList;
    private Context mContext;

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
            holder.startDateTextView.setText(Support.convertDateToString(timeList.get(position).getShiftBegin().getTime()));
            if (timeList.get(position).isFinished()) {
                holder.stopDateTextView.setText(Support.convertDateToString(timeList.get(position).getShiftEnd().getTime()));
                long timeInMilis = timeList.get(position).getWorkTime();
                String timeInMilisString = (timeInMilis < 1000) ? "" : Support.convertTimeToString(timeInMilis);
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

    public void setTimes(List<WorkTime> times) {
        this.timeList = times;
        notifyDataSetChanged();
    }

    public WorkTime getSwipedTime(int pos) {
        return timeList.get(pos);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private WorkTime mWorkTime;

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

        private void setRowID(WorkTime workTime) {
            this.mWorkTime = workTime;
        }

        private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public void onLongPress(MotionEvent event) {
                mListener.onLongPressListener(mWorkTime);
            }
        }
    }
}
