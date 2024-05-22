package com.example.strider;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_GPS_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    ProgressDialog dialog;

    private TextView dateText;
    private TextView noJourneyTextView;
    private DatePickerDialog.OnDateSetListener dateListener;

    private ListView journeyList;
    private JourneyAdapter adapter;
    private ArrayList<JourneyItem> journeyNames;

    /* Class to store all the information needed to display journey row item */
    private class JourneyItem {
        private String name;
        private String strUri;
        private long _id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setStrUri(String strUri) {
            this.strUri = strUri;
        }

        public String getStrUri() {
            return strUri;
        }

        public void set_id(long _id) {
            this._id = _id;
        }

        public long get_id() {
            return _id;
        }
    }

    /* ListView should display journey name along side a custom image uploaded by the user */
    private class JourneyAdapter extends ArrayAdapter<JourneyItem> {
        private ArrayList<JourneyItem> items;

        public JourneyAdapter(Context context, int textViewResourceId, ArrayList<JourneyItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.journeylist, null);
            }

            JourneyItem item = items.get(position);
            if (item != null) {
                TextView text = v.findViewById(R.id.singleJourney);
                ImageView img = v.findViewById(R.id.journeyList_journeyImg);
                if (text != null) {
                    text.setText(item.getName());
                }
                if (img != null) {
                    String strUri = item.getStrUri();
                    if (strUri != null) {
                        try {
                            final Uri imageUri = Uri.parse(strUri);
                            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            img.setImageBitmap(selectedImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        img.setImageDrawable(getResources().getDrawable(R.drawable.run));
                    }
                }
            }
            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);
        noJourneyTextView = findViewById(R.id.noJourneyTextView);
        navView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.navigation_home) {
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_map) {
                startActivity(new Intent(getApplicationContext(), MyLocationActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_track) {
                startActivity(new Intent(getApplicationContext(), RecordJourney.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_goal) {
                startActivity(new Intent(getApplicationContext(), TaskActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_account) {
                startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            }
            return false;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Getting location...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        requestLocationPermission();

        journeyNames = new ArrayList<>();
        adapter = new JourneyAdapter(this, R.layout.journeylist, journeyNames);

        // Initialize ListView and set the adapter
        journeyList = findViewById(R.id.listView); // Make sure this id matches your ListView id in XML
        journeyList.setAdapter(adapter);

        setUpDateDialogue();

        journeyList.setClickable(true);
        journeyList.setOnItemClickListener((arg0, arg1, position, arg3) -> {
            JourneyItem o = (JourneyItem) journeyList.getItemAtPosition(position);
            long journeyID = o.get_id();

            // start the single journey activity sending it the journeyID
            Bundle b = new Bundle();
            b.putLong("journeyID", journeyID);
            Intent singleJourney = new Intent(MainActivity.this, ViewSingleJourney.class);
            singleJourney.putExtras(b);
            startActivity(singleJourney);
        });

        setInitialDateToToday();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);
        String date = dateText.getText().toString();
        if (!date.toLowerCase().equals("select date")) {
            listJourneys(date);
        }
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_GPS_CODE);
            }
            else {
                getLocation();
            }
        }
        else {
            getLocation();
        }
    }

    private void getLocation() {
        try {
            Task<Location> locationResult = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
            locationResult.addOnCompleteListener(task -> dialog.hide());
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_GPS_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Strider needs your location", Toast.LENGTH_SHORT).show();
                requestLocationPermission();
            }
        }
    }



    private void setInitialDateToToday() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = sdf.format(calendar.getTime());
        dateText.setText(currentDate);
        listJourneys(currentDate);
    }

    private void setUpDateDialogue() {
        dateText = findViewById(R.id.selectDateText);
        journeyList = findViewById(R.id.listView); // Make sure this id matches your ListView id in XML

        dateText.setOnClickListener(view -> {
            int yyyy;
            int mm;
            int dd;

            // if first time selecting date choose current date, else last selected date
            if (dateText.getText().toString().toLowerCase().equals("select date")) {
                Calendar calendar = Calendar.getInstance();
                yyyy = calendar.get(Calendar.YEAR);
                mm = calendar.get(Calendar.MONTH);
                dd = calendar.get(Calendar.DAY_OF_MONTH);
            } else {
                String[] dateParts = dateText.getText().toString().split("/");
                yyyy = Integer.parseInt(dateParts[2]);
                mm = Integer.parseInt(dateParts[1]) - 1;
                dd = Integer.parseInt(dateParts[0]);
            }

            DatePickerDialog dialog = new DatePickerDialog(
                    MainActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateListener,
                    yyyy, mm, dd);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        dateListener = (datePicker, yyyy, mm, dd) -> {
            // user has selected a date on which to view journeys
            mm = mm + 1;
            String date;

            // format the date so its like dd/mm/yyyy
            if (mm < 10) {
                date = dd + "/0" + mm + "/" + yyyy;
            } else {
                date = dd + "/" + mm + "/" + yyyy;
            }

            if (dd < 10) {
                date = "0" + date;
            }

            dateText.setText(date);
            listJourneys(date);
        };
    }

    /* Query database to get all journeys in specified date in dd/mm/yyyy format and display them in listview */
    private void listJourneys(String date) {
        // sqlite server expects yyyy-mm-dd
        String[] dateParts = date.split("/");
        date = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

        Log.d("mdp", "Searching for date " + date);

        Cursor c = getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                new String[]{JourneyProviderContract.J_ID + " _id", JourneyProviderContract.J_NAME, JourneyProviderContract.J_IMAGE}, JourneyProviderContract.J_DATE + " = ?", new String[]{date}, JourneyProviderContract.J_NAME + " ASC");

        Log.d("mdp", "Journeys Loaded: " + c.getCount());

        // Clear previous journeyNames
        journeyNames.clear();

        if (c.getCount() == 0) {
            // Nếu không có hành trình nào được tìm thấy, ẩn ListView và hiển thị TextView
            journeyList.setVisibility(View.GONE);
            noJourneyTextView.setVisibility(View.VISIBLE);
        } else {
            // Nếu có hành trình được tìm thấy, hiển thị ListView và ẩn TextView
            journeyList.setVisibility(View.VISIBLE);
            noJourneyTextView.setVisibility(View.GONE);
        }

        // Add cursor items into ArrayList and add those items to the adapter
        try {
            while (c.moveToNext()) {
                JourneyItem i = new JourneyItem();
                int nameIndex = c.getColumnIndex(JourneyProviderContract.J_NAME);
                int imageIndex = c.getColumnIndex(JourneyProviderContract.J_IMAGE);
                int idIndex = c.getColumnIndex("_id");
                i.setName(c.getString(nameIndex));
                i.setStrUri(c.getString(imageIndex));
                i.set_id(c.getLong(idIndex));
                journeyNames.add(i);
            }
        } finally {
            c.close();
        }

        // Notify the adapter of data changes
        adapter.notifyDataSetChanged();
    }

}