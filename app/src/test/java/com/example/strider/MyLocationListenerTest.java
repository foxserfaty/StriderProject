package com.example.strider;

import static org.junit.Assert.*;

import android.location.Location;

import org.junit.Test;

public class MyLocationListenerTest {

    private MyLocationListener locationListener = new MyLocationListener();

    @Test
    public void newJourney() {
        assertEquals(locationListener.getLocations().size(), 0);
    }

    @Test
    public void getDistanceOfJourney() {
        Location location1 = new Location("A");
        location1.setLatitude(17.372102);
        location1.setLongitude(78.484196);
        Location location2 = new Location("B");
        location1.setLatitude(17.375775);
        location1.setLongitude(78.469218);
        double distance = location1.distanceTo(location2) / 1000;
        assertEquals(locationListener.getLocations().size(), 0);

        locationListener.getLocations().add(location1);
        locationListener.getLocations().add(location2);
        assertEquals(locationListener.getLocations().size(), 2);
        assertEquals(distance, locationListener.getDistanceOfJourney(), 0.01);
    }
}