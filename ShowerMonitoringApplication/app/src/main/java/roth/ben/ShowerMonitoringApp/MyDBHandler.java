package roth.ben.ShowerMonitoringApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ben Roth on 29-Apr-18.
 */

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "showerData.db";

    private static final String TABLE_NAME = "showerData";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_TEMP = "temperature";
    private static final String COLUMN_GALLONS = "gallonsUsed";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " INTEGER, " +
                COLUMN_GALLONS + " REAL, " +
                COLUMN_TEMP + " REAL, " +
                COLUMN_TIMESTAMP + " TIME);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Add new row
    public void addRow(double time, double gallons, double temp) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_GALLONS, gallons);
        values.put(COLUMN_TEMP, temp);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //Delete row
    public void deleteRow(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=\"" + id + "\";");
    }

    public void printAllData() {
        String holder;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_ID + ", " + COLUMN_TIME + ", " + COLUMN_TEMP + ", " + COLUMN_GALLONS + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            holder = "";
            holder += "ID: " + cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            holder += ", Time: " +  cursor.getInt(cursor.getColumnIndex(COLUMN_TIME));
            holder += ", Average Temperature: " + cursor.getDouble(cursor.getColumnIndex(COLUMN_TEMP));
            holder += ", Water Used: " + cursor.getDouble(cursor.getColumnIndex(COLUMN_GALLONS));
            Log.d("database", holder);
            cursor.moveToNext();
        }
        db.close();
    }

    public double getTotalTemp() {
        double tempSum = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_TEMP + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            tempSum += cursor.getDouble(cursor.getColumnIndex(COLUMN_TEMP));
            cursor.moveToNext();
        }
        return tempSum;
    }

    public double[] getTempData() {
        int count = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        db.rawQuery(query, null);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int size = cursor.getInt(0);
        double[] tempArray = new double[size];
        query = "SELECT " + COLUMN_TEMP + " FROM " + TABLE_NAME;
        cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            tempArray[count] = cursor.getDouble(cursor.getColumnIndex(COLUMN_TEMP));
            cursor.moveToNext();
            count++;
        }
        return tempArray;
    }

    public double getTotalGallons() {
        double galSum = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_GALLONS + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            galSum += cursor.getDouble(cursor.getColumnIndex(COLUMN_GALLONS));
            cursor.moveToNext();
        }
        return galSum;
    }

    public double[] getGallonData() {
        int count = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        db.rawQuery(query, null);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int size = cursor.getInt(0);
        double[] galArray = new double[size];
        query = "SELECT " + COLUMN_GALLONS + " FROM " + TABLE_NAME;
        cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            galArray[count] = cursor.getDouble(cursor.getColumnIndex(COLUMN_GALLONS));
            cursor.moveToNext();
            count++;
        }
        return galArray;
    }

    public int getTotalTime() {
        int seconds = 0;
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_TIME + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            seconds += cursor.getInt(cursor.getColumnIndex(COLUMN_TIME));
            cursor.moveToNext();
        }
        return seconds;
    }

    public int getShowerCount() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        db.rawQuery(query, null);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }
}
