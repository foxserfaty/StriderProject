package com.example.strider;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackingLocationService extends Service {

    private static final String TAG = "TrackingLocationService";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int DEFAULT_INTERVAL = 1000;
    double total = 0;
    int lastSize = 0;
    boolean recordLocations;
    private long startTime = 0;
    private long stopTime = 0;
    private boolean isJourneyStarted = false;

    ArrayList<Location> locations = new ArrayList<>();
    private final IBinder binder = new TrackingLocationServiceBinder();

    public class TrackingLocationServiceBinder extends Binder {
        public TrackingLocationService getService() {
            return TrackingLocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (!isJourneyStarted) {
                    playJourney();
                    isJourneyStarted = true;
                }
                List<Location> currentLocations = locationResult.getLocations();
                Location bestLocation = currentLocations.get(0);
                for (Location location : locationResult.getLocations()) {
                    if (location.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = location;
                    }
                }
                locations.add(bestLocation);
                Intent intent = new Intent("LocationUpdate");
                intent.putExtra("latitude", bestLocation.getLatitude());
                intent.putExtra("longitude", bestLocation.getLongitude());
                sendBroadcast(intent);
                Log.d(TAG, "Location updated " + getDistance());
                Log.d(TAG, "Time updated " + getDuration());
            }
        };
    }

    public void playJourney() {
        startTime = SystemClock.elapsedRealtime();
        stopTime = 0;
    }

   public double getDuration() {
        if (startTime == 0) {
            return 0.0;
        }
        long endTime = SystemClock.elapsedRealtime();

        if (stopTime != 0) {
            endTime = stopTime;
        }

        long elapsedMilliSeconds = endTime - startTime;
        return elapsedMilliSeconds / 1000.0;
    }

    public boolean currentlyTracking() {
        return startTime != 0;
    }

    private String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }

    public void startLocationUpdates() {
        createLocationCallback();
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, DEFAULT_INTERVAL).build();
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }
    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        saveJourney();
        newJourney();
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void newJourney() {
        locations.clear();
        stopTime = 0;
        startTime = 0;
        total=0;
        lastSize = 0;
        isJourneyStarted = false;
    }

    public double getDistance() {
        if (locations.size() < 2) {
            return 0;
        }
        if (lastSize < locations.size()) {
            total += locations.get(locations.size() - 2).distanceTo(locations.get(locations.size() - 1)) / 1000;
            lastSize = locations.size();
        }
        return total;
    }

    public void saveJourney() {
        ContentValues journeyData = new ContentValues();
        journeyData.put(JourneyProviderContract.J_DISTANCE, getDistance());
        journeyData.put(JourneyProviderContract.J_DURATION, (long) getDuration());
        journeyData.put(JourneyProviderContract.J_DATE, getDateTime());

        long journeyID = Long.parseLong(getContentResolver().insert(JourneyProviderContract.JOURNEY_URI, journeyData).getLastPathSegment());

        for (Location location : getLocations()) {
            ContentValues locationData = new ContentValues();
            locationData.put(JourneyProviderContract.L_JID, journeyID);
            locationData.put(JourneyProviderContract.L_ALTITUDE, location.getAltitude());
            locationData.put(JourneyProviderContract.L_LATITUDE, location.getLatitude());
            locationData.put(JourneyProviderContract.L_LONGITUDE, location.getLongitude());
            getContentResolver().insert(JourneyProviderContract.LOCATION_URI, locationData);
        }



        Log.d("mdp", "Journey saved with id = " + journeyID);
    }
}
