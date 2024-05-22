package com.example.strider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.strider.customjourney.CustomJourney;
import com.example.strider.customjourney.DogJourney;
import com.example.strider.customjourney.PigJourney;
import com.example.strider.customjourney.VNJourney;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MyLocationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private GoogleMap myMap;
    private FusedLocationProviderClient fusedLocationClient;
    Spinner spinner;
    Polyline polyline = null;
    List<LatLng> latLngList = new ArrayList<>();
    CustomJourney customJourney = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.journey_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setEnabled(false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);  // Initialize the FusedLocationProviderClient

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_map);
        navView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.navigation_map) {
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_track) {
                startActivity(new Intent(getApplicationContext(), RecordJourney.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_goal) {
                startActivity(new Intent(getApplicationContext(), TaskActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_account) {
                startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            }
            return false;
        });


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        supportMapFragment.getMapAsync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_map);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        if (polyline != null) {
            polyline.remove();
        }
        latLngList.clear();
        switch (selectedItem) {
            case "Pig journey":
                if (customJourney != null)
                    customJourney.removePolyline();
                customJourney = new PigJourney(latLngList, myMap);
                break;
            case "Viet Nam Flag Journey":
                if (customJourney != null)
                    customJourney.removePolyline();
                customJourney = new VNJourney(latLngList, myMap);
                break;
            case "Dog Journey":
                if (customJourney != null)
                    customJourney.removePolyline();
                customJourney = new DogJourney(latLngList, myMap);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        spinner.setEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myMap.setMyLocationEnabled(true);
        myMap.getUiSettings().setMyLocationButtonEnabled(true);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        
        Task<Location> locationResult = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
        locationResult.addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location lastKnownLocation = task.getResult();
                LatLng mylocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 13.0f));

            } else {
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.0501, 105.7502), 13.0f));
            }
        });
    }
}
