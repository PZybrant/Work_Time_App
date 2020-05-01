package com.example.patryk.work_time_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;

import java.util.ArrayList;

public class TimerFragmentOverviewAdapter extends RecyclerView.Adapter<TimerFragmentOverviewAdapter.MyViewHolder> {

    private ArrayList<String> stringArrayList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_text_view, parent, false);
        return new MyViewHolder(view);
    }

    public TimerFragmentOverviewAdapter(ArrayList<String> list) {
        stringArrayList = list;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(stringArrayList.get(position));
    }

    public void notifyRecyclerview() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }
}
