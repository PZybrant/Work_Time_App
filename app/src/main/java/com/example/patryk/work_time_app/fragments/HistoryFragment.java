package com.example.patryk.work_time_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.adapters.HistoryAdapter;
import com.example.patryk.work_time_app.data.WorkTime;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private TextView totalTimeTextView;
    private HistoryAdapter historyAdapter;
    private List<WorkTime> workTimeList;
    private HistoryFragmentViewModel viewModel;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(HistoryFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        totalTimeTextView = view.findViewById(R.id.fragment_history_tv_total_value);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_history_rv);
        FloatingActionButton addButton = view.findViewById(R.id.fragment_history_fab_add);
        AppCompatImageButton filterButton = view.findViewById(R.id.fragment_history_ib_filter);
        AppCompatButton resetButton = view.findViewById(R.id.fragment_history_button_reset);

        long id = sharedPreferences.getLong("work_id", -1);
        if (id > 0) {
            addButton.setVisibility(View.INVISIBLE);
        }

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
                WorkTime workTime = historyAdapter.getSwipedTime(viewHolder.getAdapterPosition());
                if (direction == ItemTouchHelper.LEFT) {
                    if (workTime.isFinished()) {
                        int numberOfRowDeleted = viewModel.deleteWorkTime(workTime);
                        if (numberOfRowDeleted > 0) {
                            Toast.makeText(getContext(), "Delete successful", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Cannot be deleted until is finished", Toast.LENGTH_SHORT).show();
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    if (workTime.isFinished()) {
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        EditFragment editFragment = new EditFragment(workTime);
                        fragmentTransaction.replace(R.id.fragmentContainer, editFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else {
                        Toast.makeText(getContext(), "Cannot be edited until is finished", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }).attachToRecyclerView(recyclerView);

        viewModel.getWorkTimeList().observe(getViewLifecycleOwner(), list -> {
            workTimeList = list;
            historyAdapter.setTimes(workTimeList);
            long totalWorkTime = 0;
            for (WorkTime wt : list) {
                totalWorkTime += wt.getWorkTime();
            }
            totalTimeTextView.setText(Support.convertToString(totalWorkTime));
        });


        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new FilterDialog();
                dialogFragment.show(getActivity().getSupportFragmentManager(), getTag());
                HistoryFragment.this.onPause();
            }
        });

        resetButton.setOnClickListener(view1 -> historyAdapter.setTimes(workTimeList));

        addButton.setOnClickListener(view1 -> {
            DialogFragment addDialog = new AddDialog(viewModel);
            addDialog.show(getParentFragmentManager(), getTag());
        });
        return view;
    }

    public void update(Bundle args) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long from = args.getLong("from");
        long to = args.getLong("to");

        GregorianCalendar date1 = new GregorianCalendar(Locale.getDefault());
        date1.setTime(new Date(from));

        GregorianCalendar date2 = new GregorianCalendar(Locale.getDefault());
        date2.setTime(new Date(to));

        String fDate = dateFormat.format(date1.getTime());
        String tDate = dateFormat.format(date2.getTime());

        viewModel.getWorkWithSpecifiedDateLiveData(fDate, tDate).observe(getViewLifecycleOwner(), list -> {
                    historyAdapter.setTimes(list);
                    long totalWorkTime = 0;
                    for (WorkTime wt : list) {
                        totalWorkTime += wt.getWorkTime();
                    }
                    totalTimeTextView.setText(Support.convertToString(totalWorkTime));
                }
        );
    }

    private HistoryAdapter.HistoryAdapterListener adapterListener = mWorkTime -> {
        DetailWorkTimeDialog detailWorkTimeDialog = new DetailWorkTimeDialog(viewModel, mWorkTime);
        detailWorkTimeDialog.show(getChildFragmentManager(), getTag());
    };
}
