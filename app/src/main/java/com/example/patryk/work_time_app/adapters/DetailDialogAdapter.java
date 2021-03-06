package com.example.patryk.work_time_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.data.PauseTimeRecord;

import java.util.List;

public class DetailDialogAdapter extends RecyclerView.Adapter<DetailDialogAdapter.MyViewHolder> {

    private List<PauseTimeRecord> mPauseTimeRecords;

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_fragment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String concat = Support.convertDateToString(mPauseTimeRecords.get(position).getPauseBegin().getTime()) + " > " + Support.convertDateToString(mPauseTimeRecords.get(position).getPauseEnd().getTime());
        holder.textView.setText(concat);
    }

    @Override
    public int getItemCount() {
        if (mPauseTimeRecords != null) {
            return mPauseTimeRecords.size();
        } else {
            return 0;
        }
    }

    public void setList(List<PauseTimeRecord> pauseTimeRecords) {
        mPauseTimeRecords = pauseTimeRecords;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_timer_tv_registry);
        }
    }
}
