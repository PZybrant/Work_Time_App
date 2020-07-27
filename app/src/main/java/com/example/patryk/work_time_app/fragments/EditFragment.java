package com.example.patryk.work_time_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.viewmodels.EditFragmentViewModel;

import java.util.Calendar;
import java.util.List;

public class EditFragment extends Fragment {

    private WorkTime workTime;
    private TextView totalTimeTextView;
    private EditDialog editDialogFragment;

    private EditFragmentViewModel editFragmentViewModel;
    private List<PauseTime> pauseTimeList;
    ;
    private TextView textViewShiftBeginDate, textViewShiftBeginTime, textViewShiftEndDate, textViewShiftEndTime;

    public EditFragment(WorkTime workTime) {
        this.workTime = workTime;
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
        ImageButton buttonAdd = view.findViewById(R.id.fragment_edit_ib_add);
        ImageButton buttonBack = view.findViewById(R.id.fragment_edit_ib_back);
        ImageButton buttonDelete = view.findViewById(R.id.fragment_edit_ib_delete);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_edit_rv);
        totalTimeTextView = view.findViewById(R.id.fragment_edit_tv_total);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EditAdapter adapter = new EditAdapter(adapterListener);
        recyclerView.setAdapter(adapter);

        textViewShiftBeginDate.setText(Support.makeDateText(workTime.getShiftBegin()));
        textViewShiftBeginTime.setText(Support.makeTimeText(workTime.getShiftBegin()));
        textViewShiftEndDate.setText(Support.makeDateText(workTime.getShiftEnd()));
        textViewShiftEndTime.setText(Support.makeTimeText(workTime.getShiftEnd()));

        totalTimeTextView.setText(Support.convertTimeToString(workTime.getWorkTime()));

        viewShiftBegin.setOnClickListener(v -> {
            editDialogFragment = new EditDialog(listener, editFragmentViewModel, workTime, 0);
            editDialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
            view.clearFocus();
        });

        viewShiftEnd.setOnClickListener(v -> {
            editDialogFragment = new EditDialog(listener, editFragmentViewModel, workTime, 1);
            editDialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
            view.clearFocus();
        });

