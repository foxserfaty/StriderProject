package com.example.strider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.strider.ui.map.MapFragment;


public class MyLocationActivity extends AppCompatActivity {
    private MapFragment myMap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.fragment_map);
         FragmentManager fragmentManager = this.getSupportFragmentManager();
         this.myMap = (MapFragment) fragmentManager.findFragmentById(R.id.map_fragment);
    }

}
