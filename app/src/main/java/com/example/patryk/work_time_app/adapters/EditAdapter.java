package com.example.patryk.work_time_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.data.PauseTime;

import java.util.List;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.MyViewHolder> {

    public interface EditFragmentAdapterListener {
        void onAddButtonClick(PauseTime pauseTime, int pos);

        void onDeleteButtonClick(PauseTime pauseTime);

        void onEditViewClick(View view, PauseTime pauseTime, int type);
    }

    private List<PauseTime> pauseTimeList;
    private EditFragmentAdapterListener listener;

    public EditAdapter(EditFragmentAdapterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_fragment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setPauseTimeId(pauseTimeList.get(position));
        holder.setPosition(position);
        holder.textViewBeginPauseDate.setText(Support.makeDateText(pauseTimeList.get(position).getPauseBegin()));
        holder.textViewBeginPauseTime.setText(Support.makeTimeText(pauseTimeList.get(position).getPauseBegin()));
        holder.textViewEndPauseDate.setText(Support.makeDateText(pauseTimeList.get(position).getPauseEnd()));
        holder.textViewEndPauseTime.setText(Support.makeTimeText(pauseTimeList.get(position).getPauseEnd()));
        holder.textViewTotal.setText(Support.convertTimeToString(pauseTimeList.get(position).getPauseTime()));
    }

    @Override
    public int getItemCount() {
        if (pauseTimeList != null) {
            return pauseTimeList.size();
        } else {
            return 0;
        }
    }

    public void setList(List<PauseTime> list) {
        this.pauseTimeList = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private int position;
        private TextView textViewBeginPauseDate, textViewBeginPauseTime, textViewEndPauseDate, textViewEndPauseTime, textViewTotal;
        private PauseTime pauseTime;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            View viewBeginPauseDateTime = itemView.findViewById(R.id.row_edit_view_pause_begin);
            View viewEndPauseDateTime = itemView.findViewById(R.id.row_edit_view_pause_end);
            ImageButton buttonDelete = itemView.findViewById(R.id.row_edit_ib_delete);
            ImageButton buttonAdd = itemView.findViewById(R.id.row_edit_ib_add);
            textViewBeginPauseDate = itemView.findViewById(R.id.row_edit_view_pause_begin_date);
            textViewBeginPauseTime = itemView.findViewById(R.id.row_edit_view_pause_begin_time);
            textViewEndPauseDate = itemView.findViewById(R.id.row_edit_view_pause_end_date);
            textViewEndPauseTime = itemView.findViewById(R.id.row_edit_view_pause_end_time);
            textViewTotal = itemView.findViewById(R.id.row_edit_tv_total);

            viewBeginPauseDateTime.setOnClickListener(v -> listener.onEditViewClick(v, pauseTime, 0));
            viewEndPauseDateTime.setOnClickListener(v -> listener.onEditViewClick(v, pauseTime, 1));

            buttonDelete.setOnClickListener(view -> listener.onDeleteButtonClick(pauseTime));
            buttonAdd.setOnClickListener(view -> listener.onAddButtonClick(pauseTime, position));
        }

        void setPauseTimeId(PauseTime pauseTime) {
            this.pauseTime = pauseTime;
        }

        void setPosition(int pos) {
            this.position = pos;
        }
    }
}
