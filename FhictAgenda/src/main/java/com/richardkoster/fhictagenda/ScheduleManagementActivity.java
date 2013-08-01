package com.richardkoster.fhictagenda;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.richardkoster.fhictagenda.adapters.ScheduleListAdapter;
import com.richardkoster.fhictagenda.api.FhictClient;
import com.richardkoster.fhictagenda.api.objects.Schedule;
import com.richardkoster.fhictagenda.api.objects.User;
import com.richardkoster.fhictagenda.application.CalendarApplication;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ScheduleManagementActivity extends ListActivity implements ActionMode.Callback, AdapterView.OnItemLongClickListener, ColorPickerActionProvider.OnColorSelectedListener {

    CalendarApplication app;
    ListView listView;
    ScheduleListAdapter adapter;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        app = (CalendarApplication) getApplication();
        user = app.getUser();

        listView = getListView();
        listView.setLongClickable(true);
        listView.setAdapter(new ScheduleListAdapter(this, user.schedules));
        adapter = (ScheduleListAdapter) listView.getAdapter();
        listView.setOnItemLongClickListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule, menu);
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.cab_schedulemanagement, menu);
        ColorPickerActionProvider colorpicker = (ColorPickerActionProvider) menu.findItem(R.id.cab_schedulemanagement_colorpicker).getActionProvider();
        colorpicker.setOnColorSelectedListener(this);
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
        listView.setItemChecked(i,true);
        return true;
    }

    @Override
    public void onColorSelected(String color) {
        Schedule s = user.schedules.get(listView.getCheckedItemPosition());
        FhictClient.getApi(app.getToken()).updateScheduleColor(s.id, color, new Callback<Schedule>() {
            @Override
            public void success(Schedule schedule, Response response) {
                Toast.makeText(ScheduleManagementActivity.this, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(ScheduleManagementActivity.this, "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
