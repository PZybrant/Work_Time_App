package com.example.patryk.work_time_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.adapters.HistoryAdapter;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private static final int WORK_TIME_LOADER = 0;

    private AppCompatImageView filterButton;
    private AppCompatButton resetButton;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<WorkTime> workTimeList;
    private HistoryFragmentViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInsanceState) {
        super.onCreate(savedInsanceState);
        viewModel = new ViewModelProvider(this).get(HistoryFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        historyAdapter = new HistoryAdapter(getContext(), viewModel, adapterListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(historyAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.deleteWorkTime(historyAdapter.getSwipedTime(viewHolder.getAdapterPosition()));
                }
            }

        }).attachToRecyclerView(recyclerView);

        viewModel.getWorkTimeList().observe(getViewLifecycleOwner(), list -> {
            workTimeList = list;
            historyAdapter.setTimes(workTimeList);
        });

        filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new FilterDialogFragment();
                dialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
                HistoryFragment.this.onPause();
            }
        });

        resetButton = view.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyAdapter.setTimes(workTimeList);
            }
        });
        return view;
    }

    public void update(Bundle args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        long from = args.getLong("from");
        long to = args.getLong("to");

        GregorianCalendar date1 = new GregorianCalendar(Locale.getDefault());
        date1.setTime(new Date(from));

        GregorianCalendar date2 = new GregorianCalendar(Locale.getDefault());
        date1.setTime(new Date(to));

        String fDate = "'" + dateFormat.format(date1.getTime()) + " 00:00:00'";
        String tDate = "'" + dateFormat.format(date2.getTime()) + " 23:59:00'";

        viewModel.getWorkWithSpecifiedDate(fDate, tDate).observe(getViewLifecycleOwner(), list -> historyAdapter.setTimes(list));
    }

    private HistoryAdapter.HistoryAdapterListener adapterListener = workId -> {
        DetailWorkTimeDialogFragment detailWorkTimeDialogFragment = new DetailWorkTimeDialogFragment(viewModel, workId);
        detailWorkTimeDialogFragment.show(getChildFragmentManager(), getTag());
    };

}
