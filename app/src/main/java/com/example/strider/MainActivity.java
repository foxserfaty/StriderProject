package com.example.strider;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_GPS_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Getting location...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        requestLocationPermission();
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_GPS_CODE);
            } else {
                getLocation();
            }
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        try {
            Task<Location> locationResult = fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null);
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    dialog.hide();
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_GPS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
            }
        }
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

    public void onClickMyLocation(View v) {
        // go to the activity for displaying the user's location
        Intent myLocation = new Intent(MainActivity.this, MyLocationActivity.class);
        startActivity(myLocation);
    }

    public void onClickSpotify(View v) {
        // go to the activity for integrating with Spotify
        Intent spotify = new Intent(MainActivity.this, SpotifyActivity.class);
        startActivity(spotify);
    }
}
