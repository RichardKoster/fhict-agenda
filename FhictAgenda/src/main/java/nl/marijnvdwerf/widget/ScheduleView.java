package nl.marijnvdwerf.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.richardkoster.fhictagenda.R;

import org.joda.time.DateTime;

public class ScheduleView extends FrameLayout {

    private SchedulePagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private DateTime mDate = DateTime.now();

    private ScheduleAdapter mScheduleAdapter;

    private static int PADDING = 24;
    private int mHorizontalSpacing = 0;

    public ScheduleView(Context context) {
        this(context, null);
    }

    public ScheduleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScheduleView, defStyle, 0);

        mHorizontalSpacing = a.getDimensionPixelOffset(R.styleable.ScheduleView_horizontalSpacing, 0);

        setBackgroundColor(Color.TRANSPARENT);

        mViewPagerAdapter = new SchedulePagerAdapter();

        mViewPager = new ViewPager(getContext());
        mViewPager.setBackgroundColor(Color.MAGENTA);
        mViewPager.setAdapter(mViewPagerAdapter);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.leftMargin = Math.round(getResources().getDisplayMetrics().scaledDensity * 64f);
        lp.rightMargin = Math.round(getResources().getDisplayMetrics().scaledDensity * 16f);


        addView(mViewPager, lp);
    }

    public ScheduleAdapter getAdapter() {
        return mScheduleAdapter;
    }

    public void setAdapter(ScheduleAdapter scheduleAdapter) {
        mScheduleAdapter = scheduleAdapter;
        requestLayout();
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
        paint.setStrokeWidth(Math.round(getResources().getDisplayMetrics().scaledDensity * 1f));
        return paint;
    }

    public Paint getHalfHourLinePaint() {
        Paint paint = getHourLinePaint();
        int dipWidth = Math.round(getResources().getDisplayMetrics().scaledDensity * 1f);
        paint.setPathEffect(new DashPathEffect(new float[]{dipWidth, dipWidth}, 0));
        return paint;
    }

    protected int getVerticalPosition(int hour, int minute) {
        float availableHeight = this.getHeight() - 2 * (PADDING);
        float time = (float) hour + ((float) minute / 60f);
        float y = PADDING;
        y += availableHeight / 24f * time;
        return Math.round(y);
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

    private class SchedulePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            DateTime date = (DateTime) o;
            ScheduleDayLayout page = (ScheduleDayLayout) view;
            return page.getDate().equals(date);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            DateTime date = DateTime.now().plusDays(position);

            ScheduleDayLayout page = new ScheduleDayLayout(getContext());
            page.setAdapter(mScheduleAdapter);
            page.setDate(date);
            container.addView(page);

            return date;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            DateTime date = (DateTime) object;
            for (int i = 0; i < container.getChildCount(); i++) {
                ScheduleDayLayout page = (ScheduleDayLayout) container.getChildAt(i);
                if (page != null && page.getDate().equals(date)) {
                    container.removeViewAt(i);
                    return;
                }
            }
        }
    }
}
