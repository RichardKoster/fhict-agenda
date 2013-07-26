package com.richardkoster.fhictagenda;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeekHeaderView extends LinearLayout {

    public WeekHeaderView(Context context) {
        this(context, null);
    }

    public WeekHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Force horizontal layout
        setOrientation(HORIZONTAL);

        // Center children
        setGravity(Gravity.CENTER);

        addHeader("Ma");
        addHeader("Di");
        addHeader("Wo");
        addHeader("Do");
        addHeader("Vr");
    }

    protected void addHeader(String dayName) {
        TextView header = new TextView(getContext());
        header.setText(dayName);
        header.setGravity(Gravity.CENTER);
        header.setSingleLine();
        header.setTextSize(getResources().getDimensionPixelSize(R.dimen.actionbar_tab_header_size));
        header.setTypeface(Typeface.SANS_SERIF, 1);
        header.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        header.setWidth(getResources().getDimensionPixelSize(R.dimen.actionbar_tab_width));

        addView(header);
    }
}
