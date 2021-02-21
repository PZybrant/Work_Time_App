package com.example.patryk.work_time_app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.CustomItemDivider;
import com.example.patryk.work_time_app.R;
import com.example.patryk.work_time_app.Support;
import com.example.patryk.work_time_app.adapters.HistoryAdapter;
import com.example.patryk.work_time_app.data.WorkTimeRecord;
import com.example.patryk.work_time_app.viewmodels.HistoryFragmentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private static final float ALPHA_FULL = 0.0f;

    private int dir;
    private boolean isSwipeRight = false;
    private boolean isSwipeLeft = false;
    private int[] headersArray;
    private TextView totalTimeTextView;
    private HistoryAdapter historyAdapter;
    private HistoryFragmentViewModel viewModel;
    private SharedPreferences sharedPreferences;
    private Drawable deleteIcon, editIcon;
    private Paint p;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.shared_preference_name), Context.MODE_PRIVATE);
        viewModel = new ViewModelProvider(this).get(HistoryFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        totalTimeTextView = view.findViewById(R.id.fragment_history_tv_total_value);
        RecyclerView recyclerView = view.findViewById(R.id.fragment_history_rv);
        FloatingActionButton addButton = view.findViewById(R.id.fragment_history_fab_add);
        AppCompatButton filterButton = view.findViewById(R.id.fragment_history_ib_filter);
        AppCompatButton resetButton = view.findViewById(R.id.fragment_history_button_reset);
        deleteIcon = requireContext().getDrawable(R.drawable.ic_delete_24dp);
        editIcon = requireContext().getDrawable(R.drawable.ic_edit_24dp);
        p = new Paint();

        recyclerView.setHasFixedSize(true);
        historyAdapter = new HistoryAdapter(getContext(), adapterListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(historyAdapter);
        recyclerView.addItemDecoration(new CustomItemDivider(requireContext()));


        viewModel.getWorkTimeRecordRecordList().observe(getViewLifecycleOwner(), list -> {
            populateHeadersArray(list);
            historyAdapter.setTimes(list, headersArray);
            long totalWorkTime = 0;
            for (WorkTimeRecord wt : list) {
                totalWorkTime += wt.getWorkTime();
            }
            totalTimeTextView.setText(Support.convertTimeToString(totalWorkTime));
        });


        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);

        filterButton.setOnClickListener(v -> {
            DialogFragment dialogFragment = new FilterDialog(filterDialogFragmentListener);
            dialogFragment.show(requireActivity().getSupportFragmentManager(), getTag());
            HistoryFragment.this.onPause();
        });

        resetButton.setOnClickListener(v -> viewModel.getWorkTimeRecordRecordList().observe(getViewLifecycleOwner(), list -> {
            populateHeadersArray(list);
            historyAdapter.setTimes(list, headersArray);
            long totalWorkTime = 0;
            for (WorkTimeRecord wt : list) {
                totalWorkTime += wt.getWorkTime();
            }
            totalTimeTextView.setText(Support.convertTimeToString(totalWorkTime));
        }));

        addButton.setOnClickListener(v -> {
            DialogFragment addDialog = new AddDialog(addDialogListener, viewModel);
            addDialog.show(getParentFragmentManager(), getTag());
        });
        return view;
    }

    private AddDialog.AddDialogListener addDialogListener = newWorkTime -> {
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        EditFragment editFragment = new EditFragment(newWorkTime);
        fragmentTransaction.replace(R.id.fragmentContainer, editFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    };

    private FilterDialog.FilterDialogFragmentListener filterDialogFragmentListener = (date1, date2) -> {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fDate = dateFormat.format(date1.getTime());
        String tDate = dateFormat.format(date2.getTime());
        viewModel.setTimeRecordListWithSpecifiedDate(fDate, tDate).observe(this, list -> {
            populateHeadersArray(list);
            historyAdapter.setTimes(list, headersArray);
            long totalWorkTime = 0;
            for (WorkTimeRecord wt : list) {
                totalWorkTime += wt.getWorkTime();
            }
            totalTimeTextView.setText(Support.convertTimeToString(totalWorkTime));
        });
    };

    private HistoryAdapter.HistoryAdapterListener adapterListener = mWorkTime -> {
        DetailWorkTimeDialog detailWorkTimeDialog = new DetailWorkTimeDialog(viewModel, mWorkTime);
        detailWorkTimeDialog.show(getChildFragmentManager(), getTag());
    };

    private ItemTouchHelper.Callback swipeCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            dir = direction;
            WorkTimeRecord workTimeRecord = historyAdapter.getSwipedTime(viewHolder.getAdapterPosition());
            if (direction == ItemTouchHelper.RIGHT) {
                int numberOfRowDeleted = viewModel.deleteWorkTime(workTimeRecord);
                if (numberOfRowDeleted > 0) {
                    if (!workTimeRecord.isFinished()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("work_id");
                        editor.remove("pause_id");
                        editor.remove("stringSet");
                        editor.apply();
                    }
                    Toast.makeText(getContext(), "Delete successful", Toast.LENGTH_SHORT).show();
                }

            } else if (direction == ItemTouchHelper.LEFT) {
                if (workTimeRecord.isFinished()) {
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    EditFragment editFragment = new EditFragment(workTimeRecord);
                    fragmentTransaction.replace(R.id.fragmentContainer, editFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    System.out.println(workTimeRecord.toString());
                } else {
                    Toast.makeText(getContext(), "It cannot be edited until it is finished", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            View itemView = viewHolder.itemView;
            int itemHeight = itemView.getBottom() - itemView.getTop();
            int itemWidth = itemView.getRight() - itemView.getLeft();
            boolean isCanceled = dX == 0f && !isCurrentlyActive;
            float maxSwipe = itemWidth / 5.0f;

            if (isCanceled) {
                clearCanvas(c, itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                return;
            }

            if (dX > 0) {
                isSwipeRight = true;
                int deleteIconTop = itemView.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconMargin = (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconLeft = itemView.getLeft() + deleteIconMargin;
                int deleteIconRight = itemView.getLeft() + deleteIconMargin + deleteIcon.getIntrinsicWidth();
                int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();
                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);

                if (dX < itemWidth / 5.0) {
                    final float alpha = ALPHA_FULL + (Math.abs(dX) * 4) / (float) viewHolder.itemView.getWidth();
                    p.setARGB((int) (alpha * 300), 255, 0, 0);
                } else {
                    if (isCurrentlyActive) {
                        dX = maxSwipe;
                    }
                }

                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), (float) itemView.getLeft() + dX, (float) itemView.getBottom(), p);
                itemView.setTranslationX(dX);
                deleteIcon.draw(c);

            } else if (dX < 0) {
                int editIconTop = itemView.getTop() + (itemHeight - editIcon.getIntrinsicHeight()) / 2;
                int editIconMargin = (itemHeight - editIcon.getIntrinsicHeight()) / 2;
                int editIconLeft = itemView.getRight() - editIconMargin - editIcon.getIntrinsicWidth();
                int editIconRight = itemView.getRight() - editIconMargin;
                int editIconBottom = editIconTop + editIcon.getIntrinsicHeight();
                editIcon.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom);

                maxSwipe *= -1;

                if (dX > (itemWidth / 5.0) * -1) {
                    final float alpha = ALPHA_FULL + (Math.abs(dX) * 4) / (float) itemView.getWidth();
                    p.setARGB((int) (alpha * 300), 255, 255, 0);
                } else {
                    if (isCurrentlyActive) {
                        dX = maxSwipe;
                    }
                }
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), p);
                itemView.setTranslationX(dX);
                editIcon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return (super.getSwipeThreshold(viewHolder) * 2) / 5;
        }

        @Override
        public float getSwipeEscapeVelocity(float defaultValue) {
            defaultValue *= 10;
            return super.getSwipeEscapeVelocity(defaultValue);
        }

        @Override
        public float getSwipeVelocityThreshold(float defaultValue) {
            defaultValue = 0;
            return super.getSwipeVelocityThreshold(defaultValue);
        }

        @Override
        public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
            return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy) * 2;
        }
    };

    private void clearCanvas(Canvas c, float left, float top, float right, float bottom) {
        p.setColor(Color.TRANSPARENT);
        c.drawRect(left, top, right, bottom, p);
    }

    private void populateHeadersArray(List<WorkTimeRecord> list) {
        headersArray = null;
        headersArray = new int[list.size()];

        Calendar separatorDate = null;
        for (int i = 0; i < list.size(); i++) {
            if (separatorDate == null) {
                separatorDate = Calendar.getInstance();
                separatorDate.setTimeInMillis(list.get(i).getShiftBegin().getTimeInMillis());
                separatorDate.set(Calendar.HOUR_OF_DAY, 0);
                separatorDate.set(Calendar.MINUTE, 0);
                separatorDate.set(Calendar.SECOND, 0);
                separatorDate.set(Calendar.MILLISECOND, 0);
                // put WorkTimeRecord and value for header: 0 if true & 1 if false
                headersArray[i] = 0;
            } else {
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(list.get(i).getShiftBegin().getTimeInMillis());
                date.set(Calendar.HOUR_OF_DAY, 0);
                date.set(Calendar.MINUTE, 0);
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                if (date.compareTo(separatorDate) == 0) {
                    // put WorkTimeRecord and value for header: 0 if true & 1 if false
                    headersArray[i] = 1;
                } else {
                    separatorDate.setTimeInMillis(date.getTimeInMillis());
                    // put WorkTimeRecord and value for header: 0 if true & 1 if false
                    headersArray[i] = 0;
                }
            }
        }
    }
}
