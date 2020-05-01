package com.example.patryk.work_time_app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    public interface HistoryAdapterListener {
        void onLongPressListener();
    }

    private HistoryAdapterListener listener;

    private final LayoutInflater mInflater;
    private long id;
    private List<WorkTime> timeList;
    private Context mContext;
    private HistoryFragmentViewModel mViewModel;

    public HistoryAdapter(Context context, HistoryFragmentViewModel viewModel) {
        this.mViewModel = viewModel;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);

        try {
            listener = (HistoryAdapterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("No HistoryAdapterListener found!");
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_fragment_text_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (timeList != null) {
            holder.setRowID(timeList.get(position).getId());
            holder.startDateTextView.setText(timeList.get(position).getShiftBegin());
            holder.stopDateTextView.setText(timeList.get(position).getShiftEnd());

            long timeInMilis = timeList.get(position).getWorkTime();
            String s = (timeInMilis < 1000) ? "" : convertToString(timeInMilis);
            holder.differenceTextView.setText(s);
        }
    }

    private String convertToString(long timeInMilis) {
        long seconds = timeInMilis / 1000;

        long minutes = seconds / 60;
        seconds = seconds % 60;

        long hours = minutes / 60;
        minutes = minutes % 60;

        String s = (seconds < 10) ? ("0" + seconds) : String.valueOf(seconds);
        String m = (minutes < 10) ? ("0" + minutes) : String.valueOf(minutes);
        String h = (hours < 10) ? ("0" + hours) : String.valueOf(hours);

        return String.format("%s:%s:%s", h, m, s);
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

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private long workId;

        public View view;
        private TextView startDateTextView;
        private TextView stopDateTextView;
        private TextView differenceTextView;

        private GestureDetectorCompat mDetector;

        private MyViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            startDateTextView = view.findViewById(R.id.start_date_text_view);
            stopDateTextView = view.findViewById(R.id.stop_date_text_view);
            differenceTextView = view.findViewById(R.id.difference_text_view);


            mDetector = new GestureDetectorCompat(mContext, new MyGestureListener());

            itemView.setOnTouchListener((view, motionEvent) -> {
                view.performClick();
                return mDetector.onTouchEvent(motionEvent);
            });
        }

        private void setRowID(long id) {
            this.workId = id;
        }

        class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public void onLongPress(MotionEvent event) {
                listener.onLongPressListener();
                itemView.setBackgroundColor(Color.LTGRAY);
            }
        }
    }
}
