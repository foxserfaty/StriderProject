package com.example.strider.ui.map;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private List<LatLng> locationList = new ArrayList<>();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final LatLng defaultLocation = new LatLng(21.0501, 105.7502);
    private static final int DEFAULT_ZOOM = 16;
    private static final int DEFAULT_INTERVAL = 5000;
    private boolean locationPermissionGranted;
    private boolean myLocationFocus = false;

    private Location lastKnownLocation;
    LatLng mylocation;
    private Polyline polyline;
    private boolean startTracking = false;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locations = locationResult.getLocations();
            Location bestLocation = locations.get(0);
            for (Location location : locations) {
                if (location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            }
            mylocation = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
            locationList.add(mylocation);
            drawPolyline(locationList.get(locationList.size() - 1));
            printCurrentLocation();
            if (!startTracking) {
                startTracking = true;
                map.clear();
                locationList.clear();
                locationList.add(mylocation);
                if (polyline != null) {
                    polyline.remove();
                    polyline = null;
                }
                markLocation(mylocation, "Start");
            }
            if (myLocationFocus) {
                cameraFocus(bestLocation);
            }
        }
    };

    private void cameraFocus(Location location) {
        if (map != null) {
            float currentZoom = map.getCameraPosition().zoom;
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, currentZoom));
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap gmap) {
        this.map = gmap;
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        getDeviceLocation();

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, DEFAULT_INTERVAL).build();
        map.setOnMyLocationButtonClickListener(() -> {
            myLocationFocus = true;
            Log.d(TAG, "my location");
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
            Task<Location> locationResult = fusedLocationClient.getLastLocation();
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

    public void markLocation(LatLng myLocation, String title) {
        if (map != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLocation);
            markerOptions.title(title);
            map.addMarker(markerOptions);
        }
    }

    private void startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e(TAG, "Exception: " + e.getMessage(), e);
        }
    }

    private void printCurrentLocation() {
        if (mylocation != null) {
            double latitude = mylocation.latitude;
            double longitude = mylocation.longitude;
            Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
        } else {
            Log.d(TAG, "Current location is not available.");
        }
    }

    private void stopLocationUpdates() {
        if (mylocation != null) {
            markLocation(mylocation, "End");
            startTracking = false;
        }
        if (!locationList.isEmpty()) {
            locationList.clear();
        }
        fusedLocationClient.removeLocationUpdates(locationCallback);
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

    public void startLocationUpdatesFromActivity() {
        startLocationUpdates();
    }

    public void stopLocationUpdatesFromActivity() {
        stopLocationUpdates();
    }
}
