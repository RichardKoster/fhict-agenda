package nl.marijnvdwerf.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ScheduleDayLayout extends FrameLayout {

    private static final String TAG = "ScheduleDayLayout";
    private DateTime mDate = DateTime.now();

    private int mVerticalPadding = 24;
    private int mHorizontalSpacing = 0;
    private int mVerticalSpacing = 0;

    private ScheduleAdapter mScheduleAdapter;
    private HashMap<ScheduleAdapter.ScheduleEvent, ScheduleEventPosition> mEventPositions = new HashMap<ScheduleAdapter.ScheduleEvent, ScheduleEventPosition>();
    private static int mColor = Color.BLACK;

    public ScheduleDayLayout(Context context) {
        this(context, null);
    }

    public ScheduleDayLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleDayLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBackgroundColor(Color.RED);
    }

    public void setAdapter(ScheduleAdapter adapter) {
        mScheduleAdapter = adapter;
    }

    public ScheduleAdapter getAdapter() {
        return mScheduleAdapter;
    }

    public void setDate(DateTime date) {
        mDate = date;
        requestLayout();

        TextView tv = new TextView(getContext());
        tv.setText(date.toString());
        addView(tv);
    }

    public DateTime getDate() {
        return mDate;
    }

    View obtainView(int position) {
        return getAdapter().getView(position, null, this);
    }

    ScheduleAdapter.ScheduleEvent obtainEvent(int position) {
        return getAdapter().getEvent(position);
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getAdapter() == null) {
            return;
        }

        if (mDate.toString(ISODateTimeFormat.basicDate()).equals("20130808")) {
            Log.d(TAG, "onLayout()");
        }

        calculateEventPositions();

        for (int i = 0; i < getAdapter().getCountForDate(getDate()); i++) {
            View child = obtainView(i);
            ScheduleAdapter.ScheduleEvent event = obtainEvent(i);
            Rect eventRect = getEventRect(event);
            FrameLayout.LayoutParams layoutParams = new LayoutParams(eventRect.width(), eventRect.height());
            layoutParams.setMargins(eventRect.left, eventRect.top, eventRect.right, eventRect.bottom);
            addViewInLayout(child, i, layoutParams, true);


            if (mDate.toString(ISODateTimeFormat.basicDate()).equals("20130808")) {
                Log.d(TAG, eventRect.toString());
            }
        }

        invalidate();
    }

    protected void calculateEventPositions() {
        if (getAdapter() == null) {
            return;
        }


        ArrayList<ScheduleAdapter.ScheduleEvent> events = new ArrayList<ScheduleAdapter.ScheduleEvent>();
        for (int i = 0; i < getAdapter().getCountForDate(getDate()); i++) {
            events.add(obtainEvent(i));
        }

        Collections.sort(events, new Comparator<ScheduleAdapter.ScheduleEvent>() {
            @Override
            public int compare(ScheduleAdapter.ScheduleEvent a, ScheduleAdapter.ScheduleEvent b) {
                Duration startDiff = new Duration(b.getStartDateTime(), a.getStartDateTime());
                if (startDiff.getMillis() != 0) {
                    return (int) startDiff.getMillis();
                }

                Duration endDiff = new Duration(a.getEndDateTime(), b.getEndDateTime());
                return (int) endDiff.getMillis();
            }
        });

        ArrayList<ArrayList<ScheduleAdapter.ScheduleEvent>> eventClusters = new ArrayList<ArrayList<ScheduleAdapter.ScheduleEvent>>();
        eventClusters.add(new ArrayList<ScheduleAdapter.ScheduleEvent>());
        DateTime clusterEndTime = events.get(0).getEndDateTime();
        for (ScheduleAdapter.ScheduleEvent event : events) {
            ArrayList<ScheduleAdapter.ScheduleEvent> currentCluster = eventClusters.get(eventClusters.size() - 1);
            if (currentCluster.size() == 0) {
                currentCluster.add(event);
                continue;
            }

            ScheduleAdapter.ScheduleEvent lastClusterEvent = currentCluster.get(currentCluster.size() - 1);
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
            eventClusters.add(currentCluster = new ArrayList<ScheduleAdapter.ScheduleEvent>());
            currentCluster.add(event);
            clusterEndTime = event.getEndDateTime();
        }

        for (ArrayList<ScheduleAdapter.ScheduleEvent> eventCluster : eventClusters) {
            ArrayList<ArrayList<ScheduleEventPosition>> columns = new ArrayList<ArrayList<ScheduleEventPosition>>();

            for (ScheduleAdapter.ScheduleEvent event : eventCluster) {
                int columnIndex = -1;

                for (ArrayList<ScheduleEventPosition> column : columns) {
                    if (column.size() == 0) {
                        columnIndex = columns.indexOf(columnIndex);
                        break;
                    }
                    ScheduleAdapter.ScheduleEvent lastEventInColumn = column.get(column.size() - 1).event;
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

    protected Rect getEventRect(ScheduleAdapter.ScheduleEvent event) {
        Rect r = new Rect();
        r.top = getVerticalPosition(event.getStartDateTime());
        r.bottom = getVerticalPosition(event.getEndDateTime());

        int maxWidth = getWidth();
        ScheduleEventPosition position = mEventPositions.get(event);
        int colWidth = maxWidth / position.totalColumnCount;
        r.left = colWidth * position.column;
        r.right = r.left + colWidth;

        float spacing = ((float) mHorizontalSpacing) / 2f;

        if (position.column > 0) {
            // apply half of the spacing to the left;
            r.left += Math.floor(spacing);
        }
        if (position.column < position.totalColumnCount) {
            // apply half of the spacing to the right;
            r.right -= Math.ceil(spacing);
        }

        return r;
    }

    protected int getVerticalPosition(DateTime dateTime) {
        return getVerticalPosition(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    protected int getVerticalPosition(int hour, int minute) {
        float availableHeight = this.getHeight() - 2 * (mVerticalPadding);
        float time = (float) hour + ((float) minute / 60f);
        float y = mVerticalPadding;
        y += availableHeight / 24f * time;
        return Math.round(y);
    }

    private class ScheduleEventPosition {
        public int column;
        public int columnSpan = 1;
        public int totalColumnCount;
        public ScheduleAdapter.ScheduleEvent event;
    }
}
