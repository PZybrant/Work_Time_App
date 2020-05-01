package com.example.patryk.work_time_app.fragments;

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


public class DetailWorkTimeDialogFragment extends DialogFragment {

    private RecyclerView recyclerView;
    private TextView totalTimeTextView;
    private Button closeButton;
    private ViewModel historyFragmentViewModel;

    public DetailWorkTimeDialogFragment(ViewModel viewModel) {
        this.historyFragmentViewModel = viewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.detail_work_time_dialog_fragment, container, false);

        totalTimeTextView = view.findViewById(R.id.totalTimeTextView);
        closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(view1 -> dismiss());

        recyclerView = view.findViewById(R.id.detailRecyclerView);
        DetailDialogFragmentAdapter adapter = new DetailDialogFragmentAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

}
