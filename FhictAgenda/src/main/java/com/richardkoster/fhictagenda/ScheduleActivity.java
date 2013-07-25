package com.richardkoster.fhictagenda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class ScheduleActivity extends Activity implements CalendarStrip.OnDateChangeListener {

    private CalendarStrip mCalendarStrip;
    private TextView mDateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        mDateView = (TextView) findViewById(R.id.date);

        mCalendarStrip = (CalendarStrip) findViewById(R.id.calendar_strip);
        mCalendarStrip.setOnDateChangeListener(this);
        mDateView.setText(mCalendarStrip.getCurrentDate().toString(DateTimeFormat.forPattern("d MMM")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_manage:
                Intent intent = new Intent(this, ScheduleManagementActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSelected(DateTime date) {
        mDateView.setText(date.toString(DateTimeFormat.forPattern("d MMM")));
    }
}
