package com.example.strider.ui.map;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.strider.TrackingLocationService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private TrackingLocationService trackingLocationService;
    private boolean isBound = false;

    private List<LatLng> locationList = new ArrayList<>();

    private final LatLng defaultLocation = new LatLng(21.0501, 105.7502);
    private static final int DEFAULT_ZOOM = 16;
    private boolean myLocationFocus = false;

    private Location lastKnownLocation;
    LatLng mylocation;
    private Polyline polyline;
    private boolean startTracking = false;
    private final BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            mylocation = new LatLng(latitude, longitude);
            locationList.add(mylocation);
            drawPolyline(mylocation);
            if (!startTracking) {
                startTracking = true;
                map.clear();
                locationList.clear();
                locationList.add(mylocation);
                markLocation(mylocation);
            }
            if (myLocationFocus) {
                cameraFocus(mylocation);
            }
        }
    };

    public void setTrackingLocationService(TrackingLocationService service) {
        this.trackingLocationService = service;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TrackingLocationService.TrackingLocationServiceBinder binder = (TrackingLocationService.TrackingLocationServiceBinder) service;
            trackingLocationService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            trackingLocationService = null;
            isBound = false;
        }
    };


    private void cameraFocus(LatLng myLocation) {
        if (map != null) {
            float currentZoom = map.getCameraPosition().zoom;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, currentZoom));
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        Intent intent = new Intent(requireActivity(), TrackingLocationService.class);
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(locationReceiver, new IntentFilter("LocationUpdate"), Context.RECEIVER_EXPORTED);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(locationReceiver);
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap gmap) {
        this.map = gmap;
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        map.setOnMyLocationButtonClickListener(() -> {
            myLocationFocus = true;
            return false;
        });
        getDeviceLocation();

        map.setOnMyLocationButtonClickListener(() -> {
            myLocationFocus = true;
            return false;
        });
        map.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                myLocationFocus = false;
            }
        });
    }

    public MapFragment() {
        getMapAsync(this);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return true;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    public void getDeviceLocation() {
        if (map == null) return;
        try {
            Task<Location> locationResult = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
            locationResult.addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    lastKnownLocation = task.getResult();
                    mylocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, DEFAULT_ZOOM));
                } else {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void markLocation(LatLng myLocation) {
        if (map != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLocation);
            map.addMarker(markerOptions);
        }
    }

    public void startLocationUpdates() {
        if (trackingLocationService != null) {
            trackingLocationService.startLocationUpdates();
            myLocationFocus = true;
        }
    }

    public void stopLocationUpdates() {
        if (mylocation != null) {
            markLocation(mylocation);
            startTracking = false;
        }
        if (!locationList.isEmpty()) {
            locationList.clear();
        }
        if (polyline != null) {
            polyline = null;
        }
        myLocationFocus = false;

        if (trackingLocationService != null) {
            trackingLocationService.stopLocationUpdates();
        }
    }

    private void drawPolyline(LatLng latestLocation) {
        if (map != null && locationList.size() >= 2) {
            if (polyline != null) {
                List<LatLng> points = polyline.getPoints();
                points.add(latestLocation);
                polyline.setPoints(points);
            } else {
                PolylineOptions polylineOptions = new PolylineOptions()
                        .color(Color.parseColor("#016CC3"))
                        .width(12)
                        .addAll(locationList);
                polyline = map.addPolyline(polylineOptions);
            }
        }
    }

}
