package com.example.strider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_GPS_CODE = 1;
    private static final int PERMISSION_COAL_GPS_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickRecord(View v) {
        // go to the record journey activity
        Intent journey = new Intent(MainActivity.this, RecordJourney.class);
        startActivity(journey);
    }

    public void onClickView(View v) {
        // go to the activity for displaying journeys
        Intent view = new Intent(MainActivity.this, ViewJourneys.class);
        startActivity(view);
    }

    public void onClickStatistics(View v) {
        // go to the activity for displaying statistics
        Intent stats = new Intent(MainActivity.this, StatisticsActivity.class);
        startActivity(stats);
    }
}