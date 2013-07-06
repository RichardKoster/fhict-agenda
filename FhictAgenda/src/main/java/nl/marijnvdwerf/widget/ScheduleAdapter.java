package nl.marijnvdwerf.widget;

import android.widget.Adapter;

import org.joda.time.DateTime;

public interface ScheduleAdapter extends Adapter {

    public int getCountForDate(DateTime date);

    public ScheduleEvent getEvent(int position);

    public static interface ScheduleEvent {
        public DateTime getStartDateTime();

        public DateTime getEndDateTime();
    }
}
