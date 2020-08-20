package com.example.patryk.work_time_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.data.PauseTimeRecord;

import java.util.List;

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.MyViewHolder> {

    public interface EditFragmentAdapterListener {
        void onAddButtonClick(PauseTimeRecord pauseTimeRecord, int pos);

        void onDeleteButtonClick(PauseTimeRecord pauseTimeRecord);

        void onEditViewClick(View view, PauseTimeRecord pauseTimeRecord, int type);
    }

    private List<PauseTimeRecord> pauseTimeRecordList;
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
        holder.setPauseTimeId(pauseTimeRecordList.get(position));
        holder.setPosition(position);
        holder.textViewBeginPauseDate.setText(Support.makeDateText(pauseTimeRecordList.get(position).getPauseBegin()));
        holder.textViewBeginPauseTime.setText(Support.makeTimeText(pauseTimeRecordList.get(position).getPauseBegin()));
        holder.textViewEndPauseDate.setText(Support.makeDateText(pauseTimeRecordList.get(position).getPauseEnd()));
        holder.textViewEndPauseTime.setText(Support.makeTimeText(pauseTimeRecordList.get(position).getPauseEnd()));
        holder.textViewTotal.setText(Support.convertTimeToString(pauseTimeRecordList.get(position).getPauseTime()));
    }

    @Override
    public int getItemCount() {
        if (pauseTimeRecordList != null) {
            return pauseTimeRecordList.size();
        } else {
            return 0;
        }
    }

    public void setList(List<PauseTimeRecord> list) {
        this.pauseTimeRecordList = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private int position;
        private TextView textViewBeginPauseDate, textViewBeginPauseTime, textViewEndPauseDate, textViewEndPauseTime, textViewTotal;
        private PauseTimeRecord pauseTimeRecord;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            View viewBeginPauseDateTime = itemView.findViewById(R.id.row_edit_view_pause_begin);
            View viewEndPauseDateTime = itemView.findViewById(R.id.row_edit_view_pause_end);
            Button buttonDelete = itemView.findViewById(R.id.row_edit_ib_delete);
            Button buttonAdd = itemView.findViewById(R.id.row_edit_ib_add);
            textViewBeginPauseDate = itemView.findViewById(R.id.row_edit_view_pause_begin_date);
            textViewBeginPauseTime = itemView.findViewById(R.id.row_edit_view_pause_begin_time);
            textViewEndPauseDate = itemView.findViewById(R.id.row_edit_view_pause_end_date);
            textViewEndPauseTime = itemView.findViewById(R.id.row_edit_view_pause_end_time);
            textViewTotal = itemView.findViewById(R.id.row_edit_tv_total);

            viewBeginPauseDateTime.setOnClickListener(v -> listener.onEditViewClick(v, pauseTimeRecord, 0));
            viewEndPauseDateTime.setOnClickListener(v -> listener.onEditViewClick(v, pauseTimeRecord, 1));

            buttonDelete.setOnClickListener(view -> listener.onDeleteButtonClick(pauseTimeRecord));
            buttonAdd.setOnClickListener(view -> listener.onAddButtonClick(pauseTimeRecord, position));
        }

        void setPauseTimeId(PauseTimeRecord pauseTimeRecord) {
            this.pauseTimeRecord = pauseTimeRecord;
        }

        void setPosition(int pos) {
            this.position = pos;
        }
    }
}
