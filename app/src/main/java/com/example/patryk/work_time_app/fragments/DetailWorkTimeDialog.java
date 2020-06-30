package com.example.patryk.work_time_app.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.adapters.DetailDialogAdapter;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class DetailWorkTimeDialog extends DialogFragment {

    private WorkTime mWorkTime;
    private RecyclerView recyclerView;
    private TextView workTimeBeginTextView, workTimeEndTextView, totalTimeTextView;
    private Button closeButton;
    private HistoryFragmentViewModel mViewModel;
    private ArrayList<String> strings;
    private List<PauseTime> mPauseTimeListl;

    public DetailWorkTimeDialog(HistoryFragmentViewModel viewModel, WorkTime workTime) {
        this.mViewModel = viewModel;
        this.mWorkTime = workTime;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_detail, container, false);

        workTimeBeginTextView = view.findViewById(R.id.dialog_detail_tv_shift_begin);
        workTimeEndTextView = view.findViewById(R.id.dialog_detail_tv_shift_end);
        totalTimeTextView = view.findViewById(R.id.dialog_detail_tv_total);
        closeButton = view.findViewById(R.id.dialog_detail_button_close);
        closeButton.setOnClickListener(view1 -> dismiss());

        recyclerView = view.findViewById(R.id.dialog_detail_rv);
        DetailDialogAdapter adapter = new DetailDialogAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        mViewModel.getPauseTimesWithWorkIdLiveData(mWorkTime.getId()).observe(getViewLifecycleOwner(), new Observer<List<PauseTime>>() {
            @Override
            public void onChanged(List<PauseTime> pauseTimes) {
                adapter.setList(pauseTimes);
            }
        });

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                PauseTime tempPauseTime = adapter.getSwipedPauseTime(viewHolder.getAdapterPosition());
//                if(mWorkTime.isFinished()){
//                    long totalPauseTime = tempPauseTime.getPauseTime();
//                    int numberOfRowDeleted = mViewModel.deletePauseTime(tempPauseTime);
//                    if(numberOfRowDeleted > 0) {
//                        long newTotalWorkTime = mWorkTime.getWorkTime() + totalPauseTime;
//                        totalTimeTextView.setText(Support.convertToString(newTotalWorkTime));
//                        mWorkTime.setWorkTime(newTotalWorkTime);
//                        mViewModel.updateWorkTime(mWorkTime);
//                        Toast.makeText(getContext(), "Delete successful", Toast.LENGTH_SHORT).show();
//                    }
//                } else{
//                    Toast.makeText(getContext(), "Cannot delete until is not finished", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).attachToRecyclerView(recyclerView);

        workTimeBeginTextView.setText(Support.convertToString(mWorkTime.getShiftBegin().getTime()));
        workTimeEndTextView.setText(Support.convertToString(mWorkTime.getShiftEnd().getTime()));
        totalTimeTextView.setText(Support.convertToString(mWorkTime.getWorkTime()));
        return view;
    }


}