        buttonAdd.setOnClickListener(v -> {
            long differenceBetweenRecords;
            if (pauseTimeList == null || pauseTimeList.size() == 0) {
                differenceBetweenRecords = Support.calculateDifference(workTime.getShiftBegin().getTimeInMillis(), workTime.getShiftEnd().getTimeInMillis()) / 2;
            } else {
                differenceBetweenRecords = Support.calculateDifference(workTime.getShiftBegin().getTimeInMillis(), pauseTimeList.get(0).getPauseBegin().getTimeInMillis()) / 2;
            }
            differenceBetweenRecords /= 1000;
            if (differenceBetweenRecords >= 5) {
                Calendar newPauseBeginTime = Calendar.getInstance();
                newPauseBeginTime.setTimeInMillis(workTime.getShiftBegin().getTimeInMillis());
                newPauseBeginTime.add(Calendar.SECOND, (int) differenceBetweenRecords - 1);
                Calendar newPauseEndTime = Calendar.getInstance();
                newPauseEndTime.setTimeInMillis(newPauseBeginTime.getTimeInMillis());
                newPauseEndTime.add(Calendar.SECOND, 2);
                long timeDifference = Support.calculateDifference(newPauseBeginTime.getTimeInMillis(), newPauseEndTime.getTimeInMillis());
                long totalTime = workTime.getWorkTime();
                totalTime -= timeDifference;
                workTime.setWorkTime(totalTime);
                editFragmentViewModel.updateWorkTime(workTime);
                totalTimeTextView.setText(Support.convertTimeToString(workTime.getWorkTime()));
                PauseTime newPauseTime = new PauseTime(workTime.getId(), newPauseBeginTime, newPauseEndTime, timeDifference, true);
                long rowId = editFragmentViewModel.insertPauseTime(newPauseTime);
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
            int numberOfRowsDeleted = editFragmentViewModel.deleteWorkTime(workTime);
            if (numberOfRowsDeleted > 0) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        editFragmentViewModel.getPauseTimesWithWorkIdLiveData(workTime.getId()).observe(getViewLifecycleOwner(), pauseTimes -> {
            pauseTimeList = pauseTimes;
            adapter.setList(pauseTimes);
        });

        return view;
    }

    private EditAdapter.EditFragmentAdapterListener adapterListener = new EditAdapter.EditFragmentAdapterListener() {
        @Override
        public void onAddButtonClick(PauseTime pauseTime, int pos) {
            long differenceBetweenRecords;
            if (pauseTimeList.size() == 1 || pos == pauseTimeList.size() - 1) {
                differenceBetweenRecords = Support.calculateDifference(pauseTime.getPauseEnd().getTimeInMillis(), workTime.getShiftEnd().getTimeInMillis()) / 2;
            } else {
                differenceBetweenRecords = Support.calculateDifference(pauseTime.getPauseEnd().getTimeInMillis(), pauseTimeList.get(pos + 1).getPauseBegin().getTimeInMillis()) / 2;
            }
            differenceBetweenRecords /= 1000;
            if (differenceBetweenRecords >= 5) {
                Calendar newPauseTimeBegin = Calendar.getInstance();
                newPauseTimeBegin.setTimeInMillis(pauseTime.getPauseEnd().getTimeInMillis());
                newPauseTimeBegin.add(Calendar.SECOND, (int) differenceBetweenRecords);
                Calendar newPauseTimeEnd = Calendar.getInstance();
                newPauseTimeEnd.setTimeInMillis(newPauseTimeBegin.getTimeInMillis());
                newPauseTimeEnd.add(Calendar.SECOND, 2);
                long timeDifference = Support.calculateDifference(newPauseTimeBegin.getTimeInMillis(), newPauseTimeEnd.getTimeInMillis());
                long totalTime = workTime.getWorkTime();
                totalTime -= timeDifference;
                workTime.setWorkTime(totalTime);
                editFragmentViewModel.updateWorkTime(workTime);
                PauseTime newPauseTime = new PauseTime(workTime.getId(), newPauseTimeBegin, newPauseTimeEnd, timeDifference, true);
                totalTimeTextView.setText(Support.convertTimeToString(workTime.getWorkTime()));
                long rowId = editFragmentViewModel.insertPauseTime(newPauseTime);
                if (rowId > 0) {
                    Toast.makeText(getContext(), "Insertion confirmed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Not enough time between", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDeleteButtonClick(PauseTime pauseTime) {
            editFragmentViewModel.deletePauseTime(pauseTime);
            long totalTime = workTime.getWorkTime();
            totalTime += pauseTime.getPauseTime();
            workTime.setWorkTime(totalTime);
            editFragmentViewModel.updateWorkTime(workTime);
            totalTimeTextView.setText(Support.convertTimeToString(workTime.getWorkTime()));
        }

        @Override
        public void onEditViewClick(View view, PauseTime pauseTime, int type) {
            editDialogFragment = new EditDialog(listener, editFragmentViewModel, workTime, pauseTime, type);
            editDialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
            view.clearFocus();
        }
    };

    private EditDialog.OnApplyButtonClickListener listener = new EditDialog.OnApplyButtonClickListener() {
        @Override
        public void onClick(PauseTime pauseTime, int type) {
            if (pauseTime != null) {
                long diff = Support.calculateDifference(pauseTime.getPauseBegin().getTimeInMillis(), pauseTime.getPauseEnd().getTimeInMillis());
                pauseTime.setPauseTime(diff);
                editFragmentViewModel.updatePauseTime(pauseTime);
            } else {
                if (type == 0) {
                    textViewShiftBeginDate.setText(Support.makeDateText(workTime.getShiftBegin()));
                    textViewShiftBeginTime.setText(Support.makeTimeText(workTime.getShiftBegin()));
                } else if (type == 1) {
                    textViewShiftEndDate.setText(Support.makeDateText(workTime.getShiftEnd()));
                    textViewShiftEndTime.setText(Support.makeTimeText(workTime.getShiftEnd()));
                }
            }
            long l = editFragmentViewModel.recalculateWorkTime(workTime);
            workTime.setWorkTime(l);
            totalTimeTextView.setText(Support.convertTimeToString(workTime.getWorkTime()));
            editFragmentViewModel.updateWorkTime(workTime);
            editDialogFragment.dismiss();
        }
    };
}
