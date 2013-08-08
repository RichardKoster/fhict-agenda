package com.richardkoster.fhictagenda;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import nl.marijnvdwerf.app.ScheduleFragment;
import nl.marijnvdwerf.widget.ScheduleAdapter;

public class DayScheduleFragment extends ScheduleFragment {

    List<Event> events;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DateTime today = DateTime.now();
        DateTime start;
        DateTime end;
        events = new ArrayList<Event>();

        start = today.withTime(8, 45, 0, 0);
        end = today.withTime(9, 35, 0, 0);
        events.add(new Event("8:45-9:35", start, end));

        start = today.withTime(8, 45, 0, 0);
        end = today.withTime(10, 25, 0, 0);
        events.add(new Event("8:45-10:25", start, end));

        start = today.withTime(9, 35, 0, 0);
        end = today.withTime(11, 35, 0, 0);
        events.add(new Event("9:35-11:35", start, end));

        start = today.withTime(12, 25, 0, 0);
        end = today.withTime(14, 05, 0, 0);
        events.add(new Event("12:25-14:05", start, end));

        start = today.withTime(12, 25, 0, 0);
        end = today.withTime(14, 05, 0, 0);
        events.add(new Event("12:25-14:05", start, end));

        start = today.withTime(12, 25, 0, 0);
        end = today.withTime(14, 05, 0, 0);
        events.add(new Event("12:25-14:05", start, end));

        getScheduleView().setAdapter(new DayScheduleAdapter(getActivity(), events));
    }

    private class Event implements ScheduleAdapter.ScheduleEvent {
        protected DateTime mStartDateTime;
        protected DateTime mEndDateTime;
        private CharSequence mTitle;

        public Event(String title, DateTime startDateTime, DateTime endDateTime) {
            mTitle = title;
            mStartDateTime = startDateTime;
            mEndDateTime = endDateTime;
        }

        @Override
        public DateTime getStartDateTime() {
            return mStartDateTime;
        }

        @Override
        public DateTime getEndDateTime() {
            return mEndDateTime;
        }

        private CharSequence getTitle() {
            return mTitle;
        }
    }

    private class DayScheduleAdapter extends ScheduleAdapter {
        Context mContext;
        LayoutInflater mInflater;
        List<Event> mEvents;

        public DayScheduleAdapter(Context context, List<Event> events) {
            mContext = context.getApplicationContext();
            mInflater = LayoutInflater.from(mContext);
            mEvents = events;
        }

        @Override
        public int getCountForDate(DateTime date) {
            return mEvents.size();
        }

        @Override
        public Event getEvent(int position) {
            return mEvents.get(position);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            Event e = getEvent(i);
            View v = mInflater.inflate(R.layout.item_event, viewGroup, false);

            TextView eventTitle = (TextView) v.findViewById(R.id.event_title);
            eventTitle.setText(e.getTitle());
            return v;
        }
    }
}