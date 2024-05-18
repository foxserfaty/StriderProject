package com.example.strider;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MyLocationListener implements LocationListener {
    ArrayList<Location> locations;
    boolean recordLocations;
    float total = 0;
    int lastSize = 0;

    public MyLocationListener() {
        newJourney();
        recordLocations = false;
    }

    public void newJourney() {
        locations = new ArrayList<Location>();
    }

    public float getDistanceOfJourney() {
        if(locations.size() < 2) {
            return 0;
        }

        if (lastSize < locations.size()) {
            total += locations.get(locations.size() - 2).distanceTo(locations.get(locations.size() - 1)) / 1000;
            lastSize = locations.size();
        }
        return total;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }


    @Override
    public void onLocationChanged(Location location) {
        // called when the location is changed. Can obtain latitude, longitude, altitude.
        // use location.distanceTo(otherLocation) to get a distance between two locations
        if(recordLocations) {
            locations.add(location);
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // information about the signal, i.e. number of satellites
        Log.d("mdp", "onStatusChanged: " + provider + " " + status);
    }
    @Override
    public void onProviderEnabled(String provider) {
        // the user enabled (for example) the GPS
        Log.d("mdp", "onProviderEnabled: " + provider);
    }
    @Override
    public void onProviderDisabled(String provider) {
        // the user disabled (for example) the GPS
        Log.d("mdp", "onProviderDisabled: " + provider);
    }
}
