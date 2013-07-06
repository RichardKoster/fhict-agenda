package nl.marijnvdwerf.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import com.richardkoster.fhictagenda.R;

import org.joda.time.DateTime;

import nl.marijnvdwerf.widget.ScheduleAdapter.ScheduleEvent;

public class ScheduleView extends AdapterView<ScheduleAdapter> {

    private static int PADDING = 24;
    private DateTime mDate = DateTime.now();

    private ScheduleAdapter mScheduleAdapter;

    public ScheduleView(Context context) {
        this(context, null);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.scheduleViewStyle);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public ScheduleAdapter getAdapter() {
        return mScheduleAdapter;
    }

    @Override
    public void setAdapter(ScheduleAdapter scheduleAdapter) {
        mScheduleAdapter = scheduleAdapter;
        requestLayout();
    }

    public void setDate(DateTime date) {
        mDate = date;
        requestLayout();
    }

    public DateTime getDate() {
        return mDate;
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int i) {

    }

    View obtainView(int position) {
        return getAdapter().getView(position, null, this);
    }

    ScheduleEvent obtainEvent(int position) {
        return getAdapter().getEvent(position);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getAdapter() == null) {
            return;
        }
        for (int i = 0; i < getAdapter().getCountForDate(getDate()); i++) {
            View child = obtainView(i);
            ScheduleEvent event = obtainEvent(i);
            Rect eventRect = getEventRect(i);
            child.measure(MeasureSpec.makeMeasureSpec(eventRect.width(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(eventRect.height(), MeasureSpec.EXACTLY));
            child.layout(eventRect.left, eventRect.top, eventRect.right, eventRect.bottom);
            addViewInLayout(child, i, null, true);
        }

        invalidate();
    }

    protected Rect getEventRect(int position) {
        ScheduleEvent event = obtainEvent(position);
        Rect r = new Rect();
        r.top = getVerticalPosition(event.getStartDateTime());
        r.bottom = getVerticalPosition(event.getEndDateTime());

        int width = 352;
        r.left = 80;
        r.right = r.left + width;
        if (position % 2 != 0) {
            r.offset(352, 0);
        }
        return r;
    }

    protected int getVerticalPosition(DateTime dateTime) {
        return getVerticalPosition(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    protected int getVerticalPosition(int hour, int minute) {
        float availableHeight = this.getHeight() - 2 * (PADDING);
        float time = (float) hour + ((float) minute / 60f);
        float y = PADDING;
        y += availableHeight / 24f * time;
        return Math.round(y);
    }

    Paint getHourLabelPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setTextSize(getResources().getDisplayMetrics().scaledDensity * 13);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setAntiAlias(true);
        return paint;
    }

    public Paint getHourLinePaint() {
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    public Paint getHalfHourLinePaint() {
        Paint paint = getHourLinePaint();
        paint.setPathEffect(new DashPathEffect(new float[]{1, 1}, 0));
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint hourLabelPaint = getHourLabelPaint();
        Paint hourLinePaint = getHourLinePaint();
        Paint halfHourLinePaint = getHalfHourLinePaint();
        int lineOffset = (int) (-hourLabelPaint.getFontMetrics().ascent / 2f);

        for (int i = 0; i <= 24; i++) {
            String time = Integer.toString(i) + ":00";
            float hourPosY = getVerticalPosition(i, 0);
            canvas.drawText(time, getResources().getDisplayMetrics().scaledDensity * 48, hourPosY + lineOffset, hourLabelPaint);
            canvas.drawLine(getResources().getDisplayMetrics().scaledDensity * (48 + 8), hourPosY, getWidth(), hourPosY, hourLinePaint);

            float halfPosY = getVerticalPosition(i, 30);
            Path p = new Path();
            p.moveTo(getResources().getDisplayMetrics().scaledDensity * (48 + 8) + .5f, halfPosY);
            p.lineTo(getWidth(), halfPosY);
            canvas.drawPath(p, halfHourLinePaint);
        }
    }
}
