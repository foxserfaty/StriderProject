package com.example.strider;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.strider.customjourney.CustomJourney;
import com.example.strider.customjourney.DogJourney;
import com.example.strider.customjourney.PigJourney;
import com.example.strider.customjourney.VNJourney;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class CustomMapActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    GoogleMap mMap;
    Polyline polyline = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    Spinner spinner;
    CustomJourney customJourney = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_map);
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

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        spinner.setEnabled(true);
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
                customJourney = new PigJourney(latLngList, mMap);
                break;
            case "Viet Nam Flag Journey":
                if (customJourney != null)
                    customJourney.removePolyline();
                customJourney = new VNJourney(latLngList, mMap);
                break;
            case "Dog Journey":
                if (customJourney != null)
                    customJourney.removePolyline();
                customJourney = new DogJourney(latLngList, mMap);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
