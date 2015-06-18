package vandy.mooc.provider;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The database helper used by the Weather Content Provider to create
 * and manage its underling database.
 */
public class WeatherDatabaseHelper extends SQLiteOpenHelper {
    /**
     * Database name.
     */
    private static String DATABASE_NAME =
        "vandy_mooc_weather_db";

    /**
     * Database version number.  This is updated with each schema
     * change.
     */
    private static int DATABASE_VERSION = 1;

    /*
     * SQL create table statements.
     */

    /**
     * Weather Values create table statement.
     */
    private static final String CREATE_TABLE_WEATHER_VALUES = "CREATE TABLE "
        + WeatherContract.WeatherValuesEntry.WEATHER_VALUES_TABLE_NAME
        + "("
        + WeatherContract.WeatherValuesEntry._ID 
        + " INTEGER PRIMARY KEY, "
        + WeatherContract.WeatherValuesEntry.COLUMN_LOCATION_KEY 
        + " TEXT, "
        + WeatherContract.WeatherValuesEntry.COLUMN_NAME 
        + " TEXT, "
        + WeatherContract.WeatherValuesEntry.COLUMN_DATE 
        + " REAL, "
        + WeatherContract.WeatherValuesEntry.COLUMN_COD 
        + " INTEGER, "
        + WeatherContract.WeatherValuesEntry.COLUMN_SUNRISE 
        + " REAL, "
        + WeatherContract.WeatherValuesEntry.COLUMN_SUNSET 
        + " REAL, "
        + WeatherContract.WeatherValuesEntry.COLUMN_TEMP 
        + " REAL, "
        + WeatherContract.WeatherValuesEntry.COLUMN_HUMIDITY 
        + " REAL, "
        + WeatherContract.WeatherValuesEntry.COLUMN_PRESSURE 
        + " REAL, "
        + WeatherContract.WeatherValuesEntry.COLUMN_SPEED 
        + " REAL, "
        + WeatherContract.WeatherValuesEntry.COLUMN_DEG 
        + " REAL, "
        + WeatherContract.WeatherValuesEntry.COLUMN_COUNTRY 
        + " TEXT, "
        + WeatherContract.WeatherValuesEntry.COLUMN_EXPIRATION_TIME 
        + " REAL)";

    /**
     * Weather Conditions create table statement.
     */
    private static final String CREATE_TABLE_WEATHER_CONDITIONS = "CREATE TABLE "
        + WeatherContract.WeatherConditionsEntry.WEATHER_CONDITIONS_TABLE_NAME
        + "("
        + WeatherContract.WeatherConditionsEntry._ID
        + " INTEGER PRIMARY KEY, "
        + WeatherContract.WeatherConditionsEntry.COLUMN_WEATHER_CONDITIONS_OBJECT_ID
        + " INTEGER, "
        + WeatherContract.WeatherConditionsEntry.COLUMN_MAIN
        + " TEXT, "
        + WeatherContract.WeatherConditionsEntry.COLUMN_DESCRIPTION
        + " TEXT, "
        + WeatherContract.WeatherConditionsEntry.COLUMN_LOCATION_KEY
        + " TEXT, "
        + WeatherContract.WeatherConditionsEntry.COLUMN_WEATHER_VALUES_PARENT_ID
        + " REAL, "
        + WeatherContract.WeatherConditionsEntry.COLUMN_ICON
        + " TEXT) ";

     /**
     * Constructor - initialize database name and version, but don't
     * actually construct the database (which is done in the
     * onCreate() hook method). It places the database in the
     * application's cache directory, which will be automatically
     * cleaned up by Android if the device runs low on storage space.
     * 
     * @param context
     */
    public WeatherDatabaseHelper(Context context) {
        super(context, 
              context.getCacheDir()
              + File.separator 
              + DATABASE_NAME, 
              null,
              DATABASE_VERSION);
    }

    /**
     * Hook method called when the database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tables.
        db.execSQL(CREATE_TABLE_WEATHER_VALUES);
        db.execSQL(CREATE_TABLE_WEATHER_CONDITIONS);
    }

    /**
     * Hook method called when the database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        // Delete the existing tables.
        db.execSQL("DROP TABLE IF EXISTS "
                   + WeatherContract.WeatherConditionsEntry.WEATHER_CONDITIONS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "
                   + WeatherContract.WeatherValuesEntry.WEATHER_VALUES_TABLE_NAME);
        // Create the new tables.
        onCreate(db);
    }
}
