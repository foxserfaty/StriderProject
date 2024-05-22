package com.example.strider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class RecordJourney extends AppCompatActivity {

    private TrackingLocationService locationService;
    private TextView distanceText;
    private TextView avgSpeedText;
    private TextView durationText;
    private Button playButton;
    private Button stopButton;
    private ImageButton statButton;
    private ImageButton spotifyButton;
    private MapFragment myMap;
    private LinearLayout statLayout;
    private static final int PERMISSION_GPS_CODE = 1;

    // will poll the location service for distance and duration
    private Handler postBack = new Handler();

    private ServiceConnection lsc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TrackingLocationService.TrackingLocationServiceBinder binder = (TrackingLocationService.TrackingLocationServiceBinder) service;
            locationService = binder.getService();
            myMap.setTrackingLocationService(locationService);
            // if currently tracking then enable stopButton and disable startButton
            initButtons();
            new Thread(() -> {

                while (locationService != null) {
                    // get the distance and duration from the surface
                    double d = locationService.getDuration();
                    long duration = (long) d;  // in seconds
                    double distance = locationService.getDistance();
                    long hours = duration / 3600;
                    long minutes = (duration % 3600) / 60;
                    long seconds = duration % 60;

                    double avgSpeed = 0;
                    if (d != 0) {
                        avgSpeed = distance / (d / 3600);
                    }

                    final String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    final String dist = String.format("%.2f KM", distance);
                    final String avgs = String.format("%.2f KM/H", avgSpeed);

                    postBack.post(new Runnable() {
                        @Override
                        public void run() {
                            // post back changes to UI thread
                            durationText.setText(time);
                            avgSpeedText.setText(avgs);
                            distanceText.setText(dist);
                        }
                    });

                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
        }
    };


    // whenever activity is reloaded while still tracking a journey (if back button is clicked for example)
    private void initButtons() {
        // no permissions means no buttons
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            return;
        }

        // if currently tracking then enable stopButton and disable startButton
        if (locationService != null && locationService.currentlyTracking()) {
            stopButton.setEnabled(true);
            playButton.setEnabled(false);
            playButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);
        } else {
            stopButton.setEnabled(false);
            playButton.setEnabled(true);
            playButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_journey);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        this.myMap = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
        if (myMap == null) {
            myMap = new MapFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.map_fragment, myMap)
                    .commit();
        }
        distanceText = findViewById(R.id.distanceText);
        durationText = findViewById(R.id.durationText);
        avgSpeedText = findViewById(R.id.avgSpeedText);

        playButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        statButton = findViewById(R.id.statButton);
        spotifyButton = findViewById(R.id.spotify_button);
        statLayout = findViewById(R.id.statLayout);
        statLayout.setVisibility(View.GONE);
        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (statLayout.getVisibility() == View.VISIBLE) {
                    Animation slideLeftAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
                    statLayout.startAnimation(slideLeftAnimation);
                    statLayout.setVisibility(View.GONE);
                } else {
                    Animation slideRightAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
                    statLayout.startAnimation(slideRightAnimation);
                    statLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        spotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent spotify = new Intent(RecordJourney.this, SpotifyActivity.class);
                startActivity(spotify);
            }
        });
        // connect to service to see if currently tracking before enabling a button
        stopButton.setEnabled(false);
        playButton.setEnabled(false);
        handlePermissions();
        // start the service so that it persists outside of the lifetime of this activity
        // and also bind to it to gain control over the service
        startService(new Intent(this, TrackingLocationService.class));
        bindService(
                new Intent(this, TrackingLocationService.class), lsc, Context.BIND_AUTO_CREATE);
    }

    public void onClickPlay(View view) {
        if (myMap != null) {
            myMap.startLocationUpdates();
            Animation slideRightAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
            statLayout.startAnimation(slideRightAnimation);
            statLayout.setVisibility(View.VISIBLE);

            playButton.setEnabled(false);
            stopButton.setEnabled(true);
            playButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);
        }
    }

    public void onClickStop(View view) {
        // save the current journey to the database
        double distance = locationService.getDistance();

        myMap.stopLocationUpdates();

        playButton.setEnabled(true);
        stopButton.setEnabled(false);
        playButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.GONE);


        DialogFragment modal = FinishedTrackingDialogue.newInstance(String.format("%.2f KM", distance));
        modal.show(getSupportFragmentManager(), "Finished");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // unbind to the service (if we are the only binding activity then the service will be destroyed)
        if (lsc != null) {
            unbindService(lsc);
            lsc = null;
        }
    }

    public static class FinishedTrackingDialogue extends DialogFragment {
        public static FinishedTrackingDialogue newInstance(String distance) {
            Bundle savedInstanceState = new Bundle();
            savedInstanceState.putString("distance", distance);
            FinishedTrackingDialogue frag = new FinishedTrackingDialogue();
            frag.setArguments(savedInstanceState);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your Journey has been saved. You ran a total of " + getArguments().getString("distance") + " KM")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // go back to home screen
                            getActivity().finish();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


    // PERMISSION THINGS

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(reqCode, permissions, results);
        switch (reqCode) {
            case PERMISSION_GPS_CODE:
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    initButtons();
                } else {
                    // permission denied, disable GPS tracking buttons
                    stopButton.setEnabled(false);
                    playButton.setEnabled(false);
                }
        }
    }


    public static class NoPermissionDialogue extends DialogFragment {
        public static NoPermissionDialogue newInstance() {
            return new NoPermissionDialogue();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("GPS is required to track your journey!")
                    .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // user agreed to enable GPS
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {
                        // dialogue was cancelled
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private void handlePermissions() {
        // if don't have GPS permissions then request this permission from the user.
        // if not granted the permission disable the start button
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // the user has already declined request to allow GPS
                // give them a pop up explaining why its needed and re-ask
                DialogFragment modal = NoPermissionDialogue.newInstance();
                modal.show(getSupportFragmentManager(), "Permissions");
            } else {
                // request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_CODE);
            }
        }
    }
}
