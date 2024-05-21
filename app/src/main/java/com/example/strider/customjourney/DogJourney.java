package com.example.strider.customjourney;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class DogJourney extends CustomJourney {
    public DogJourney(List<LatLng> latLngList, GoogleMap mMap) {

        latLngList.clear();
        LatLng location2990= new LatLng(20.954441666666668,105.76729666666667);
        latLngList.add(location2990);
        LatLng location3000= new LatLng(20.95319,105.76842666666667);
        latLngList.add(location3000);
        LatLng location3010= new LatLng(20.95382,105.76929);
        latLngList.add(location3010);
        LatLng location3020= new LatLng(20.954813333333334,105.76991666666666);
        latLngList.add(location3020);
        LatLng location3030= new LatLng(20.954696666666667,105.770895);
        latLngList.add(location3030);
        LatLng location3040= new LatLng(20.953881666666668,105.77148333333334);
        latLngList.add(location3040);
        LatLng location3050= new LatLng(20.953191666666665,105.77181166666666);
        latLngList.add(location3050);
        LatLng location3060= new LatLng(20.953198333333333,105.77241666666667);
        latLngList.add(location3060);
        LatLng location3070= new LatLng(20.953616666666665,105.77255);
        latLngList.add(location3070);
        LatLng location3080= new LatLng(20.954125,105.77253166666667);
        latLngList.add(location3080);
        LatLng location3090= new LatLng(20.95446,105.77253);
        latLngList.add(location3090);
        LatLng location3100= new LatLng(20.95672,105.77237);
        latLngList.add(location3100);
        LatLng location3110= new LatLng(20.95851,105.77224);
        latLngList.add(location3110);
        LatLng location3120= new LatLng(20.96051,105.77295);
        latLngList.add(location3120);
        LatLng location3130= new LatLng(20.9617,105.77466);
        latLngList.add(location3130);
        LatLng location3140= new LatLng(20.96208,105.77531);
        latLngList.add(location3140);
        LatLng location3150= new LatLng(20.96234,105.77516);
        latLngList.add(location3150);
        LatLng location3160= new LatLng(20.963345,105.77328666666666);
        latLngList.add(location3160);
        LatLng location3170= new LatLng(20.962873333333334,105.77168);
        latLngList.add(location3170);
        LatLng location3180= new LatLng(20.96413,105.77056);
        latLngList.add(location3180);
        LatLng location3190= new LatLng(20.96531,105.76949);
        latLngList.add(location3190);
        LatLng location3200= new LatLng(20.965378333333334,105.76897666666666);
        latLngList.add(location3200);
        LatLng location3210= new LatLng(20.965005,105.76850333333333);
        latLngList.add(location3210);
        LatLng location3220= new LatLng(20.964638333333333,105.76804666666666);
        latLngList.add(location3220);
        LatLng location3230= new LatLng(20.9643,105.76762);
        latLngList.add(location3230);
        LatLng location3240= new LatLng(20.96395,105.76718166666667);
        latLngList.add(location3240);
        LatLng location3250= new LatLng(20.963588333333334,105.76674);
        latLngList.add(location3250);
        LatLng location3260= new LatLng(20.96325,105.76629);
        latLngList.add(location3260);
        LatLng location3270= new LatLng(20.9629,105.76581666666667);
        latLngList.add(location3270);
        LatLng location3280= new LatLng(20.962561666666666,105.765365);
        latLngList.add(location3280);
        LatLng location3290= new LatLng(20.962111666666665,105.765175);
        latLngList.add(location3290);
        LatLng location3300= new LatLng(20.961316666666665,105.76571666666666);
        latLngList.add(location3300);
        LatLng location3310= new LatLng(20.959713333333333,105.76715666666666);
        latLngList.add(location3310);
        LatLng location3320= new LatLng(20.957806666666666,105.76886333333333);
        latLngList.add(location3320);
        LatLng location3330= new LatLng(20.95656,105.768);
        latLngList.add(location3330);
        LatLng location3340= new LatLng(20.9572,105.76677);
        latLngList.add(location3340);
        LatLng location3350= new LatLng(20.95676,105.76588);
        latLngList.add(location3350);
        LatLng location3360= new LatLng(20.95708,105.76528);
        latLngList.add(location3360);
        LatLng location3370= new LatLng(20.95607,105.76521);
        latLngList.add(location3370);
        LatLng location3380= new LatLng(20.95508,105.76599);
        latLngList.add(location3380);
        LatLng location0= new LatLng(20.95480166666667,105.76699833333333);
        latLngList.add(location0);
        LatLng location10= new LatLng(20.953978333333332,105.76722166666667);
        latLngList.add(location10);

        PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngList).color(Color.parseColor("#016CC3"))
                .width(12);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        polyline = mMap.addPolyline(polylineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location2990, 13.0f));
    }

    @Override
    public void removePolyline() {
        super.removePolyline();
    }
}
