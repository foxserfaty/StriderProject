package com.example.strider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

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
