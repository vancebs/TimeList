package com.hf.timelist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hf.view.TimeListView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private TimeListView mListView;
    private int mItemMinHeight = 0;

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if (convertView == null) {
                view = new TextView(MainActivity.this);
            } else {
                view = (TextView) convertView;
            }

            view.setText("Item " + position);
            view.setMinHeight(mItemMinHeight);

            //////////////////////////////////////////
            // add date to view
            Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

            // make some different
            cal.roll(Calendar.DATE, position);

            // set date to view
            TimeListView.setDate(view, cal);
            //TimeListView.setDate(view, System.currentTimeMillis());

            return view;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (TimeListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);

        mItemMinHeight = getResources().getDimensionPixelSize(R.dimen.item_minHeight);
    }
}
