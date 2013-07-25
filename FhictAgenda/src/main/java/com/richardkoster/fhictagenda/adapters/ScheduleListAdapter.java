package com.richardkoster.fhictagenda.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.richardkoster.fhictagenda.R;
import com.richardkoster.fhictagenda.api.objects.Schedule;
import com.richardkoster.fhictagenda.views.ScheduleListItemView;

import java.util.List;

/**
 * Created by richard on 7/19/13.
 */
public class ScheduleListAdapter extends ArrayAdapter<Schedule> {

    private List<Schedule> schedules;
    private Context context;
    private ScheduleListItemView mScheduleListItemView;

    public ScheduleListAdapter(Context context, List<Schedule> schedules) {
        super(context, R.layout.activity_schedule_management, schedules);
        this.schedules = schedules;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mScheduleListItemView = (ScheduleListItemView) convertView;
        if (mScheduleListItemView == null) {
            mScheduleListItemView = new ScheduleListItemView(context);
        }

        mScheduleListItemView.setScheduleProperties(schedules.get(position));
        return mScheduleListItemView;
    }
}
