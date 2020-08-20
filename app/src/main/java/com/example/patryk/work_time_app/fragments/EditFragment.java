package com.example.patryk.work_time_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.adapters.EditAdapter;
import com.example.patryk.work_time_app.data.PauseTimeRecord;
import com.example.patryk.work_time_app.data.WorkTimeRecord;
import com.example.patryk.work_time_app.viewmodels.EditFragmentViewModel;

import java.util.Calendar;
import java.util.List;

public class EditFragment extends Fragment {

    private WorkTimeRecord workTimeRecord;
    private TextView totalTimeTextView;
    private EditDialog editDialogFragment;

    private EditFragmentViewModel editFragmentViewModel;
    private List<PauseTimeRecord> pauseTimeRecordList;
    ;
    private TextView textViewShiftBeginDate, textViewShiftBeginTime, textViewShiftEndDate, textViewShiftEndTime;

    public EditFragment(WorkTimeRecord workTimeRecord) {
        this.workTimeRecord = workTimeRecord;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editFragmentViewModel = new ViewModelProvider(this).get(EditFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        View viewShiftBegin = view.findViewById(R.id.fragment_edit_view_shift_begin);
        textViewShiftBeginDate = view.findViewById(R.id.fragment_edit_tv_shift_begin_date);
        textViewShiftBeginTime = view.findViewById(R.id.fragment_edit_tv_shift_begin_time);
        View viewShiftEnd = view.findViewById(R.id.fragment_edit_view_shift_end);
        textViewShiftEndDate = view.findViewById(R.id.fragment_edit_tv_shift_end_date);
        textViewShiftEndTime = view.findViewById(R.id.fragment_edti_tv_shift_end_time);
        Button buttonAdd = view.findViewById(R.id.fragment_edit_ib_add);
        Button buttonBack = view.findViewById(R.id.fragment_edit_ib_back);
        Button buttonDelete = view.findViewById(R.id.fragment_edit_ib_delete);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_edit_rv);
        totalTimeTextView = view.findViewById(R.id.fragment_edit_tv_total);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EditAdapter adapter = new EditAdapter(adapterListener);
        recyclerView.setAdapter(adapter);

        textViewShiftBeginDate.setText(Support.makeDateText(workTimeRecord.getShiftBegin()));
        textViewShiftBeginTime.setText(Support.makeTimeText(workTimeRecord.getShiftBegin()));
        textViewShiftEndDate.setText(Support.makeDateText(workTimeRecord.getShiftEnd()));
        textViewShiftEndTime.setText(Support.makeTimeText(workTimeRecord.getShiftEnd()));

        totalTimeTextView.setText(Support.convertTimeToString(workTimeRecord.getWorkTime()));

        viewShiftBegin.setOnClickListener(v -> {
            editDialogFragment = new EditDialog(listener, editFragmentViewModel, workTimeRecord, 0);
            editDialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
            view.clearFocus();
        });

        viewShiftEnd.setOnClickListener(v -> {
            editDialogFragment = new EditDialog(listener, editFragmentViewModel, workTimeRecord, 1);
            editDialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
            view.clearFocus();
        });

        buttonAdd.setOnClickListener(v -> {
            long differenceBetweenRecords;
            if (pauseTimeRecordList == null || pauseTimeRecordList.size() == 0) {
                differenceBetweenRecords = Support.calculateDifference(workTimeRecord.getShiftBegin().getTimeInMillis(), workTimeRecord.getShiftEnd().getTimeInMillis());
            } else {
                differenceBetweenRecords = Support.calculateDifference(workTimeRecord.getShiftBegin().getTimeInMillis(), pauseTimeRecordList.get(0).getPauseBegin().getTimeInMillis());
            }
            differenceBetweenRecords = (differenceBetweenRecords / 1000) / 60;
            System.out.println(differenceBetweenRecords);
            if (differenceBetweenRecords >= 5) {
                differenceBetweenRecords /= 2;
                Calendar newPauseBeginTime = Calendar.getInstance();
                newPauseBeginTime.setTimeInMillis(workTimeRecord.getShiftBegin().getTimeInMillis());
                newPauseBeginTime.add(Calendar.MINUTE, (int) differenceBetweenRecords);
                Calendar newPauseEndTime = Calendar.getInstance();
                newPauseEndTime.setTimeInMillis(newPauseBeginTime.getTimeInMillis());
                newPauseEndTime.add(Calendar.MINUTE, 1);
                long timeDifference = Support.calculateDifference(newPauseBeginTime.getTimeInMillis(), newPauseEndTime.getTimeInMillis());
                long totalTime = workTimeRecord.getWorkTime();
                totalTime -= timeDifference;
                workTimeRecord.setWorkTime(totalTime);
                editFragmentViewModel.updateWorkTime(workTimeRecord);
                totalTimeTextView.setText(Support.convertTimeToString(workTimeRecord.getWorkTime()));
                PauseTimeRecord newPauseTimeRecord = new PauseTimeRecord(workTimeRecord.getId(), newPauseBeginTime, newPauseEndTime, timeDifference, true);
                long rowId = editFragmentViewModel.insertPauseTime(newPauseTimeRecord);
                if (rowId > 0) {
                    Toast.makeText(getContext(), "Insertion confirmed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Not enough time between", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBack.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        buttonDelete.setOnClickListener(v -> {
            int numberOfRowsDeleted = editFragmentViewModel.deleteWorkTime(workTimeRecord);
            if (numberOfRowsDeleted > 0) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        editFragmentViewModel.getPauseTimesWithWorkIdLiveData(workTimeRecord.getId()).observe(getViewLifecycleOwner(), pauseTimes -> {
            pauseTimeRecordList = pauseTimes;
            adapter.setList(pauseTimes);
        });

        return view;
    }

    private EditAdapter.EditFragmentAdapterListener adapterListener = new EditAdapter.EditFragmentAdapterListener() {
        @Override
        public void onAddButtonClick(PauseTimeRecord pauseTimeRecord, int pos) {
            long differenceBetweenRecords;
            if (pauseTimeRecordList.size() == 1 || pos == pauseTimeRecordList.size() - 1) {
                differenceBetweenRecords = Support.calculateDifference(pauseTimeRecord.getPauseEnd().getTimeInMillis(), workTimeRecord.getShiftEnd().getTimeInMillis());
            } else {
                differenceBetweenRecords = Support.calculateDifference(pauseTimeRecord.getPauseEnd().getTimeInMillis(), pauseTimeRecordList.get(pos + 1).getPauseBegin().getTimeInMillis());
            }
            differenceBetweenRecords = (differenceBetweenRecords / 1000) / 60;
            if (differenceBetweenRecords >= 5) {
                differenceBetweenRecords /= 2;
                Calendar newPauseTimeBegin = Calendar.getInstance();
                newPauseTimeBegin.setTimeInMillis(pauseTimeRecord.getPauseEnd().getTimeInMillis());
                newPauseTimeBegin.add(Calendar.MINUTE, (int) differenceBetweenRecords);
                Calendar newPauseTimeEnd = Calendar.getInstance();
                newPauseTimeEnd.setTimeInMillis(newPauseTimeBegin.getTimeInMillis());
                newPauseTimeEnd.add(Calendar.MINUTE, 1);
                long timeDifference = Support.calculateDifference(newPauseTimeBegin.getTimeInMillis(), newPauseTimeEnd.getTimeInMillis());
                long totalTime = workTimeRecord.getWorkTime();
                totalTime -= timeDifference;
                workTimeRecord.setWorkTime(totalTime);
                editFragmentViewModel.updateWorkTime(workTimeRecord);
                PauseTimeRecord newPauseTimeRecord = new PauseTimeRecord(workTimeRecord.getId(), newPauseTimeBegin, newPauseTimeEnd, timeDifference, true);
                totalTimeTextView.setText(Support.convertTimeToString(workTimeRecord.getWorkTime()));
                long rowId = editFragmentViewModel.insertPauseTime(newPauseTimeRecord);
                if (rowId > 0) {
                    Toast.makeText(getContext(), "Insertion confirmed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Not enough time between", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDeleteButtonClick(PauseTimeRecord pauseTimeRecord) {
            editFragmentViewModel.deletePauseTime(pauseTimeRecord);
            long totalTime = workTimeRecord.getWorkTime();
            totalTime += pauseTimeRecord.getPauseTime();
            workTimeRecord.setWorkTime(totalTime);
            editFragmentViewModel.updateWorkTime(workTimeRecord);
            totalTimeTextView.setText(Support.convertTimeToString(workTimeRecord.getWorkTime()));
        }

        @Override
        public void onEditViewClick(View view, PauseTimeRecord pauseTimeRecord, int type) {
            editDialogFragment = new EditDialog(listener, editFragmentViewModel, workTimeRecord, pauseTimeRecord, type);
            editDialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
            view.clearFocus();
        }
    };

    private EditDialog.OnApplyButtonClickListener listener = new EditDialog.OnApplyButtonClickListener() {
        @Override
        public void onClick(PauseTimeRecord pauseTimeRecord, int type) {
            if (pauseTimeRecord != null) {
                long diff = Support.calculateDifference(pauseTimeRecord.getPauseBegin().getTimeInMillis(), pauseTimeRecord.getPauseEnd().getTimeInMillis());
                pauseTimeRecord.setPauseTime(diff);
                editFragmentViewModel.updatePauseTime(pauseTimeRecord);
            } else {
                if (type == 0) {
                    textViewShiftBeginDate.setText(Support.makeDateText(workTimeRecord.getShiftBegin()));
                    textViewShiftBeginTime.setText(Support.makeTimeText(workTimeRecord.getShiftBegin()));
                } else if (type == 1) {
                    textViewShiftEndDate.setText(Support.makeDateText(workTimeRecord.getShiftEnd()));
                    textViewShiftEndTime.setText(Support.makeTimeText(workTimeRecord.getShiftEnd()));
                }
            }
            long l = editFragmentViewModel.recalculateWorkTime(workTimeRecord);
            workTimeRecord.setWorkTime(l);
            totalTimeTextView.setText(Support.convertTimeToString(workTimeRecord.getWorkTime()));
            editFragmentViewModel.updateWorkTime(workTimeRecord);
            editDialogFragment.dismiss();
        }
    };
}
