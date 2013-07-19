package com.richardkoster.fhictagenda.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.richardkoster.fhictagenda.R;
import com.richardkoster.fhictagenda.api.objects.Schedule;

/**
 * Created by richard on 7/19/13.
 */
public class ScheduleListItemView extends LinearLayout {

    public ScheduleListItemView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem_schedulemanagement, this);
    }

    public void setScheduleProperties(Schedule schedule) {
        TextView scheduleNameTextView = (TextView) findViewById(R.id.listitem_schedulemanagement_schedulename);
        scheduleNameTextView.setText(schedule.itemName);
    }
}
