package com.example.myapppas;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class dbWeatherManager extends SQLiteOpenHelper {

    public static abstract class weather_dbEntry implements BaseColumns {
        public static final String TABLE_NAME ="weatherDB";
        public static final String ID = "id";
        public static final String CITY = "city";
        public static final String DATE = "date";
        public static final String TEMP = "temperature";
        public static final String WIN = "wind  ";
        public static final String HUM = "humidity";
        public static final String RAIN = "rain";
        public static final String UVI = "uv_intensity";
        public static final String CO = "co";
        public static final String NO2 = "no2";
        public static final String SO2 = "so2";
    }
    private int count;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weatherDB.db";

    public dbWeatherManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        count = 0;
        db.execSQL("CREATE TABLE " + weather_dbEntry.TABLE_NAME + " ("
                + weather_dbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + weather_dbEntry.ID + " TEXT NOT NULL,"
                + weather_dbEntry.CITY + " TEXT NOT NULL,"
                + weather_dbEntry.DATE + " TEXT NOT NULL,"
                + weather_dbEntry.TEMP + " TEXT NOT NULL,"
                + weather_dbEntry.WIN + " TEXT NOT NULL,"
                + weather_dbEntry.HUM + " TEXT NOT NULL,"
                + weather_dbEntry.RAIN + " TEXT NOT NULL,"
                + weather_dbEntry.UVI + " TEXT NOT NULL,"
                + weather_dbEntry.CO + " TEXT NOT NULL,"
                + weather_dbEntry.NO2 + " TEXT NOT NULL,"
                + weather_dbEntry.SO2 + " TEXT NOT NULL,"
                + "UNIQUE (" + weather_dbEntry.ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        count = (int) DatabaseUtils.queryNumEntries(db, weather_dbEntry.TABLE_NAME);
    }

    public boolean insertDataWeather(SQLiteDatabase db,ContentValues contentValues){
        long result = db.insert(weather_dbEntry.TABLE_NAME, null, contentValues);
        if (result >= 0.0){
            count++;
            return true;
        }
        return false;
    }

    public int getNRows(){
        return count;
    }

}
