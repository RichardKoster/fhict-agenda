package com.richardkoster.fhictagenda;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DurationFieldType;

public class WeekView extends LinearLayout {

    private OnDateSelectedListener mDateSelectedListener;

    private int mSelectedDay = DateTimeConstants.MONDAY;
    private DateTime mStartDate;

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Force horizontal layout
        setOrientation(HORIZONTAL);

        // Center children
        setGravity(Gravity.CENTER);
    }

    public void setStartDate(DateTime startDate) {
        mStartDate = startDate;
        for (int i = 0; i < 5; i++) {
            addTab(startDate);
            startDate = startDate.withFieldAdded(DurationFieldType.days(), 1);
        }
    }

    protected void addTab(final DateTime date) {
        TextView tab = new TextView(getContext());
        tab.setText(date.dayOfMonth().getAsText());
        tab.setFocusable(true);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        tab.setTextSize(getResources().getDimensionPixelSize(R.dimen.actionbar_tab_text_size));
        tab.setTypeface(Typeface.SANS_SERIF, 1);
        tab.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        tab.setWidth(getResources().getDimensionPixelSize(R.dimen.actionbar_tab_width));

        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                mSelectedDay = date.getDayOfWeek();

                if (mDateSelectedListener == null) {
                    return;
                }

                mDateSelectedListener.onDateSelected(date);
            }
        });

        addView(tab);
    }

    public int getSelectedDay() {
        return mSelectedDay;
    }

    public void setSelectedDay(int weekDay) {
        mSelectedDay = weekDay;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        mDateSelectedListener = listener;
    }

    interface OnDateSelectedListener {
        public void onDateSelected(DateTime date);
    }
}
