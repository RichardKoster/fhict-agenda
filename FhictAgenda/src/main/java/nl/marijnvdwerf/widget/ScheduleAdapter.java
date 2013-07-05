package nl.marijnvdwerf.widget;

import android.widget.Adapter;

import java.util.Date;

public interface ScheduleAdapter extends Adapter {

    public int getCountForDate(Date date);

    public ScheduleEvent getEvent(int position);

    public static interface ScheduleEvent {
        public Date getStartDateTime();

        public Date getEndDateTime();
    }
}
