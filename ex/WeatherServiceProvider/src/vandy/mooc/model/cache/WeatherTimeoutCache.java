package vandy.mooc.model.cache;

import java.util.ArrayList;

import vandy.mooc.common.TimeoutCache;
import vandy.mooc.model.provider.WeatherContract;
import vandy.mooc.model.provider.WeatherContract.WeatherConditionsEntry;
import vandy.mooc.model.provider.WeatherContract.WeatherValuesEntry;
import vandy.mooc.model.webdata.WeatherData;
import vandy.mooc.model.webdata.WeatherData.Main;
import vandy.mooc.model.webdata.WeatherData.Sys;
import vandy.mooc.model.webdata.WeatherData.Weather;
import vandy.mooc.model.webdata.WeatherData.Wind;
import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;
import android.util.Log;

/**
 * Timeout cache that uses a content provider to store data and the
 * Alarm manager and a broadcast receiver to remove expired cache
 * entries
 */
public class WeatherTimeoutCache
       implements TimeoutCache<String, WeatherData> {
    /**
     * LogCat tag.
     */
    private final static String TAG =
        WeatherTimeoutCache.class.getSimpleName();

    /**
     * Default cache timeout in to 25 seconds (in milliseconds).
     */
    private static final long DEFAULT_TIMEOUT =
        Long.valueOf(25000L);

    /**
     * Cache is cleaned up at regular intervals (i.e., twice a day) to
     * remove expired WeatherData.
     */
    public static final long CLEANUP_SCHEDULER_TIME_INTERVAL =
        AlarmManager.INTERVAL_HALF_DAY;

    /**
     * AlarmManager provides access to the system alarm services.
     * Used to schedule Cache cleanup at regular intervals to remove
     * expired Weather Values.
     */
    private AlarmManager mAlarmManager;

    /**
     * Defines the selection clause used to query for weather values
     * that has a specific id.
     */
    private static final String WEATHER_VALUES_LOCATION_KEY_SELECTION = 
        WeatherValuesEntry.COLUMN_LOCATION_KEY
        + " = ?";
    
    /**
     * Defines the selection clause used to query for weather values
     * that has a specific id and expiration time.
     */
    private static final String WEATHER_VALUES_LOCATION_TIME_KEY_SELECTION = 
        WeatherValuesEntry.COLUMN_LOCATION_KEY
        + " = ?" 
        + " AND " 
        + WeatherValuesEntry.COLUMN_EXPIRATION_TIME
        + " = ?";
        
    /**
     * Defines the selection clause used to query for weather
     * conditions that have a specific parent id.
     */
    private static final String WEATHER_CONDITIONS_LOCATION_KEY_SELECTION = 
        WeatherConditionsEntry.COLUMN_LOCATION_KEY
        + " = ?";
    
    /**
     * Defines the selection clause used to query for weather
     * conditions that have a specific parent id and expiration time.
     */
    private static final String WEATHER_CONDITIONS_LOCATION_TIME_KEY_SELECTION = 
        WeatherConditionsEntry.COLUMN_LOCATION_KEY
        + " = ?"
        + " AND " 
        + WeatherConditionsEntry.COLUMN_EXPIRATION_TIME
        + " = ?";
    
    /**
     * The timeout for an instance of this class in milliseconds.
     */
    private long mDefaultTimeout;

    /**
     * Context used to access the contentResolver.
     */
    private Context mContext;

    /**
     * Constructor that sets the default timeout for the cache (in
     * seconds).
     */
    public WeatherTimeoutCache(Context context) {
	// Store the context.
	mContext = context;

	// Set the timeout in milliseconds.
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
                                                       long expirationTime,
                                                       String locationKey) {
	ContentValues cvs = new ContentValues();

	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_LOCATION_KEY, 
		locationKey);
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_NAME,
                wd.getName());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_DATE,
                wd.getDate());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_COD,
                wd.getCod());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_SUNRISE,
                wd.getSys().getSunrise());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_SUNSET,
                wd.getSys().getSunset());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_COUNTRY,
                wd.getSys().getCountry());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_TEMP,
                wd.getMain().getTemp());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_HUMIDITY,
                wd.getMain().getHumidity());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_PRESSURE,
                wd.getMain().getPressure());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_SPEED,
                wd.getWind().getSpeed());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_DEG,
                wd.getWind().getDeg());
	cvs.put(WeatherContract.WeatherValuesEntry.COLUMN_EXPIRATION_TIME,
	            expirationTime);
	return cvs;
    }

    /**
     * Helper method that creates a content values object that can be
     * inserted into the db's WeatherConditionsEntry table from a
     * given WeatherData object.
     */
    private ContentValues makeWeatherConditionsContentValues(Weather wo,
                                                             long expirationTime,
                                                             String locationKey) {
	ContentValues cvs = new ContentValues();

	cvs.put(WeatherContract.WeatherConditionsEntry.COLUMN_WEATHER_CONDITIONS_OBJECT_ID,
		wo.getId());
	cvs.put(WeatherContract.WeatherConditionsEntry.COLUMN_MAIN,
                wo.getMain());
	cvs.put(WeatherContract.WeatherConditionsEntry.COLUMN_DESCRIPTION,
		wo.getDescription());
	cvs.put(WeatherContract.WeatherConditionsEntry.COLUMN_ICON,
                wo.getIcon());
	cvs.put(WeatherContract.WeatherConditionsEntry.COLUMN_LOCATION_KEY,
                locationKey);
	cvs.put(WeatherContract.WeatherConditionsEntry.COLUMN_EXPIRATION_TIME, 
	            expirationTime);
	return cvs;
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
                // Timeout must be expressed in milliseconds.
                timeout * 1000);
    }

    /**
     * Helper method that places a WeatherData object into the
     * database.
     */
    private void putImpl(String locationKey,
                         WeatherData wd,
                         long timeout) {
    // Determine the data's expiration time
    final long expirationTime =
           System.currentTimeMillis() + timeout;    
        
	// Enter the main WeatherData into the WeatherValues table.
	mContext.getContentResolver().insert
                (WeatherContract.WeatherValuesEntry.WEATHER_VALUES_CONTENT_URI,
                 makeWeatherDataContentValues(wd,
                                              expirationTime,
                                              locationKey));

        // Create an array of ContentValues to bulk insert into the
	// database.
	ContentValues[] cvsArray =
            new ContentValues[wd.getWeathers().size()];

        // Index into cvsArray.
        int i = 0;

        // Insert each weather object into the ContentValues array.
	for (Weather weather : wd.getWeathers()) {
	    cvsArray[i++] = 
                makeWeatherConditionsContentValues(weather,
                                                   expirationTime, 
                                                   locationKey);
	}

	// Bulk insert the rows into the WeatherConditions table.
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
                  WEATHER_VALUES_LOCATION_KEY_SELECTION,
                  new String[] { locationKey },
                  null)) {
	    // Check that the cursor isn't null and contains an item.
	    if (wdCursor != null 
                && wdCursor.moveToFirst()) {
		Log.v(TAG,
                      "Cursor not null and has first item");

		// If cursor has a Weather Values object corresponding
		// to the location, check to see if it has expired.
		// If it has, delete it concurrently, else return the
		// data.
		final long expirationTime = wdCursor.getLong
                        (wdCursor.getColumnIndex
                             (WeatherContract.WeatherValuesEntry.COLUMN_EXPIRATION_TIME));
		
		if (expirationTime < System.currentTimeMillis()) {
		    // Concurrently delete the stale data from the db
		    // in a new thread.
		    new Thread(new Runnable() {
			public void run() {
                            // Remove the key that has the designated
                            // expiration time.
			    remove(locationKey,
                                   expirationTime);
			}
		    }).start();

		    return null;
		} else 
                    // Convert the contents of the cursor into a
                    // WeatherData object.
                    return getWeatherDataFromCursor(wdCursor);
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
    private WeatherData getWeatherDataFromCursor(Cursor data) {
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
     * Delete the Weather Values and Weather Conditions associated
     * with a @a locationKey and a specific @a expirationTime.
     */
    public void remove(String locationKey,
                        long expirationTime) {
        // Delete expired entries from the WeatherValues table.
    mContext.getContentResolver().delete
            (WeatherValuesEntry.WEATHER_VALUES_CONTENT_URI,
             WEATHER_VALUES_LOCATION_TIME_KEY_SELECTION,
             new String[] { 
                locationKey,
                Long.toString(expirationTime) 
            });

        // Delete expired entries from the WeatherConditions table.
    mContext.getContentResolver().delete
            (WeatherConditionsEntry.WEATHER_CONDITIONS_CONTENT_URI,
             WEATHER_CONDITIONS_LOCATION_TIME_KEY_SELECTION,
             new String[] { 
                locationKey,
                Long.toString(expirationTime) 
            });
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
     * Remove all expired WeatherData rows from the database.  This
     * method is called periodically via the AlarmManager.
     */
    public void removeExpiredWeatherData() {
        // Defines the selection clause used to query for weather values
        // that has expired.
        final String EXPIRATION_SELECTION =
            WeatherValuesEntry.COLUMN_EXPIRATION_TIME
	    + " <= ?";

	// First query the db to find all expired Weather Values
	// objects' ids.
	try (Cursor expiredData =
             mContext.getContentResolver().query
                 (WeatherValuesEntry.WEATHER_VALUES_CONTENT_URI, 
                  new String[] { 
                     WeatherValuesEntry.COLUMN_LOCATION_KEY,
                     WeatherValuesEntry.COLUMN_EXPIRATION_TIME
                  },
                  EXPIRATION_SELECTION, 
                  new String[] {String.valueOf(System.currentTimeMillis())}, 
                  null)) { 
	    // Use the expired data id's to delete the designated
	    // entries from both tables.
	    if (expiredData != null 
                && expiredData.moveToFirst()) {
		do {
                    // Get the location to delete.
		    final String deleteLocation =
                        expiredData.getString
                            (expiredData.getColumnIndex
                        	    (WeatherValuesEntry.COLUMN_LOCATION_KEY));
		    final long expirationTime = 
		                expiredData.getLong
                            (expiredData.getColumnIndex
                                    (WeatherValuesEntry.COLUMN_EXPIRATION_TIME));
                    remove(deleteLocation,
                           expirationTime);
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
	    mAlarmManager.setInexactRepeating
                (AlarmManager.ELAPSED_REALTIME_WAKEUP,
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
