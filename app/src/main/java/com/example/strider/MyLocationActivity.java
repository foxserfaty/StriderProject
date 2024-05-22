package com.example.strider;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyLocationActivity extends AppCompatActivity {
    private MapFragment myMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_map);
        navView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.navigation_map) {
                return true;
            }
            else if (menuItem.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
                return true;
            }
            else if (menuItem.getItemId() == R.id.navigation_track) {
                startActivity(new Intent(getApplicationContext(), RecordJourney.class));
                overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
                return true;
            }
            else if (menuItem.getItemId() == R.id.navigation_goal) {
                startActivity(new Intent(getApplicationContext(), MyLocationActivity.class));
                overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
                return true;
            }
            else if (menuItem.getItemId() == R.id.navigation_account) {
                startActivity(new Intent(getApplicationContext(),StatisticsActivity.class));
                overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
                return true;
            }
            return false;
        });

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        this.myMap = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
        if (myMap == null) {
            myMap = new MapFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.map_fragment, myMap)
                    .commit();
        }
        fragmentManager.executePendingTransactions();



    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_map);
    }
}
