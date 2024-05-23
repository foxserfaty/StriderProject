package com.example.strider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.InputStream;

public class EditJourney extends AppCompatActivity {
    private final int RESULT_LOAD_IMG = 1;

    private ImageView journeyImg;
    EditText titleET;
    EditText commentET;
    Spinner ratingSpinner;
    private long journeyID;

    private Uri selectedJourneyImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey);

        Bundle bundle = getIntent().getExtras();

        journeyImg = findViewById(R.id.journeyImg);
        titleET = findViewById(R.id.titleEditText);
        commentET = findViewById(R.id.commentEditText);
        ratingSpinner = findViewById(R.id.ratingSpinner);

        if (bundle != null) {
            journeyID = bundle.getLong("journeyID");
        } else {
            finish();
        }

        selectedJourneyImg = null;
        populateEditText();
    }

    public void onClickSave(View v) {
        int rating = ratingSpinner.getSelectedItemPosition() + 1;

        Uri rowQueryUri = Uri.withAppendedPath(JourneyProviderContract.JOURNEY_URI, "" + journeyID);

        ContentValues cv = new ContentValues();
        cv.put(JourneyProviderContract.J_RATING, rating);
        cv.put(JourneyProviderContract.J_COMMENT, commentET.getText().toString());
        cv.put(JourneyProviderContract.J_NAME, titleET.getText().toString());

        if (selectedJourneyImg != null) {
            cv.put(JourneyProviderContract.J_IMAGE, selectedJourneyImg.toString());
        }

        getContentResolver().update(rowQueryUri, cv, null, null);
        finish();
    }

    public void onClickChangeImage(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case RESULT_LOAD_IMG: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        journeyImg.setImageBitmap(selectedImage);
                        selectedJourneyImg = imageUri;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditJourney.this, "You didn't pick an Image", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    void populateEditText() {
        Cursor c = getContentResolver().query(Uri.withAppendedPath(JourneyProviderContract.JOURNEY_URI, journeyID + ""), null, null, null, null);

        if (c.moveToFirst()) {
            int nameIndex = c.getColumnIndex(JourneyProviderContract.J_NAME);
            int commentIndex = c.getColumnIndex(JourneyProviderContract.J_COMMENT);
            int ratingIndex = c.getColumnIndex(JourneyProviderContract.J_RATING);
            int imageIndex = c.getColumnIndex(JourneyProviderContract.J_IMAGE);

            titleET.setText(c.getString(nameIndex));
            commentET.setText(c.getString(commentIndex));
            ratingSpinner.setSelection(c.getInt(ratingIndex) - 1);

            String strUri = c.getString(imageIndex);
            if (strUri != null) {
                try {
                    final Uri imageUri = Uri.parse(strUri);
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    journeyImg.setImageBitmap(selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void setJourneyID(int id) {
        this.journeyID = id;
    }
}
