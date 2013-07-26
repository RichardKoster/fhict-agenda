package com.richardkoster.fhictagenda;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Weeks;

public class CalendarStrip extends FrameLayout implements ViewPager.OnPageChangeListener, WeekView.OnDateSelectedListener {


    private final ViewPager mViewPager;
    OnDateChangeListener mDateChangeListener;
    private DateTime mStartDate;
    private DateTime mEndDate;
    private DayView mDayView;
    private DayView.OnDateChangeListener mDateListener = new DateListener();
    private int mCurrentWeekDay = DateTimeConstants.MONDAY;
    private float mCurrentDayOffset;

    public CalendarStrip(Context context) {
        this(context, null);
    }

    public CalendarStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setStartDate(new DateTime().withDate(2013, 7, 22));
        setEndDate(new DateTime().withDate(2013, 8, 30));

        mViewPager = new ViewPager(getContext());
        mViewPager.setAdapter(new CalendarWeekAdapter(mStartDate, mEndDate));
        mViewPager.setOnPageChangeListener(this);
        addView(mViewPager, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        WeekHeaderView headerView = new WeekHeaderView(getContext());
        addView(headerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void setEndDate(DateTime dateTime) {
        mEndDate = dateTime.plusDays(7 - dateTime.getDayOfWeek());
    }

    private void setStartDate(DateTime dateTime) {
        mStartDate = dateTime.minusDays(dateTime.getDayOfWeek() - 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            return;
        }

        final int height = getHeight();

        int currentPosition = mViewPager.getCurrentItem();
        WeekView currentWeek = (WeekView) mViewPager.findViewWithTag(Integer.toString(currentPosition));

        int currentDay = mCurrentWeekDay;
        View currentTab = currentWeek.getChildAt(currentDay - 1);
        float lineLeft;
        float lineRight;

        if (mCurrentDayOffset > 0f) {
            int nextDay = currentDay + 1;
            if (nextDay == DateTimeConstants.SATURDAY) {
                nextDay = DateTimeConstants.MONDAY;
            }
            View nextTab = currentWeek.getChildAt(nextDay - 1);

            lineLeft = interpolate(currentTab.getLeft(), nextTab.getLeft(), mCurrentDayOffset);
            lineRight = interpolate(currentTab.getRight(), nextTab.getRight(), mCurrentDayOffset);
        } else {
            lineLeft = currentTab.getLeft();
            lineRight = currentTab.getRight();
        }

        // draw indicator line
        Paint rectPaint = new Paint();
        rectPaint.setColor(getResources().getColor(R.color.holo_blue_light));
        int underlineHeight = Math.round(getResources().getDisplayMetrics().scaledDensity * 4f);
        canvas.drawRect(lineLeft, height - underlineHeight, lineRight, height, rectPaint);
    }

    private float interpolate(float from, float to, float offset) {
        return from + (to - from) * offset;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mDateChangeListener == null) {
            return;
        }

        DateTime weekStart = mStartDate.plusWeeks(position);

        mDateChangeListener.onDateSelected(weekStart);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void setOnDateChangeListener(OnDateChangeListener listener) {
        mDateChangeListener = listener;
    }

    public DateTime getCurrentDate() {
        DateTime date = mStartDate;
        date = date.plusWeeks(mViewPager.getCurrentItem());
        return date;
    }

    @Override
    public void onDateSelected(DateTime date) {
        if (mDayView == null) {
            return;
        }

        mDayView.setCurrentDate(date);
    }

    public void setDayView(DayView dayView) {
        mDayView = dayView;
        mDayView.setOnDateChangeListener(mDateListener);
    }

    interface OnDateChangeListener {

        public void onDateSelected(DateTime date);

    }

    class CalendarWeekAdapter extends PagerAdapter {

        protected DateTime mStartDate;
        protected DateTime mEndDate;

        CalendarWeekAdapter(DateTime startDate, DateTime endDate) {
            mStartDate = startDate;
            mEndDate = endDate;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            int weeks = Weeks.weeksBetween(mStartDate, mEndDate).getWeeks();
            return weeks + 1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            DateTime startDate = mStartDate.plusWeeks(position);

            WeekView weekView = new WeekView(getContext());
            weekView.setStartDate(startDate);
            weekView.setOnDateSelectedListener(CalendarStrip.this);

            weekView.setTag(Integer.toString(position));

            container.addView(weekView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            return weekView;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }

    public void scrollToDate(DateTime date) {
        scrollToDate(date, 0f);
    }

    float mLastOffset = 0.0f;

    public void scrollToDate(DateTime date, float offsetPosition) {
        int week = Weeks.weeksBetween(mStartDate, date).getWeeks();

        mCurrentWeekDay = date.getDayOfWeek();
        mCurrentDayOffset = offsetPosition;


        mViewPager.setCurrentItem(week, false);
        if (date.getDayOfWeek() == DateTimeConstants.FRIDAY) {
            // Simulate drag to the LEFT (hence the negative value)
            float offset = -1 * offsetPosition * getWidth();

            if (!mViewPager.isFakeDragging()) {
                mViewPager.beginFakeDrag();
                mLastOffset = 0;
            } else {
                // first undo last scroll
                mViewPager.fakeDragBy(-1 * mLastOffset);
                mViewPager.fakeDragBy(offset);
                mLastOffset = offset;
            }
        }

        invalidate();
    }

    private class DateListener implements DayView.OnDateChangeListener {
        @Override
        public void onDateScrollStateChanged(int state) {
            if (state == DayView.SCROLL_STATE_IDLE) {
                if (mViewPager.isFakeDragging()) {
                    mViewPager.endFakeDrag();
                }
                scrollToDate(mDayView.getCurrentDate());
                invalidate();
            }
        }

        @Override
        public void onDateScrolled(DateTime left, float offset, DateTime right) {
            scrollToDate(left, offset);
        }

        @Override
        public void onDateSelected(DateTime date) {

        }
    }
}
