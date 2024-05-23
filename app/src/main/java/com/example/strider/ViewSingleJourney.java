package com.example.strider;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class ViewSingleJourney extends AppCompatActivity {

    private TextView distanceTV;
    private TextView avgSpeedTV;
    private TextView timeTV;
    private TextView dateTV;
    private TextView ratingTV;
    private TextView titleTV;
    private long journeyID;

    private Handler h = new Handler();

    // observer is notified when insert or delete in the given URI occurs
    protected class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // called when something in the database wrapped by the content provider changes
            // update the view
            populateView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_journey);

        Bundle bundle = getIntent().getExtras();

        distanceTV = findViewById(R.id.Statistics_recordDistance);
        avgSpeedTV = findViewById(R.id.Statistics_distanceToday);
        timeTV     = findViewById(R.id.Statistics_timeToday);
        dateTV     = findViewById(R.id.ViewSingleJourney_dateText);
        ratingTV   = findViewById(R.id.ViewSingleJourney_ratingText);
        titleTV    = findViewById(R.id.ViewSingleJourney_titleText);
        journeyID  = bundle.getLong("journeyID");

        populateView();
        getContentResolver().registerContentObserver(
                JourneyProviderContract.ALL_URI, true, new MyObserver(h));
    }

    public void onClickEdit(View v) {
        // take to activity for editing the fields for this single journey
        Intent editActivity = new Intent(ViewSingleJourney.this, EditJourney.class);
        Bundle b = new Bundle();
        b.putLong("journeyID", journeyID);
        editActivity.putExtras(b);
        startActivity(editActivity);
    }

    public void onClickMap(View v) {
        // display this journey on a google map activity
        Intent map = new Intent(ViewSingleJourney.this, MapsActivity.class);
        Bundle b = new Bundle();
        b.putLong("journeyID", journeyID);
        map.putExtras(b);
        startActivity(map);
    }

    public void onClickDelete(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Journey")
                .setMessage("Are you sure you want to delete this journey?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri journeyUri = Uri.withAppendedPath(JourneyProviderContract.JOURNEY_URI, String.valueOf(journeyID));
                        int rowsDeleted = getContentResolver().delete(journeyUri, null, null);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void populateView() {
        // use content provider to load data from the database and display on the text views
        Cursor c = getContentResolver().query(Uri.withAppendedPath(JourneyProviderContract.JOURNEY_URI,
                journeyID + ""), null, null, null, null);

        if(c.moveToFirst()) {
            int distanceIndex = c.getColumnIndex(JourneyProviderContract.J_DISTANCE);
            int durationIndex = c.getColumnIndex(JourneyProviderContract.J_DURATION);
            double distance = c.getDouble(distanceIndex);
            long time       = c.getLong(durationIndex);
            double avgSpeed = 0;

            if(time != 0) {
                avgSpeed = distance / (time / 3600.0);
            }

            long hours = time / 3600;
            long minutes = (time % 3600) / 60;
            long seconds = time % 60;

            distanceTV.setText(String.format("%.2f KM", distance));
            avgSpeedTV.setText(String.format("%.2f KM/H", avgSpeed));
            timeTV.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            int dateIndex = c.getColumnIndex(JourneyProviderContract.J_DATE);
            int ratingIndex = c.getColumnIndex(JourneyProviderContract.J_RATING);
            int nameIndex = c.getColumnIndex(JourneyProviderContract.J_NAME);
            int imageIndex = c.getColumnIndex(JourneyProviderContract.J_IMAGE);

            // date will be stored as yyyy-mm-dd, convert to dd-mm-yyyy
            String date = c.getString(dateIndex);
            String[] dateParts = date.split("-");
            date = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];

            dateTV.setText(date);
            ratingTV.setText(c.getInt(ratingIndex) + "");
            titleTV.setText(c.getString(nameIndex));

            // if an image has been set by user display it, else default image is displayed
            String strUri = c.getString(imageIndex);
            if(strUri != null) {
                try {
                    final Uri imageUri = Uri.parse(strUri);
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
