package com.example.strider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DBHelperTest {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    @After
    public void tearDown() {
        dbHelper.close();
        db.close();
    }

    @Test
    public void testJourneyTableSchema() {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='journey'", null);
        assertTrue(cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testLocationTableSchema() {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='location'", null);
        assertTrue(cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testInsertionAndQuery() {
        ContentValues journeyValues = new ContentValues();
        journeyValues.put("duration", 1000L);
        journeyValues.put("distance", 10.5);
        journeyValues.put("date", "2024-05-16 12:00:00");
        long journeyId = db.insert("journey", null, journeyValues);

        ContentValues locationValues = new ContentValues();
        locationValues.put("journeyID", journeyId);
        locationValues.put("altitude", 100.0);
        locationValues.put("longitude", 50.0);
        locationValues.put("latitude", 30.0);
        long locationId = db.insert("location", null, locationValues);

        Cursor cursor = db.rawQuery("SELECT * FROM location WHERE journeyID=?", new String[]{String.valueOf(journeyId)});
        assertTrue(cursor.moveToFirst());
        assertEquals(locationId, cursor.getLong(cursor.getColumnIndex("locationID")));
        cursor.close();
    }
}
