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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditJourney extends AppCompatActivity {
    private final int RESULT_LOAD_IMG = 1;

    private ImageView journeyImg;
    private EditText titleET;
    private EditText commentET;
    private EditText ratingET;
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
        ratingET = findViewById(R.id.ratingEditText);
        journeyID = bundle.getLong("journeyID");

        selectedJourneyImg = null;

        populateEditText();
    }

    /* Save the new title, comment, image and rating to the DB */
    public void onClickSave(View v) {
        String title = titleET.getText().toString();
        String comment = commentET.getText().toString();
        String ratingS = ratingET.getText().toString();
        if(!validateEditJourneyInput(title, comment, ratingS)){
            return;
        }
        int rating = Integer.parseInt(ratingS);

        Uri rowQueryUri = Uri.withAppendedPath(JourneyProviderContract.JOURNEY_URI, "" + journeyID);

        ContentValues cv = new ContentValues();
        cv.put(JourneyProviderContract.J_RATING, rating);
        cv.put(JourneyProviderContract.J_COMMENT, comment);
        cv.put(JourneyProviderContract.J_NAME, title);

        if(selectedJourneyImg != null) {
            cv.put(JourneyProviderContract.J_IMAGE, selectedJourneyImg.toString());
        }

        getContentResolver().update(rowQueryUri, cv, null, null);
        finish();
    }

    /* Load activity to choose an image */
    public void onClickChangeImage(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        // get the URI from the selected image
        switch(reqCode) {
            case RESULT_LOAD_IMG: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();

                        // make the URI persistent
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        journeyImg.setImageBitmap(selectedImage);
                        selectedJourneyImg = imageUri;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(EditJourney.this, "You didn't pick an Image",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /* Give the edit texts some initial text from what they were, get this by accessing DB */
    private void populateEditText() {
        Cursor c = getContentResolver().query(Uri.withAppendedPath(JourneyProviderContract.JOURNEY_URI,
                journeyID + ""), null, null, null, null);

        if(c.moveToFirst()) {
            int nameIndex = c.getColumnIndex(JourneyProviderContract.J_NAME);
            int commentIndex = c.getColumnIndex(JourneyProviderContract.J_COMMENT);
            int ratingIndex = c.getColumnIndex(JourneyProviderContract.J_RATING);
            int imageIndex = c.getColumnIndex(JourneyProviderContract.J_IMAGE);

            titleET.setText(c.getString(nameIndex));
            commentET.setText(c.getString(commentIndex));
            ratingET.setText(c.getString(ratingIndex));

            // if an image has been set by user display it, else default image is displayed
            String strUri = c.getString(imageIndex);
            if(strUri != null) {
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

    static Boolean validateEditJourneyInput(String title, String comment, String ratingS){
        if(title.isEmpty()){
//            Log.d("mdp", "Title can not be empty!");
            return false;
        }
        int rating;
        try {
            rating = Integer.parseInt(ratingS);
        } catch(Exception e) {
//            Log.d("mdp", "The following is not a number: " + ratingS);
            return false;
        }
        if(rating < 0 || rating > 5) {
//            Log.d("mdp", "Rating must be between 0-5");
            return false;
        }
        return true;
    }

}
