package com.example.patryk.work_time_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patryk.work_time_app.adapters.HistoryAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CustomItemDivider extends RecyclerView.ItemDecoration {

    private Drawable divider;
    private Paint p;
    private @ColorInt
    int color;

    @SuppressLint("Recycle")
    public CustomItemDivider(Context context) {
        int[] attrs = {android.R.attr.listDivider};
        divider = context.obtainStyledAttributes(attrs).getDrawable(0);
        p = new Paint();
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true);
        color = typedValue.data;
        p.setColor(color);
    }

    private Calendar separatorDate = null;
    private HistoryAdapter adapter;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (adapter == null) {
            adapter = (HistoryAdapter) parent.getAdapter();
        }

        HistoryAdapter.MyViewHolder childViewHolder = (HistoryAdapter.MyViewHolder) parent.getChildViewHolder(view);

        int isHeader = adapter.isHeader(childViewHolder.getAdapterPosition());

        if (isHeader == 0) {
            outRect.set(30, 90, 30, 0);
        } else {
            outRect.set(30, 5, 30, 5);
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();


        Calendar separatorDate = null;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View itemView = parent.getChildAt(i);
            HistoryAdapter.MyViewHolder childViewHolder = (HistoryAdapter.MyViewHolder) parent.getChildViewHolder(itemView);

            HistoryAdapter adapter = (HistoryAdapter) parent.getAdapter();
            int isHeader = adapter.isHeader(childViewHolder.getAdapterPosition());

            int top = itemView.getTop() - ((RecyclerView.LayoutParams) itemView.getLayoutParams()).topMargin - 10;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.setTint(color);
            p.setTextSize(40);

            if (isHeader == 0) {
                separatorDate = Calendar.getInstance();
                separatorDate.setTimeInMillis(childViewHolder.getWorkTime().getShiftBegin().getTimeInMillis());
                String dateText = makeDateText(separatorDate);
                c.drawText(dateText, itemView.getLeft(), itemView.getTop() - p.getTextSize(), p);
                divider.draw(c);
            }
        }
    }

    private static String makeDateText(Calendar calendar) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL dd, EEEE", Locale.getDefault());
            return dateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
