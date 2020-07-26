package com.example.patryk.work_time_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;

import java.util.ArrayList;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.MyViewHolder> {

    private ArrayList<String> recordList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_timer_tv_registry);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer_fragment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(recordList.get(position));
    }

    public void setRecordList(ArrayList<String> list) {
        recordList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (recordList != null) {
            return recordList.size();
        }
        return 0;
    }
}
