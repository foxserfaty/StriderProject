package com.example.strider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.strider.ui.map.MapFragment;


public class MyLocationActivity extends AppCompatActivity {
    private MapFragment myMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.fragment_map);
         FragmentManager fragmentManager = this.getSupportFragmentManager();
         this.myMap = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
         myMap.getDeviceLocation();
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
