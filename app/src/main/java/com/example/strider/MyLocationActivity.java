package com.example.strider;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.strider.ui.map.MapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class MyLocationActivity extends AppCompatActivity {
    private MapFragment myMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_WRITE_PERMISSION = 786;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        this.myMap = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);

        if (myMap == null) {
            myMap = new MapFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.map_fragment, myMap)
                    .commit();
        }

        fragmentManager.executePendingTransactions();

        Button startTrackButton = findViewById(R.id.start_track);
        Button stopTrackButton = findViewById(R.id.stop_track);
        startTrackButton.setEnabled(true);
        stopTrackButton.setEnabled(false);
        startTrackButton.setVisibility(View.VISIBLE);
        stopTrackButton.setVisibility(View.INVISIBLE);

        startTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMap != null) {
                    myMap.startLocationUpdatesFromActivity();
                    startTrackButton.setEnabled(false);
                    stopTrackButton.setEnabled(true);
                    startTrackButton.setVisibility(View.INVISIBLE);
                    stopTrackButton.setVisibility(View.VISIBLE);
                }
            }
        });

        stopTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMap != null) {
                    myMap.stopLocationUpdatesFromActivity();
                    startTrackButton.setEnabled(true);
                    stopTrackButton.setEnabled(false);
                    startTrackButton.setVisibility(View.VISIBLE);
                    stopTrackButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


}
