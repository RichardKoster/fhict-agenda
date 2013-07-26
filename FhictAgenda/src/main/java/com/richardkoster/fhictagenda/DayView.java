package com.richardkoster.fhictagenda;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;

public class DayView extends FrameLayout implements ViewPager.OnPageChangeListener {

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;
    private final ViewPager mViewPager;
    private final DateTime mStartDate;
    private final DateTime mEndDate;
    private OnDateChangeListener mOnDateChangeListener;
    private DateTime currentDate;

    public DayView(Context context) {
        this(context, null);
    }

    public DayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mStartDate = new DateTime().withDate(2013, 7, 22);
        mEndDate = new DateTime().withDate(2013, 8, 30);
        mViewPager = new ViewPager(getContext());
        mViewPager.setAdapter(new DayViewAdapter(mStartDate, mEndDate));
        mViewPager.setOnPageChangeListener(this);

        addView(mViewPager, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnDateChangeListener == null) {
            return;
        }

        DateTime leftDate = getDate(position);
        DateTime rightDate = getDate(position + 1);
        mOnDateChangeListener.onDateScrolled(leftDate, positionOffset, rightDate);
    }

    @Override
    public void onPageSelected(int position) {
        if (mOnDateChangeListener == null) {
            return;
        }

        mOnDateChangeListener.onDateSelected(getDate(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnDateChangeListener == null) {
            return;
        }

        mOnDateChangeListener.onDateScrollStateChanged(state);
    }

    public void setOnDateChangeListener(OnDateChangeListener listener) {
        mOnDateChangeListener = listener;
    }

    private DateTime getDate(int position) {
        int day = position % 5;
        int weeks = position / 5;
        return mStartDate.plusWeeks(weeks).plusDays(day);
    }

    private int getPosition(DateTime date) {
        int weeks = Weeks.weeksBetween(mStartDate, date).getWeeks();
        int weekDay = date.getDayOfWeek() - 1;
        return weeks * 5 + weekDay;
    }

    public DateTime getCurrentDate() {
        return getDate(mViewPager.getCurrentItem());
    }

    public void setCurrentDate(DateTime date) {
        setCurrentDate(date, true);
    }

    public void setCurrentDate(DateTime date, boolean smoothScroll) {
        int position = getPosition(date);
        mViewPager.setCurrentItem(position, smoothScroll);
    }

    public interface OnDateChangeListener {
        abstract void onDateScrollStateChanged(int state);

        abstract void onDateScrolled(DateTime left, float offset, DateTime right);

        abstract void onDateSelected(DateTime date);
    }

    class DayViewAdapter extends PagerAdapter {

        protected DateTime mStartDate;
        protected DateTime mEndDate;

        DayViewAdapter(DateTime startDate, DateTime endDate) {
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
            return (weeks + 1) * 5;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int day = position % 5;
            int weeks = position / 5;
            DateTime date = mStartDate.plusWeeks(weeks).plusDays(day);

            TextView textView = new TextView(getContext());
            textView.setText(date.toString(DateTimeFormat.forPattern("d MMM")));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);

            container.addView(textView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            return textView;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }
    }
}
