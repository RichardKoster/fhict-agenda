package com.richardkoster.fhictagenda;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ActivitiesFragment extends ListFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new ActivityAdapter(this.getActivity()));
    }

    public static class ActivityAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater mInflater;

        public ActivityAdapter(Context context) {
            mContext = context.getApplicationContext();
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return 15;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            View v = mInflater.inflate(R.layout.item_event, viewGroup, false);
            return v;
        }
    }
}
