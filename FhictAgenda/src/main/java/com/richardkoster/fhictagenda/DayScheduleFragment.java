package com.richardkoster.fhictagenda;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import nl.marijnvdwerf.app.ScheduleFragment;
import nl.marijnvdwerf.widget.ScheduleAdapter;

public class DayScheduleFragment extends ScheduleFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getScheduleView().setAdapter(new DayScheduleAdapter(getActivity()));
    }

    private class Event implements ScheduleAdapter.ScheduleEvent {
        protected Date mStartDateTime;
        protected Date mEndDateTime;

        public Event(Date startDateTime, Date endDateTime) {
            mStartDateTime = startDateTime;
            mEndDateTime = endDateTime;
        }

        @Override
        public Date getStartDateTime() {
            return mStartDateTime;
        }

        @Override
        public Date getEndDateTime() {
            return mEndDateTime;
        }
    }

    private class DayScheduleAdapter implements ScheduleAdapter {
        Context mContext;
        LayoutInflater mInflater;

        public DayScheduleAdapter(Context context) {
            mContext = context.getApplicationContext();
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCountForDate(Date date) {
            return 5;
        }

        @Override
        public ScheduleEvent getEvent(int position) {
            return new Event(new Date(90, 7, 29, 10, 15, 0), new Date(90, 7, 29, 11, 5, 0));
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View v = mInflater.inflate(R.layout.item_event, viewGroup, false);
            return v;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
