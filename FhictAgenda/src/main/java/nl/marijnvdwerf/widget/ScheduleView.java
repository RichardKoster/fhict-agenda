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
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import nl.marijnvdwerf.widget.ScheduleAdapter.ScheduleEvent;

public class ScheduleView extends AdapterView<ScheduleAdapter> {

    private static int PADDING = 24;
    private DateTime mDate = DateTime.now();

    private ScheduleAdapter mScheduleAdapter;
    private HashMap<ScheduleEvent, ScheduleEventPosition> mEventPositions = new HashMap<ScheduleEvent, ScheduleEventPosition>();

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

        calculateEventPositions();

        for (int i = 0; i < getAdapter().getCountForDate(getDate()); i++) {
            View child = obtainView(i);
            ScheduleEvent event = obtainEvent(i);
            Rect eventRect = getEventRect(event);
            child.measure(MeasureSpec.makeMeasureSpec(eventRect.width(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(eventRect.height(), MeasureSpec.EXACTLY));
            child.layout(eventRect.left, eventRect.top, eventRect.right, eventRect.bottom);
            addViewInLayout(child, i, null, true);
        }

        invalidate();
    }

    protected void calculateEventPositions() {
        if (getAdapter() == null) {
            return;
        }


        ArrayList<ScheduleEvent> events = new ArrayList<ScheduleEvent>();
        for (int i = 0; i < getAdapter().getCountForDate(getDate()); i++) {
            events.add(obtainEvent(i));
        }

        Collections.sort(events, new Comparator<ScheduleEvent>() {
            @Override
            public int compare(ScheduleEvent a, ScheduleEvent b) {
                Duration startDiff = new Duration(b.getStartDateTime(), a.getStartDateTime());
                if (startDiff.getMillis() != 0) {
                    return (int) startDiff.getMillis();
                }

                Duration endDiff = new Duration(a.getEndDateTime(), b.getEndDateTime());
                return (int) endDiff.getMillis();
            }
        });

        ArrayList<ArrayList<ScheduleEvent>> eventClusters = new ArrayList<ArrayList<ScheduleEvent>>();
        eventClusters.add(new ArrayList<ScheduleEvent>());
        DateTime clusterEndTime = events.get(0).getEndDateTime();
        for (ScheduleEvent event : events) {
            ArrayList<ScheduleEvent> currentCluster = eventClusters.get(eventClusters.size() - 1);
            if (currentCluster.size() == 0) {
                currentCluster.add(event);
                continue;
            }

            ScheduleEvent lastClusterEvent = currentCluster.get(currentCluster.size() - 1);
            Duration duration = new Duration(clusterEndTime, event.getStartDateTime());
            // Current event starts while previous event hasn't ended
            if (duration.getMillis() < 0) {
                currentCluster.add(event);

                if (clusterEndTime.getMillis() < event.getEndDateTime().getMillis()) {
                    clusterEndTime = event.getEndDateTime();
                }
                continue;
            }

            // Start new cluster
            eventClusters.add(currentCluster = new ArrayList<ScheduleEvent>());
            currentCluster.add(event);
            clusterEndTime = event.getEndDateTime();
        }

        for (ArrayList<ScheduleEvent> eventCluster : eventClusters) {
            ArrayList<ArrayList<ScheduleEventPosition>> columns = new ArrayList<ArrayList<ScheduleEventPosition>>();

            for (ScheduleEvent event : eventCluster) {
                int columnIndex = -1;

                for (ArrayList<ScheduleEventPosition> column : columns) {
                    if (column.size() == 0) {
                        columnIndex = columns.indexOf(columnIndex);
                        break;
                    }
                    ScheduleEvent lastEventInColumn = column.get(column.size() - 1).event;
                    Duration duration = new Duration(lastEventInColumn.getEndDateTime(), event.getStartDateTime());
                    if (duration.getMillis() >= 0) {
                        columnIndex = columns.indexOf(column);
                        break;
                    }
                }

                if (columnIndex == -1) {
                    columnIndex = columns.size();
                    columns.add(new ArrayList<ScheduleEventPosition>());
                }

                ScheduleEventPosition eventPosition = new ScheduleEventPosition();
                eventPosition.event = event;
                eventPosition.column = columnIndex;

                columns.get(columnIndex).add(eventPosition);
            }

            for (ArrayList<ScheduleEventPosition> column : columns) {
                for (ScheduleEventPosition eventPosition : column) {
                    eventPosition.totalColumnCount = columns.size();

                    mEventPositions.put(eventPosition.event, eventPosition);
                }
            }

        }

    }

    protected Rect getEventRect(ScheduleEvent event) {
        Rect r = new Rect();
        r.top = getVerticalPosition(event.getStartDateTime());
        r.bottom = getVerticalPosition(event.getEndDateTime());

        int maxWidth = getWidth() - 80 - 16;
        ScheduleEventPosition position = mEventPositions.get(event);
        int colWidth = maxWidth / position.totalColumnCount;
        r.left = 80 + (colWidth * position.column);
        r.right = r.left + colWidth;
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

    private class ScheduleEventPosition {
        public int column;
        public int columnSpan = 1;
        public int totalColumnCount;
        public ScheduleEvent event;
    }
}
