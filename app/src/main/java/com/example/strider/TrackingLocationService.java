package com.example.strider;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;

public class TrackingLocationService extends Service {

    private static final String TAG = "TrackingLocationService";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int DEFAULT_INTERVAL = 2000;
    private final IBinder binder = new LocalBinder();
    private Location currentLocation;

    public class LocalBinder extends Binder {
        public TrackingLocationService getService() {
            return TrackingLocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                super.onLocationResult(locationResult);
                List<Location> locations = locationResult.getLocations();
                Location bestLocation = locations.get(0);
                for (Location location : locationResult.getLocations()) {
                    if (location.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = location;
                    }
                }
                currentLocation = bestLocation;
                Intent intent = new Intent("LocationUpdate");
                intent.putExtra("latitude", bestLocation.getLatitude());
                intent.putExtra("longitude", bestLocation.getLongitude());
                sendBroadcast(intent);
                Log.d(TAG, "Location updated");
            }
        };
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, DEFAULT_INTERVAL).build();
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}
