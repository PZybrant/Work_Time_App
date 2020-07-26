package com.example.patryk.work_time_app.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.adapters.DetailDialogAdapter;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;

import java.util.List;


public class DetailWorkTimeDialog extends DialogFragment {

    private WorkTime workTime;
    private HistoryFragmentViewModel viewModel;

    public DetailWorkTimeDialog(HistoryFragmentViewModel viewModel, WorkTime workTime) {
        this.viewModel = viewModel;
        this.workTime = workTime;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_detail, container, false);

        TextView workTimeBeginTextView = view.findViewById(R.id.dialog_detail_tv_shift_begin);
        TextView workTimeEndTextView = view.findViewById(R.id.dialog_detail_tv_shift_end);
        TextView totalTimeTextView = view.findViewById(R.id.dialog_detail_tv_total);
        Button closeButton = view.findViewById(R.id.dialog_detail_button_close);
        closeButton.setOnClickListener(view1 -> dismiss());

        RecyclerView recyclerView = view.findViewById(R.id.dialog_detail_rv);
        DetailDialogAdapter adapter = new DetailDialogAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        List<PauseTime> pauseTimesWithWorkId = viewModel.getPauseTimeRecordListWithWorkId(workTime.getId());
        adapter.setList(pauseTimesWithWorkId);

        workTimeBeginTextView.setText(Support.convertDateToString(workTime.getShiftBegin().getTime()));
        if (workTime.isFinished()) {
            workTimeEndTextView.setText(Support.convertDateToString(workTime.getShiftEnd().getTime()));
            totalTimeTextView.setText(Support.convertTimeToString(workTime.getWorkTime()));
        }
        return view;
    }


}
