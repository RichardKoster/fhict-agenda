package nl.marijnvdwerf.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.richardkoster.fhictagenda.R;

public class ScheduleView extends AdapterView<ScheduleAdapter> {

    private static int PADDING = 24;

    private ScheduleAdapter mScheduleAdapter;

    public ScheduleView(Context context) {
        this(context, null);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.scheduleViewStyle);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
    }

    @Override
    public ScheduleAdapter getAdapter() {
        return mScheduleAdapter;
    }

    @Override
    public void setAdapter(ScheduleAdapter scheduleAdapter) {
        mScheduleAdapter = scheduleAdapter;
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int i) {

    }


    Paint getHourLabelPaint() {
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDisplayMetrics().scaledDensity * 13);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setAntiAlias(true);
        return paint;
    }

    protected int getVerticalPosition(int hour, int minute) {
        float availableHeight = this.getHeight() - 2 * (PADDING);
        float time = (float) hour + ((float) minute / 60f);
        int y = PADDING;
        y += Math.round(availableHeight / 24f * time);
        return y;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutEvents();
    }

    protected void layoutEvents() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint hourPaint = getHourLabelPaint();
        int lineOffset = (int) (-hourPaint.getFontMetrics().ascent / 2f);

        for (int i = 0; i <= 24; i++) {
            String time = Integer.toString(i) + ":00";
            int y = getVerticalPosition(i, 0);
            canvas.drawText(time, getResources().getDisplayMetrics().scaledDensity * 48, y + lineOffset, hourPaint);
            canvas.drawLine(getResources().getDisplayMetrics().scaledDensity * (48 + 8), y, getWidth(), y, hourPaint);
        }
    }
}
