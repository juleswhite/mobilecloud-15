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
     * Database version number. This is updated with each schema
     * change.
     */
    private static int DATABASE_VERSION = 3;

    /*
     * SQL create table statements.
     */

    /**
     * Weather Data create table statement.
     */
    private static final String CREATE_TABLE_WEATHER_DATA = "CREATE TABLE "
        + WeatherContract.WeatherDataEntry.WEATHER_DATA_TABLE_NAME + "("
        + WeatherContract.WeatherDataEntry._ID + " INTEGER PRIMARY KEY, "
        + WeatherContract.WeatherDataEntry.COLUMN_NAME + " TEXT, "
        + WeatherContract.WeatherDataEntry.COLUMN_DATE + " REAL, "
        + WeatherContract.WeatherDataEntry.COLUMN_COD + " INTEGER, "
        + WeatherContract.WeatherDataEntry.COLUMN_SUNRISE + " REAL, "
        + WeatherContract.WeatherDataEntry.COLUMN_SUNSET + " REAL, "
        + WeatherContract.WeatherDataEntry.COLUMN_TEMP + " REAL, "
        + WeatherContract.WeatherDataEntry.COLUMN_HUMIDITY + " REAL, "
        + WeatherContract.WeatherDataEntry.COLUMN_PRESSURE + " REAL, "
        + WeatherContract.WeatherDataEntry.COLUMN_SPEED + " REAL, "
        + WeatherContract.WeatherDataEntry.COLUMN_DEG + " REAL, "
        + WeatherContract.WeatherDataEntry.COLUMN_COUNTRY + " TEXT, "
        + WeatherContract.WeatherDataEntry.COLUMN_EXPIRATION_TIME
        + " REAL)";

    /**
     * Weather Condition create table statement.
     */
    private static final String CREATE_TABLE_WEATHER_CONDITION = "CREATE TABLE "
        + WeatherContract.WeatherConditionEntry.WEATHER_CONDITION_TABLE_NAME
        + "("
        + WeatherContract.WeatherConditionEntry._ID
        + " INTEGER PRIMARY KEY, "
        + WeatherContract.WeatherConditionEntry.COLUMN_WEATHER_CONDITION_OBJECT_ID
        + " INTEGER, "
        + WeatherContract.WeatherConditionEntry.COLUMN_MAIN
        + " TEXT, "
        + WeatherContract.WeatherConditionEntry.COLUMN_DESCRIPTION
        + " TEXT, "
        + WeatherContract.WeatherConditionEntry.COLUMN_LOCATION
        + " TEXT, "
        + WeatherContract.WeatherConditionEntry.COLUMN_WEATHER_DATA_PARENT_ID
        + " REAL, "
        + WeatherContract.WeatherConditionEntry.COLUMN_ICON
        + " TEXT) ";

     /**
     * Constructor - initialize database name and version, but don't
     * actually construct the database (which is done in the
     * onCreate() hook method). It places the db in the application's 
     * cache directory
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
     * Hook method called when the database is created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WEATHER_DATA);
        db.execSQL(CREATE_TABLE_WEATHER_CONDITION);
    }

    /**
     * Hook method called when the database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "
                   + WeatherContract.WeatherConditionEntry.WEATHER_CONDITION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "
                   + WeatherContract.WeatherDataEntry.WEATHER_DATA_TABLE_NAME);
        onCreate(db);
    }
}
