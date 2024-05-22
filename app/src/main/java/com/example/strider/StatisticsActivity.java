package com.example.strider;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StatisticsActivity extends AppCompatActivity {

    private BarChart barChart;
    private TextView recordDistance;
    private TextView distanceToday;
    private TextView timeToday;
    private TextView distanceAllTime;
    private TextView dateText;

    private DatePickerDialog.OnDateSetListener dateListener;
    private Handler postBack = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_account);
        navView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.navigation_account) {
                return true;
            }
            else if (menuItem.getItemId() == R.id.navigation_map) {
                startActivity(new Intent(getApplicationContext(), MyLocationActivity.class));
                overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
                navView.setSelectedItemId(R.id.navigation_home);
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
            else if (menuItem.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(R.anim.slide_right,R.anim.slide_left);
                return true;
            }
            return false;
        });

        recordDistance = findViewById(R.id.Statistics_recordDistance);
        distanceToday = findViewById(R.id.Statistics_distanceToday);
        timeToday = findViewById(R.id.Statistics_timeToday);
        distanceAllTime = findViewById(R.id.Statistics_distanceAllTime);
        dateText = findViewById(R.id.Statistics_selectDate);
        barChart = findViewById(R.id.barchart);

        setUpDateDialogue();
        setInitialDateToToday();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_account);
    }

    private void setInitialDateToToday() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = sdf.format(calendar.getTime());
        dateText.setText(currentDate);
        displayStatsOnDate(currentDate);
    }

    private void setUpDateDialogue() {
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
                    StatisticsActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dateListener,
                    yyyy, mm, dd);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        dateListener = (datePicker, yyyy, mm, dd) -> {
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
            displayStatsOnDate(date);
        };
    }

    private void displayStatsOnDate(String d) {
        final String[] dateParts = d.split("/");
        final String date = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

        new Thread(() -> {
            Cursor c = getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                    null, JourneyProviderContract.J_DATE + " = ?", new String[]{date}, null);
            double distanceTodayKM = 0;
            long timeTodayS = 0;
            try {
                while (c.moveToNext()) {
                    int distanceIndex = c.getColumnIndex(JourneyProviderContract.J_DISTANCE);
                    int durationIndex = c.getColumnIndex(JourneyProviderContract.J_DURATION);
                    distanceTodayKM += c.getDouble(distanceIndex);
                    timeTodayS += c.getLong(durationIndex);
                }
            } finally {
                c.close();
            }

            final long hours = timeTodayS / 3600;
            final long minutes = (timeTodayS % 3600) / 60;
            final long seconds = timeTodayS % 60;
            final double distanceTodayKM_ = distanceTodayKM;

            c = getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                    null, null, null, null);
            double totalDistanceKM = 0;
            double recordDistanceKM = 0;
            try {
                while (c.moveToNext()) {
                    int distanceIndex = c.getColumnIndex(JourneyProviderContract.J_DISTANCE);
                    double d1 = c.getDouble(distanceIndex);
                    if (recordDistanceKM < d1) {
                        recordDistanceKM = d1;
                    }
                    totalDistanceKM += d1;
                }
            } finally {
                c.close();
            }

            final double totalDistanceKM_ = totalDistanceKM;
            final double recordDistanceKM_ = recordDistanceKM;

            ArrayList<BarEntry> entries = new ArrayList<>();
            float[] distancesOnDays = {0, 0, 0, 0, 0, 0, 0};
            try {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                cal.setTime(sdf.parse(date));
                cal.setFirstDayOfWeek(Calendar.MONDAY);

                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                String mon = sdf.format(cal.getTime());
                for (int i = 0; i < 6; i++) {
                    cal.add(Calendar.DATE, 1);
                }
                String sun = sdf.format(cal.getTime());

                Log.d("mdp", "Mon = " + mon + ", Sun = " + sun);
                c = getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                        null, JourneyProviderContract.J_DATE + " BETWEEN ? AND ?",
                        new String[]{mon, sun}, null);
                try {
                    for (int i = 0; c.moveToNext(); i++) {
                        int dateIndex = c.getColumnIndex(JourneyProviderContract.J_DATE);
                        int distanceIndex = c.getColumnIndex(JourneyProviderContract.J_DISTANCE);
                        String date1 = c.getString(dateIndex);
                        Log.d(TAG, date1);
                        cal = Calendar.getInstance();
                        cal.setTime(sdf.parse(date1));
                        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                        distancesOnDays[dayOfWeek - 1] += (float) c.getDouble(distanceIndex);
                    }
                } finally {
                    c.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 1; i < distancesOnDays.length; i++) {
                entries.add(new BarEntry(i - 1, distancesOnDays[i]));
            }
            entries.add(new BarEntry(distancesOnDays.length - 1, distancesOnDays[0]));

            postBack.post(() -> {
                distanceToday.setText(String.format("%.2f KM", distanceTodayKM_));
                timeToday.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                recordDistance.setText(String.format("%.2f KM", recordDistanceKM_));
                distanceAllTime.setText(String.format("%.2f KM", totalDistanceKM_));
                loadBarChart(entries);
            });
        }).start();
    }

    private void loadBarChart(ArrayList<BarEntry> entries) {
        BarDataSet bardataset = new BarDataSet(entries, "Days");

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Mon");
        labels.add("Tue");
        labels.add("Wed");
        labels.add("Thu");
        labels.add("Fri");
        labels.add("Sat");
        labels.add("Sun");

        BarData data = new BarData(bardataset);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getDescription().setText("Distance (KM)");
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.animateY(3000);
    }
}
