package com.example.strider;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.strider.R;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TaskActivity extends AppCompatActivity {
    Context context;
    String filename = "task.txt";
    TextView kmTaskTV, timeTaskTV, kmSuccess, timeSuccess;
    Button editButton;
    EditText enterTime, enterKm;
    String[] goals, tasks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        kmTaskTV = (TextView) findViewById(R.id.kmTaskTV);
        timeTaskTV = (TextView) findViewById(R.id.timeTaskTV);
        editButton = (Button) findViewById(R.id.editButton);
        enterTime = (EditText) findViewById(R.id.enterTime);
        enterKm = (EditText) findViewById(R.id.enterKm);
        timeSuccess = (TextView) findViewById(R.id.timeSuccess);
        kmSuccess = (TextView) findViewById(R.id.kmSuccess);

        context = getApplicationContext();

        goals = readFileFromInternalStorage().split(",");
        tasks = getTodayStat().split(",");

        if (tasks.length >= 2) {
            kmTaskTV.setText(tasks[0] + "/" + goals[0]);
            timeTaskTV.setText(tasks[1] + "/" + goals[1]);
            checkSuccess();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.navigation_goal) {
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_map) {
                startActivity(new Intent(getApplicationContext(), MyLocationActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_track) {
                startActivity(new Intent(getApplicationContext(), RecordJourney.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            } else if (menuItem.getItemId() == R.id.navigation_account) {
                startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_goal);
    }

    private void checkSuccess() {
        if(Double.parseDouble(tasks[0]) >= Double.parseDouble(goals[0])) {
            kmSuccess.setText("Success");
        } else {
            kmSuccess.setText("Ongoing");
        }

        if(Double.parseDouble(tasks[1]) >= Double.parseDouble(goals[1])) {
            timeSuccess.setText("Success");
        } else {
            timeSuccess.setText("Ongoing");
        }
    }

    public String getTodayStat() {
        String stat = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = sdf.format(calendar.getTime());

        String[] dateParts = currentDate.split("/");
        String date = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];

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

        long hours = timeTodayS / 3600;
        long minutes = (timeTodayS % 3600) / 60;
        double doubleTime = (double) minutes / 60 + hours;

        double distance = (int)(Math.round(distanceTodayKM * 100))/100.0;
        double time = (int)(Math.round(doubleTime * 100))/100.0;

        return distance + "," + time;
    }

    public void editText(View view) {
        try {
            Double test = Double.parseDouble(String.valueOf(enterKm.getText()));
            test = Double.parseDouble(String.valueOf(enterTime.getText()));
        } catch (NumberFormatException ignore) {
            Log.e("Wrong edit format", "Need to be a double value");
            return;
        }

        goals[0] = String.valueOf(enterKm.getText());
        goals[1] = String.valueOf(enterTime.getText());

        enterKm.setText("");
        enterTime.setText("");

        writeFileToInternalStorage(goals[0] + "," + goals[1]);
        kmTaskTV.setText(tasks[0] + "/" + goals[0]);
        timeTaskTV.setText(tasks[1] + "/" + goals[1]);

        checkSuccess();
    }

    public void writeFileToInternalStorage(String str){
        File file  = new File(context.getFilesDir(), filename);
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(str);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String readFileFromInternalStorage() {
        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            return bufferedReader.readLine();
        } catch (FileNotFoundException e) {
            writeFileToInternalStorage("15,15");
            return "15,15";
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
