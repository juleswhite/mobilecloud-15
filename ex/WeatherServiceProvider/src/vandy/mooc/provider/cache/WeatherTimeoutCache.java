package vandy.mooc.provider.cache;

import java.util.ArrayList;

import vandy.mooc.provider.WeatherContract;
import vandy.mooc.provider.WeatherContract.WeatherConditionsEntry;
import vandy.mooc.provider.WeatherContract.WeatherValuesEntry;
import vandy.mooc.retrofitWeather.WeatherData;
import vandy.mooc.retrofitWeather.WeatherData.Main;
import vandy.mooc.retrofitWeather.WeatherData.Sys;
import vandy.mooc.retrofitWeather.WeatherData.Weather;
import vandy.mooc.retrofitWeather.WeatherData.Wind;
import android.app.AlarmManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

/**
 * Timeout cache that uses a content provider to store data and the Alarm
 * manager and a broadcast receiver to remove expired cache entries
 */
public class WeatherTimeoutCache
       implements TimeoutCache<String, WeatherData> {
    /**
     * LogCat tag.
     */
    private final String TAG =
        getClass().getSimpleName();

    /**
     * Default cache timeout in to 25 seconds (in nanoseconds).
     */
    private static final long DEFAULT_TIMEOUT =
        Long.valueOf(25000000000L);

    /**
     * Cache is cleaned up at regular intervals (i.e., twice a day) to
     * remove expired WeatherData.
     */
    public static final long CLEANUP_SCHEDULER_TIME_INTERVAL =
        AlarmManager.INTERVAL_HALF_DAY;

    /**
     * AlarmManager provides access to the system alarm services. Used
     * to schedule Cache cleanup at regular intervals to remove
     * expired Weather Values.
     */
    private static AlarmManager mAlarmManager;

    /**
     * Defines the selection clause query for the weather values of a
     * given location.
     */
    private static final String LOCATION_SELECTION_CLAUSE =
        WeatherValuesEntry.COLUMN_LOCATION_KEY
	    + " = ?";

    /**
     * Defines the selection clause query for the weather values of a
     * given location.
     */
    private static final String WEATHER_CONDITIONS_LOCATION_SELECTION_CLAUSE =
        WeatherConditionsEntry.COLUMN_LOCATION_KEY
	    + " = ?";

    /**
     * Defines the selection clause used to query for weather values
     * that has expired.
     */
    private static final String EXPIRATION_SELECTION =
        WeatherValuesEntry.COLUMN_EXPIRATION_TIME
	    + " <= ?";
    
    /**
     * Defines the selection clause used to query for weather values
     * that has a specific id.
     */
    private static final String WEATHER_VALUES_ID_SELECTION = 
        WeatherValuesEntry._ID 
        + " = ?";
        
    /**
     * Defines the selection clause used to query for weather
     * conditions that have a specific parent id.
     */
    private static final String WEATHER_CONDITIONS_PARENT_ID_SELECTION = 
        WeatherConditionsEntry.COLUMN_WEATHER_VALUES_PARENT_ID
        + " = ?";
    
    /**
     * The timeout for an instance of this class in seconds.
     */
    private long mDefaultTimeout;

    /**
     * Context used to access the contentResolver
     */
    private Context mContext;

    /**
     * Constructor that sets the default timeout for the cache (in
     * seconds).
     */
    public WeatherTimeoutCache(Context context) {
	// Store the context.
	mContext = context;

	// Set the timeout in nanoseconds.
	mDefaultTimeout = DEFAULT_TIMEOUT;

	// Get the AlarmManager system service.
	mAlarmManager = (AlarmManager) 
            context.getSystemService(Context.ALARM_SERVICE);

	// If Cache Cleanup is not scheduled, then schedule it.
	scheduleCacheCleanup(context);
    }

    /**
     * Helper method that creates a content values object that can be
     * inserted into the db's WeatherValuesEntry table from a given
     * WeatherData object.
     */
    private ContentValues makeWeatherDataContentValues(WeatherData wd,
                                                         long timeout,
                                                         String key) {
	ContentValues val = new ContentValues();

	val.put(WeatherContract.WeatherValuesEntry.COLUMN_LOCATION_KEY, 
		key);
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_NAME,
                wd.getName());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_DATE,
                wd.getDate());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_COD,
                wd.getCod());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_SUNRISE,
                wd.getSys().getSunrise());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_SUNSET,
                wd.getSys().getSunset());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_COUNTRY,
                wd.getSys().getCountry());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_TEMP,
                wd.getMain().getTemp());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_HUMIDITY,
                wd.getMain().getHumidity());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_PRESSURE,
                wd.getMain().getPressure());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_SPEED,
                wd.getWind().getSpeed());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_DEG,
                wd.getWind().getDeg());
	val.put(WeatherContract.WeatherValuesEntry.COLUMN_EXPIRATION_TIME,
		System.nanoTime() 
                + timeout);
	return val;
    }

    /**
     * Helper method that creates a content values object that can be
     * inserted into the db's WeatherConditionsEntry table from a given
     * WeatherData object.
     */
    private ContentValues makeWeatherConditionContentValues(Weather wo,
                                                              long parentId,
                                                              String key) {
	ContentValues val = new ContentValues();

	val.put(WeatherContract.WeatherConditionsEntry.COLUMN_WEATHER_CONDITIONS_OBJECT_ID,
		wo.getId());
	val.put(WeatherContract.WeatherConditionsEntry.COLUMN_MAIN,
                wo.getMain());
	val.put(WeatherContract.WeatherConditionsEntry.COLUMN_DESCRIPTION,
		wo.getDescription());
	val.put(WeatherContract.WeatherConditionsEntry.COLUMN_ICON,
                wo.getIcon());
	val.put(WeatherContract.WeatherConditionsEntry.COLUMN_LOCATION_KEY,
                key);
	val.put(WeatherContract.WeatherConditionsEntry.COLUMN_WEATHER_VALUES_PARENT_ID,
		parentId);
	return val;
    }

    /**
     * Helper method that places a WeatherData object into the
     * database.
     */
    private void putImpl(String key,
                         WeatherData wd,
                         long timeout) {
	// Enter the main WeatherData.  The result Uri is used to
	// determine the row that it was placed in.
	final Uri uri =
            mContext.getContentResolver().insert
                (WeatherContract.WeatherValuesEntry.WEATHER_VALUES_CONTENT_URI,
                 makeWeatherDataContentValues(wd,
                                                timeout,
                                                key));

        // @@ Geoff, please see if you can zap this stuff!
	final long parentId =
            ContentUris.parseId(uri);

	// Create an array of ContentValues to bulk insert into the
	// database.
	ContentValues[] cvsArray =
            new ContentValues[wd.getWeathers().size()];

        // Index into cvArray.
        int i = 0;

        // Insert each weather object into the ContentValues array.
	for (Weather weather : wd.getWeathers()) {
	    cvsArray[i++] = 
                makeWeatherConditionContentValues(weather,
                                                  parentId,
                                                  key);
	}

	// Bulk insert the rows into the weather condition table.
	mContext.getContentResolver()
		.bulkInsert
                    (WeatherContract.WeatherConditionsEntry.WEATHER_CONDITIONS_CONTENT_URI,
                     cvsArray);
    }

    /**
     * Attempts to retrieve the given key's corresponding WeatherData
     * object.  If the key doesn't exist or has timed out, null is
     * returned.
     */
    @Override
    public WeatherData get(final String locationKey) {
	// Attempt to retrieve the location's data from the content
	// provider.
	try (Cursor wdCursor = mContext.getContentResolver().query
                 (WeatherContract.ACCESS_ALL_DATA_FOR_LOCATION_URI,
                  null,
                  LOCATION_SELECTION_CLAUSE,
                  new String[] { locationKey },
                  null)) {
	    // Check that the cursor isn't null and contains an item.
	    if (wdCursor != null 
                && wdCursor.moveToFirst()) {
		Log.v(TAG,
                      "Cursor not null and has first item");

		// If cursor has a Weather Values object corresponding
		// to the location, check to see if it has timed out.
		// If it has, delete it, else return the data.
		if (wdCursor.getLong
                        (wdCursor.getColumnIndex
                             (WeatherContract.WeatherValuesEntry.COLUMN_EXPIRATION_TIME)) 
                    < System.nanoTime()) {

		    // Concurrently delete the stale data from the db
		    // in a new thread.
		    new Thread(new Runnable() {
			public void run() {
			    remove(locationKey);
			}
		    }).start();

		    return null;
		} else 
                    // Return the WeatherData object from the contents
                    // in the cursor.
                    return getWeatherData(wdCursor);
	    } else
		// Query was empty or returned null.
		return null;
	}
    }

    /**
     * Constructor using a cursor returned by the WeatherProvider.
     * This cursor must contain all the data for the object - i.e., it
     * must contain a row for each Weather object corresponding to the
     * Weather object.
     */
    private WeatherData getWeatherData(Cursor data) {
	if (data == null 
            || !data.moveToFirst())
            return null;
        else {
            // Obtain data from the first row.  
            final String name = 
                data.getString(data.getColumnIndex(WeatherValuesEntry.COLUMN_NAME));
            final long date = 
                data.getLong(data.getColumnIndex(WeatherValuesEntry.COLUMN_DATE));
            final long cod = 
                data.getLong(data.getColumnIndex(WeatherValuesEntry.COLUMN_COD));
            final Sys sys = 
                new Sys(data.getLong(data.getColumnIndex
                                     (WeatherValuesEntry.COLUMN_SUNRISE)),
                        data.getLong(data.getColumnIndex
                                     (WeatherValuesEntry.COLUMN_SUNSET)),
                        data.getString(data.getColumnIndex
                                       (WeatherValuesEntry.COLUMN_COUNTRY)));
            final Main main =
                new Main(data.getDouble(data.getColumnIndex
                                        (WeatherValuesEntry.COLUMN_TEMP)),
                         data.getLong(data.getColumnIndex
                                      (WeatherValuesEntry.COLUMN_HUMIDITY)),
                         data.getDouble(data.getColumnIndex
                                        (WeatherValuesEntry.COLUMN_PRESSURE)));
            final Wind wind = 
                new Wind(data.getDouble(data.getColumnIndex
                                        (WeatherValuesEntry.COLUMN_SPEED)),
                         data.getDouble(data.getColumnIndex
                                        (WeatherValuesEntry.COLUMN_DEG)));

            final ArrayList<Weather> weathers =
                new ArrayList<>();

            // Once the Weather Values are processed, loop through the
            // cursor to get all the Weather Conditions.
            do {
                weathers.add(new Weather
                             (data.getLong
                              (data.getColumnIndex
                               (WeatherConditionsEntry.COLUMN_WEATHER_CONDITIONS_OBJECT_ID)),
                              data.getString
                              (data.getColumnIndex
                               (WeatherConditionsEntry.COLUMN_MAIN)),
                              data.getString
                              (data.getColumnIndex
                               (WeatherConditionsEntry.COLUMN_DESCRIPTION)),
                              data.getString
                              (data.getColumnIndex
                               (WeatherConditionsEntry.COLUMN_ICON))));
            } while (data.moveToNext());

            // Return a WeatherData object.
            return new WeatherData(name,
                                   date,
                                   cod,
                                   sys,
                                   main,
                                   wind,
                                   weathers);
	}
    }

    /**
     * Place the WeatherData object into the cache. It assumes that a
     * get() method has already attempted to find this object's
     * location in the cache, and returned null.
     */
    @Override
    public void put(String key,
                    WeatherData obj) {
	putImpl(key,
                obj,
                mDefaultTimeout);
    }

    /**
     * Places the WeatherData object into the cache with a user specified
     * timeout.
     */
    @Override
    public void put(String key,
                    WeatherData obj,
                    int timeout) {
	putImpl(key,
                obj,
                // Timeout must be expressed in nanoseconds.
                timeout * 1000 * 1000 * 1000);
    }

    /**
     * Delete the Weather Values and Weather Conditions associated
     * with a @a locationKey.
     */
    @Override
    public void remove(String locationKey) {
	// Delete the WeatherValuesEntries.
	mContext.getContentResolver().delete
            (WeatherContract.WeatherValuesEntry.WEATHER_VALUES_CONTENT_URI,
             LOCATION_SELECTION_CLAUSE,
             new String[] { locationKey });

	// Delete WeatherConditionsEntries.
	mContext.getContentResolver()
		.delete(WeatherContract.WeatherConditionsEntry.WEATHER_CONDITIONS_CONTENT_URI,
			WEATHER_CONDITIONS_LOCATION_SELECTION_CLAUSE,
			new String[] { locationKey });
    }

    /**
     * Return the current number of WeatherData objects in Database.
     * 
     * @return size
     */
    @Override
    public int size() {
	// Query the data for all rows of the Weather Values table.
        try (Cursor cursor =
             mContext.getContentResolver().query
             (WeatherContract.WeatherValuesEntry.WEATHER_VALUES_CONTENT_URI,
              new String[] {WeatherValuesEntry._ID}, 
              null,
              null,
              null)) {
            // Return the number of rows in the table, which is equivlent
            // to the number of objects
            return cursor.getCount();
            }
    }

    /**
     * Remove the expired WeatherData from the database.
     */
    public void removeExpiredWeatherData() {
	// First query the db to find all expired Weather Values
	// objects' ids.
	try (Cursor expiredData =
             mContext.getContentResolver().query
                 (WeatherValuesEntry.WEATHER_VALUES_CONTENT_URI, 
                  new String[] {WeatherValuesEntry._ID}, 
                  EXPIRATION_SELECTION, 
                  new String[] {String.valueOf(System.nanoTime())}, 
                  null)) { 
	    // Use the expired data id's to delete the correct entries
	    // from both tables.
	    if (expiredData != null 
                && expiredData.moveToFirst()) {
		do {
		    String deleteId =
                        expiredData.getString
                            (expiredData.getColumnIndex(WeatherValuesEntry._ID));

		    mContext.getContentResolver().delete
                        (WeatherValuesEntry.WEATHER_VALUES_CONTENT_URI,
                         WEATHER_VALUES_ID_SELECTION, 
                         new String [] {deleteId});
		    
		    mContext.getContentResolver().delete
                        (WeatherConditionsEntry.WEATHER_CONDITIONS_CONTENT_URI, 
                         WEATHER_CONDITIONS_PARENT_ID_SELECTION, 
                         new String [] {deleteId});
		} while (expiredData.moveToNext());
	    }
	}
    }

    /**
     * Helper method that uses AlarmManager to schedule Cache Cleanup at regular
     * intervals.
     * 
     * @param context
     */
    private void scheduleCacheCleanup(Context context) {
	// Only schedule the Alarm if it's not already scheduled.
	if (!isAlarmActive(context)) {
	    // Schedule an alarm after a certain timeout to start a
	    // service to delete expired data from Database.
	    mAlarmManager.setInexactRepeating(
		    AlarmManager.ELAPSED_REALTIME_WAKEUP,
		    SystemClock.elapsedRealtime()
			    + CLEANUP_SCHEDULER_TIME_INTERVAL,
		    CLEANUP_SCHEDULER_TIME_INTERVAL,
		    CacheCleanupReceiver.makeReceiverPendingIntent(context));
	}
    }

    /**
     * Helper method to check whether the Alarm is already active or not.
     * 
     * @param context
     * @return boolean, whether the Alarm is already active or not
     */
    private boolean isAlarmActive(Context context) {
	// Check whether the Pending Intent already exists or not.
	return CacheCleanupReceiver.makeCheckAlarmPendingIntent(context) != null;
    }
}
