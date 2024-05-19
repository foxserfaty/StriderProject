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
        if(recordLocations) {
            locations.add(location);
        }
    }

}
