package com.example.strider.customjourney;

import com.google.android.gms.maps.model.Polyline;

public class CustomJourney {
    protected Polyline polyline;
    public void removePolyline() {
        if (polyline != null)
            polyline.remove();
    }
}
