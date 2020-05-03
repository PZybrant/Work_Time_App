package com.example.patryk.work_time_app.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.adapters.DetailDialogFragmentAdapter;
import com.example.patryk.work_time_app.data.PauseTime;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class DetailWorkTimeDialogFragment extends DialogFragment {

    private long mWorkId;
    private RecyclerView recyclerView;
    private TextView totalTimeTextView;
    private Button closeButton;
    private HistoryFragmentViewModel mViewModel;
    private ArrayList<String> strings;
    private WorkTime mWorkTime;
    private List<PauseTime> mPauseTimeListl;

    public DetailWorkTimeDialogFragment(HistoryFragmentViewModel viewModel, long workId) {
        this.mViewModel = viewModel;
        this.mWorkId = workId;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.detail_work_time_dialog_fragment, container, false);

        totalTimeTextView = view.findViewById(R.id.totalTimeTextView);
        closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(view1 -> dismiss());

        strings = initList();

        recyclerView = view.findViewById(R.id.detailRecyclerView);
        DetailDialogFragmentAdapter adapter = new DetailDialogFragmentAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setList(strings);
        totalTimeTextView.setText(convertToString(mWorkTime.getWorkTime()));

        return view;
    }

    private ArrayList<String> initList() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        mWorkTime = mViewModel.getOneWorkTime(mWorkId);
        mPauseTimeListl = mViewModel.getPauseTimesWithWorkId(mWorkId);

        stringArrayList.add(mWorkTime.getShiftBegin());
        for (PauseTime p : mPauseTimeListl) {
            stringArrayList.add(p.getPauseBegin() + " -> " + p.getPauseEnd());
        }
        stringArrayList.add(mWorkTime.getShiftEnd());

        return stringArrayList;
    }

    private String convertToString(long timeInMilis) {
        long seconds = timeInMilis / 1000;

        long minutes = seconds / 60;
        seconds = seconds % 60;

        long hours = minutes / 60;
        minutes = minutes % 60;

        String s = (seconds < 10) ? ("0" + seconds) : String.valueOf(seconds);
        String m = (minutes < 10) ? ("0" + minutes) : String.valueOf(minutes);
        String h = (hours < 10) ? ("0" + hours) : String.valueOf(hours);

        return String.format("%s:%s:%s", h, m, s);
    }

}
