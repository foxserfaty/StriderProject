package com.example.strider;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.strider.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private long journeyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        journeyID = bundle.getLong("journeyID");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // draw polyline
        Cursor c = getContentResolver().query(JourneyProviderContract.LOCATION_URI,
                null, JourneyProviderContract.L_JID + " = " + journeyID, null, null);

        PolylineOptions line = new PolylineOptions().clickable(false).color(Color.parseColor("#016CC3"))
                .width(12);;
        LatLng firstLoc = null;
        LatLng lastLoc = null;
        int index = 2990;
        String string = "";
        try {
            while(c.moveToNext()) {
                int latIndex = c.getColumnIndex(JourneyProviderContract.L_LATITUDE);
                int longIndex = c.getColumnIndex(JourneyProviderContract.L_LONGITUDE);
                LatLng loc = new LatLng(c.getDouble(latIndex),
                        c.getDouble(longIndex));
                if (index % 10 == 0) {
                    string += "LatLng location" + index + "= new LatLng(" + c.getDouble(latIndex) + "," + c.getDouble(longIndex) + ");";
                    string += "\n" + "latLngList.add(location" + index + ");" + "\n";
                }
                index++;
                if(c.isFirst()) {
                    firstLoc = loc;
                }
                if(c.isLast()) {
                    lastLoc = loc;
                }
                line.add(loc);
            }
        } finally {
            c.close();
        }

        Log.d("MyStr", string);

        float zoom = 15.0f;
        if(lastLoc != null && firstLoc != null) {
            mMap.addMarker(new MarkerOptions().position(firstLoc).title("Start"));
            mMap.addMarker(new MarkerOptions().position(lastLoc).title("End"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLoc, zoom));
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addPolyline(line);
    }
}