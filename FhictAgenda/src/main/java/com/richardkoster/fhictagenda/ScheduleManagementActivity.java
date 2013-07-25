package com.richardkoster.fhictagenda;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.richardkoster.fhictagenda.adapters.ScheduleListAdapter;
import com.richardkoster.fhictagenda.api.objects.User;
import com.richardkoster.fhictagenda.application.CalendarApplication;

public class ScheduleManagementActivity extends ListActivity implements ActionMode.Callback, AdapterView.OnItemLongClickListener {

    CalendarApplication app;
    ListView listView;
    ScheduleListAdapter adapter;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_management);

        app = (CalendarApplication) getApplication();
        user = app.getUser();

        listView = getListView();
        listView.setLongClickable(true);
        listView.setAdapter(new ScheduleListAdapter(this, user.schedules));
        adapter = (ScheduleListAdapter) listView.getAdapter();
        listView.setOnItemLongClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule_management, menu);
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.schedule_management, menu);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        startActionMode(this);
        return true;
    }
}
