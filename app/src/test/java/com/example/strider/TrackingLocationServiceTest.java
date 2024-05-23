package com.example.strider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;

import android.location.Location;
import android.os.SystemClock;

import com.example.strider.TrackingLocationService;

public class TrackingLocationServiceTest {

    private TrackingLocationService trackingLocationService;

    @Before
    public void setUp() {
        trackingLocationService = new TrackingLocationService();
    }

    @After
    public void tearDown() {
        trackingLocationService = null;
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
        assertEquals(trackingLocationService.getLocations().size(), 0);

        trackingLocationService.getLocations().add(location1);
        trackingLocationService.getLocations().add(location2);
        assertEquals(trackingLocationService.getLocations().size(), 2);
        assertEquals(distance, trackingLocationService.getDistance(), 0.01);
    }
    @Test
    public void newJourney() {
        assertEquals(trackingLocationService.getLocations().size(), 0);
    }



}
