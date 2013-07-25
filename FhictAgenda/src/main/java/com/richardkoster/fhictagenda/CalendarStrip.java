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
import org.joda.time.Weeks;

public class CalendarStrip extends FrameLayout {


    private final ViewPager mViewPager;
    private DateTime mStartDate;
    private DateTime mEndDate;

    public CalendarStrip(Context context) {
        this(context, null);
    }

    public CalendarStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setStartDate(new DateTime().withDate(2013, 7, 26));
        setEndDate(new DateTime().withDate(2013, 8, 27));

        mViewPager = new ViewPager(getContext());
        mViewPager.setAdapter(new CalendarWeekAdapter(mStartDate, mEndDate));

        addView(mViewPager, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //WeekView mWeek = new WeekView(getContext());
        //addView(mWeek, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
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

        View currentTab = currentWeek.getChildAt(0);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // draw indicator line
        Paint rectPaint = new Paint();
        rectPaint.setColor(getResources().getColor(R.color.holo_blue_light));
        int underlineHeight = Math.round(getResources().getDisplayMetrics().scaledDensity * 3f);
        canvas.drawRect(lineLeft, height - underlineHeight, lineRight, height, rectPaint);
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

            weekView.setTag(Integer.toString(position));

            container.addView(weekView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            return weekView;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }
}
