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

    private WorkTime mWorkTime;
    private TextView totalTimeTextView;
    private EditDialog editDialogFragment;

    private EditFragmentViewModel mViewModel;
    private List<PauseTime> pauseTimeList;
    ;
    private View viewShiftBegin;
    private RecyclerView recyclerView;
    private TextView textViewShiftBeginDate, textViewShiftBeginTime, textViewShiftEndDate, textViewShiftEndTime;

    public EditFragment(WorkTime workTime) {
        this.mWorkTime = workTime;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EditFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        viewShiftBegin = view.findViewById(R.id.fragment_edit_view_shift_begin);
        textViewShiftBeginDate = view.findViewById(R.id.fragment_edit_tv_shift_begin_date);
        textViewShiftBeginTime = view.findViewById(R.id.fragment_edit_tv_shift_begin_time);
        View viewShiftEnd = view.findViewById(R.id.fragment_edit_view_shift_end);
        textViewShiftEndDate = view.findViewById(R.id.fragment_edit_tv_shift_end_date);
        textViewShiftEndTime = view.findViewById(R.id.fragment_edti_tv_shift_end_time);
        ImageButton buttonAdd = view.findViewById(R.id.fragment_edit_ib_add);
        ImageButton buttonBack = view.findViewById(R.id.fragment_edit_ib_back);
        ImageButton buttonDelete = view.findViewById(R.id.fragment_edit_ib_delete);
        recyclerView = view.findViewById(R.id.fragment_edit_rv);
        totalTimeTextView = view.findViewById(R.id.fragment_edit_tv_total);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EditAdapter adapter = new EditAdapter(adapterListener);
        recyclerView.setAdapter(adapter);

        textViewShiftBeginDate.setText(Support.makeDateText(mWorkTime.getShiftBegin()));
        textViewShiftBeginTime.setText(Support.makeTimeText(mWorkTime.getShiftBegin()));
        textViewShiftEndDate.setText(Support.makeDateText(mWorkTime.getShiftEnd()));
        textViewShiftEndTime.setText(Support.makeTimeText(mWorkTime.getShiftEnd()));

        totalTimeTextView.setText(Support.convertToString(mWorkTime.getWorkTime()));

        viewShiftBegin.setOnClickListener(v -> {
            editDialogFragment = new EditDialog(listener, mViewModel, mWorkTime, 0);
            editDialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
            view.clearFocus();
        });

        viewShiftEnd.setOnClickListener(v -> {
            editDialogFragment = new EditDialog(listener, mViewModel, mWorkTime, 1);
            editDialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
            view.clearFocus();
        });


        buttonAdd.setOnClickListener(view1 -> {
            long l;
            if (pauseTimeList == null || pauseTimeList.size() == 0 && mWorkTime.isFinished()) {
                l = calculateDifference(mWorkTime.getShiftBegin().getTimeInMillis(), mWorkTime.getShiftEnd().getTimeInMillis()) / 2;
            } else {
                l = calculateDifference(mWorkTime.getShiftBegin().getTimeInMillis(), pauseTimeList.get(0).getPauseBegin().getTimeInMillis()) / 2;
            }
            l /= 1000;
            if (l >= 4) {
                Calendar newPauseBeginTime = Calendar.getInstance();
                newPauseBeginTime.setTimeInMillis(mWorkTime.getShiftBegin().getTimeInMillis());
                newPauseBeginTime.add(Calendar.SECOND, (int) l);
                PauseTime newPauseTime = new PauseTime(mWorkTime.getId(), newPauseBeginTime, true);
                Calendar newPauseEndTime = Calendar.getInstance();
                newPauseEndTime.setTimeInMillis(newPauseBeginTime.getTimeInMillis());
                newPauseEndTime.add(Calendar.SECOND, 1);
                newPauseTime.setPauseEnd(newPauseEndTime);
                newPauseTime.setPauseTime(calculateDifference(newPauseTime.getPauseBegin().getTimeInMillis(), newPauseTime.getPauseEnd().getTimeInMillis()));
                long rowId = mViewModel.insertPauseTime(newPauseTime);
                if (rowId > 0) {
                    Toast.makeText(getContext(), "Insertion confirmed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Not enough time between", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBack.setOnClickListener(view1 -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        buttonDelete.setOnClickListener(view1 -> {
            if (mWorkTime.isFinished()) {
                int numberOfRowsDeleted = mViewModel.deleteWorkTime(mWorkTime);
                if (numberOfRowsDeleted > 0) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                }
            } else {
                Toast.makeText(getContext(), "Cannot be deleted", Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getPauseTimesWithWorkIdLiveData(mWorkTime.getId()).observe(getViewLifecycleOwner(), pauseTimes -> {
            pauseTimeList = pauseTimes;
            adapter.setList(pauseTimes);
            recyclerView.getAdapter().notifyDataSetChanged();
        });

        return view;
    }

    private EditAdapter.EditFragmentAdapterListener adapterListener = new EditAdapter.EditFragmentAdapterListener() {
        @Override
        public void onAddButtonClick(PauseTime pauseTime, int pos) {
            long l;
            if (pauseTimeList.size() == 1 || pos == pauseTimeList.size() - 1) {
                l = calculateDifference(pauseTime.getPauseEnd().getTimeInMillis(), mWorkTime.getShiftEnd().getTimeInMillis()) / 2;
            } else {
                l = calculateDifference(pauseTime.getPauseEnd().getTimeInMillis(), pauseTimeList.get(pos + 1).getPauseBegin().getTimeInMillis()) / 2;
            }
            l /= 1000;
            if (l >= 4) {
                Calendar newPauseTimeBegin = Calendar.getInstance();
                newPauseTimeBegin.setTimeInMillis(pauseTime.getPauseEnd().getTimeInMillis());
                newPauseTimeBegin.add(Calendar.SECOND, (int) l);
                PauseTime newPauseTime = new PauseTime(mWorkTime.getId(), newPauseTimeBegin, true);
                Calendar newPauseTimeEnd = Calendar.getInstance();
                newPauseTimeEnd.setTimeInMillis(newPauseTimeBegin.getTimeInMillis());
                newPauseTimeEnd.add(Calendar.SECOND, 1);
                newPauseTime.setPauseEnd(newPauseTimeEnd);
                newPauseTime.setPauseTime(calculateDifference(newPauseTimeBegin.getTimeInMillis(), newPauseTimeEnd.getTimeInMillis()));
                long rowId = mViewModel.insertPauseTime(newPauseTime);
                if (rowId > 0) {
                    Toast.makeText(getContext(), "Insertion confirmed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Not enough time between", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onDeleteButtonClick(PauseTime pauseTime) {
            if (pauseTime.isFinished()) {
                mViewModel.deletePauseTime(pauseTime);
                long totalTime = mWorkTime.getWorkTime();
                totalTime -= pauseTime.getPauseTime();
                mWorkTime.setWorkTime(totalTime);
                mViewModel.updateWorkTime(mWorkTime);
                totalTimeTextView.setText(Support.convertToString(mWorkTime.getWorkTime()));
            }
        }

        @Override
        public void onEditViewClick(View view, PauseTime pauseTime, int type) {
            editDialogFragment = new EditDialog(listener, mViewModel, mWorkTime, pauseTime, type);
            editDialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
            view.clearFocus();
        }
    };

    private EditDialog.OnApplyButtonClickListener listener = new EditDialog.OnApplyButtonClickListener() {
        @Override
        public void onClick(PauseTime pauseTime, int type) {
            if (pauseTime != null) {
                long diff = calculateDifference(pauseTime.getPauseBegin().getTimeInMillis(), pauseTime.getPauseEnd().getTimeInMillis());
                pauseTime.setPauseTime(diff);
                mViewModel.updatePauseTime(pauseTime);
            } else {
                if (type == 0) {
                    textViewShiftBeginDate.setText(Support.makeDateText(mWorkTime.getShiftBegin()));
                    textViewShiftBeginTime.setText(Support.makeTimeText(mWorkTime.getShiftBegin()));
                } else if (type == 1) {
                    textViewShiftEndDate.setText(Support.makeDateText(mWorkTime.getShiftEnd()));
                    textViewShiftEndTime.setText(Support.makeTimeText(mWorkTime.getShiftEnd()));
                }
            }
            long l = recalculateWorkTime();
            mWorkTime.setWorkTime(l);
            totalTimeTextView.setText(Support.convertToString(mWorkTime.getWorkTime()));
            mViewModel.updateWorkTime(mWorkTime);
            editDialogFragment.dismiss();
        }
    };

    private long calculateDifference(long time1, long time2) {
        return Math.abs(time1 - time2);
    }

    private long recalculateWorkTime() {
        long totalWorkTime;
        long totalPauseTime = 0;

        Calendar x = mWorkTime.getShiftBegin();
        Calendar y = mWorkTime.getShiftEnd();

        totalWorkTime = calculateDifference(x.getTimeInMillis(), y.getTimeInMillis());

        List<PauseTime> pauseTimesWithWorkId = mViewModel.getPauseTimesWithWorkId(mWorkTime.getId());
        for (PauseTime p : pauseTimesWithWorkId) {
            totalPauseTime += p.getPauseTime();
        }

        return totalWorkTime - totalPauseTime;
    }

}
