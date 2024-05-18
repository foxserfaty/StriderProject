package com.example.strider;

import android.os.Bundle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SpotifyActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "8eca3f3f02134dedafa2976f91806396";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "https://github.com/foxserfaty/StriderProject";
    private static final String PLAYLIST_URI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL";

    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        // Create the authorization request
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();

        // Open the login activity for Spotify authorization
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

        Button playPlaylistButton = findViewById(R.id.playMusicButton);

        // Set OnClickListener for the button
        playPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if Spotify app is installed
                if (isSpotifyInstalled()) {
                    // Open Spotify app
                    openSpotify();
                } else {
                    Log.e("MainActivity", "Spotify app is not installed.");
                    // Handle case where Spotify app is not installed
                }
            }
        });
    }

    private boolean isSpotifyInstalled() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.spotify.music", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void openSpotify() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.spotify.music");
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Log.e("MainActivity", "Error opening Spotify app.");
            // Handle error opening Spotify app
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connection setup moved to onStart()


    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect from Spotify on activity stop
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play(PLAYLIST_URI);
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
    }
}