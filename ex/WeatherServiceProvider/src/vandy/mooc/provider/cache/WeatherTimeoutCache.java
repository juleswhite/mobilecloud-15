package vandy.mooc.provider.cache;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.provider.WeatherContract;
import vandy.mooc.provider.WeatherContract.WeatherConditionEntry;
import vandy.mooc.provider.WeatherContract.WeatherDataEntry;
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
public class WeatherTimeoutCache implements TimeoutCache<String, WeatherData> {
    /**
     * Default cache timeout in to 25 seconds (in nanoseconds).
     */
    private static final long DEFAULT_TIMEOUT = Long.valueOf(25000000000L);

    /**
     * Cache is to be cleaned up at regular intervals to remove expired
     * WeatherData.
     */
    public static final long CLEANUP_SCHEDULER_TIME_INTERVAL = AlarmManager.INTERVAL_HALF_DAY;

    /**
     * AlarmManager provides access to the system alarm services. Used to
     * schedule Cache cleanup at regular intervals to remove expired Weather
     * Data.
     */
    private static AlarmManager mAlarmManager;

    /**
     * Defines the selection clause query for the weather data of a given
     * location.
     */
    private static final String LOCATION_SELECTION_CLAUSE = WeatherDataEntry.COLUMN_NAME
	    + " = ?";

    /**
     * Defines the selection clause query for the weather data of a given
     * location.
     */
    private static final String WEATHER_COND_LOCATION_SELECTION_CLAUSE = WeatherConditionEntry.COLUMN_LOCATION
	    + " = ?";

    /**
     * Defines the selection clause used to query for weather data that has
     * expired
     */
    private static final String EXPIRATION_SELECTION = WeatherDataEntry.COLUMN_EXPIRATION_TIME
	    + " <= ?";
    
    /**
     * Defines the selection clause used to query for weather data that has
     * a specific id
     */
    private static final String WEATHER_DATA_ID_SELECTION = 
	    WeatherDataEntry._ID + " = ?";
    
    
    /**
     * Defines the selection clause used to query for weather conditions that have
     * a specific parent id
     */
    private static final String WEATHER_COND_PARENT_ID_SELECTION = 
	    WeatherConditionEntry.COLUMN_WEATHER_DATA_PARENT_ID + " = ?";
    
    /**
     * LogCat tag.
     */
    private static final String TAG = WeatherTimeoutCache.class
	    .getCanonicalName();

    /**
     * The timeout for an instance of this class in seconds.
     */
    private long mDefaultTimeout;

    /**
     * Context used to access the contentResolver
     */
    private Context mContext;

    /**
     * Ctor that sets the default timeout for the cache (in seconds)
     */
    public WeatherTimeoutCache(Context context) {
	// Set the timeout in nanoseconds
	mDefaultTimeout = DEFAULT_TIMEOUT * 1000 * 1000 * 1000;

	// Get the AlarmManager system service.
	mAlarmManager = (AlarmManager) context
		.getSystemService(Context.ALARM_SERVICE);

	// Store the context.
	mContext = context;

	// If Cache Cleanup is not scheduled, then schedule it.
	scheduleCacheCleanup(context);
    }

    /**
     * Helper method that creates a content values object that can be inserted
     * into the db's WeatherDataEntry table from a given WeatherData object.
     */
    private ContentValues createWeatherDataContentValues(WeatherData wd,
	    long timeout) {
	ContentValues val = new ContentValues();

	val.put(WeatherContract.WeatherDataEntry.COLUMN_NAME,
                wd.getName());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_DATE,
                wd.getDate());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_COD,
                wd.getCod());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_SUNRISE,
                wd.getSys().getSunrise());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_SUNSET,
                wd.getSys().getSunset());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_COUNTRY,
                wd.getSys().getCountry());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_TEMP,
                wd.getMain().getTemp());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_HUMIDITY,
                wd.getMain().getHumidity());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_PRESSURE,
                wd.getMain().getPressure());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_SPEED,
                wd.getWind().getSpeed());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_DEG,
                wd.getWind().getDeg());
	val.put(WeatherContract.WeatherDataEntry.COLUMN_EXPIRATION_TIME,
		System.nanoTime() 
                + timeout);
	return val;
    }

    /**
     * Helper method that creates a content values object that can be inserted
     * into the db's WeatherConditionEntry table from a given WeatherData
     * object.
     */
    private ContentValues createWeatherConditionContentValues(Weather wo,
	    long parentId, String location) {
	ContentValues val = new ContentValues();

	val.put(WeatherContract.WeatherConditionEntry.COLUMN_WEATHER_CONDITION_OBJECT_ID,
		wo.getId());
	val.put(WeatherContract.WeatherConditionEntry.COLUMN_MAIN,
                wo.getMain());
	val.put(WeatherContract.WeatherConditionEntry.COLUMN_DESCRIPTION,
		wo.getDescription());
	val.put(WeatherContract.WeatherConditionEntry.COLUMN_ICON,
                wo.getIcon());
	val.put(WeatherContract.WeatherConditionEntry.COLUMN_LOCATION,
                location);
	val.put(WeatherContract.WeatherConditionEntry.COLUMN_WEATHER_DATA_PARENT_ID,
		parentId);
	return val;
    }

    /**
     * Helper method that places a weather data object into the db
     */
    private void putImpl(String key, WeatherData wd, long timeout) {
	// Enter the main WeatherData. The result Uri is used to
	// determine the row that it was placed in.
	final Uri uri = mContext.getContentResolver().insert(
		WeatherContract.WeatherDataEntry.WEATHER_DATA_CONTENT_URI,
		createWeatherDataContentValues(wd, timeout));

	final long parentId = ContentUris.parseId(uri);

	List<Weather> weathers = wd.getWeathers();

	ContentValues[] cvArray = new ContentValues[weathers.size()];

	// Create a list of the content values to insert into the db
	for (int i = 0; i < weathers.size(); i++) {
	    cvArray[i] = createWeatherConditionContentValues(weathers.get(i),
		    parentId, wd.getName());
	}

	// bulk insert the rows at once into the weather condition table
	mContext.getContentResolver()
		.bulkInsert(
			WeatherContract.WeatherConditionEntry.WEATHER_CONDITION_CONTENT_URI,
			cvArray);
    }

    /**
     * Attempts to retrieve the given key's corresponding WeatherData object. If
     * the key doesn't exist or has timed out, null is returned.
     */
    @Override
    public WeatherData get(final String location) {
	// Attempt to retrieve the location's data from the content
	// provider.
	try (Cursor wdCursor = mContext.getContentResolver().query(
		WeatherContract.ACCESS_ALL_DATA_FOR_LOCATION_URI, null,
		LOCATION_SELECTION_CLAUSE, new String[] { location }, null)) {
	    // Check that the cursor isn't null and contains an item.
	    if (wdCursor != null && wdCursor.moveToFirst()) {
		Log.v(TAG, "Cursor not null and has first item");

		// If the cursor has a Weather Data object
		// corresponding to the location, check to see if it
		// has timed out. If it has, delete it, else return
		// the data.
		if (wdCursor
			.getLong(wdCursor
				.getColumnIndex(WeatherContract.WeatherDataEntry.COLUMN_EXPIRATION_TIME)) < System
			.nanoTime()) {

		    // Delete the stale data from the db in a new thread.
		    new Thread(new Runnable() {
			public void run() {
			    remove(location);
			}
		    }).start();

		    return null;
		} else {
		    WeatherData newData = getWeatherData(wdCursor);
		    return newData;
		}

	    } else
		// Query was empty or returned null.
		return null;
	}
    }

    /**
     * Constructor using a Cursor returned by the WeatherProvider.
     * This Cursor must contain all the data for the object - i.e., it
     * must contain a row for each Weather object corresponding to the
     * Weather object.
     */
    private WeatherData getWeatherData(Cursor data) {
	if (data == null 
            || !data.moveToFirst())
            return null;
        else {
            // Obtain data from the first row.  Once Weather is used,
            // loop through the cursor to get all the Weather Data.
            final String name = 
                data.getString(data.getColumnIndex(WeatherDataEntry.COLUMN_NAME));
            final long date = 
                data.getLong(data.getColumnIndex(WeatherDataEntry.COLUMN_DATE));
            final long cod = 
                data.getLong(data.getColumnIndex(WeatherDataEntry.COLUMN_COD));
            final Sys sys = 
                new Sys(data.getLong(data.getColumnIndex(WeatherDataEntry.COLUMN_SUNRISE)),
                        data.getLong(data.getColumnIndex(WeatherDataEntry.COLUMN_SUNSET)),
                        data.getString(data.getColumnIndex(WeatherDataEntry.COLUMN_COUNTRY)));
            final Main main =
                new Main(data.getDouble(data.getColumnIndex(WeatherDataEntry.COLUMN_TEMP)),
                         data.getLong(data.getColumnIndex(WeatherDataEntry.COLUMN_HUMIDITY)),
                         data.getDouble(data.getColumnIndex(WeatherDataEntry.COLUMN_PRESSURE)));
            final Wind wind = 
                new Wind(data.getDouble(data.getColumnIndex(WeatherDataEntry.COLUMN_SPEED)),
                         data.getDouble(data.getColumnIndex(WeatherDataEntry.COLUMN_DEG)));
            final ArrayList<Weather> weathers = new ArrayList<>();

            do {
                weathers.add
                    (new Weather(data.getLong
                                 (data.getColumnIndex(WeatherConditionEntry.COLUMN_WEATHER_CONDITION_OBJECT_ID)),
                                 data.getString
                                 (data.getColumnIndex(WeatherConditionEntry.COLUMN_MAIN)),
                                 data.getString
                                 (data.getColumnIndex(WeatherConditionEntry.COLUMN_DESCRIPTION)),
                                 data.getString
                                 (data.getColumnIndex(WeatherConditionEntry.COLUMN_ICON))));
            } while (data.moveToNext());

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
     * Place the WeatherData object into the cache. It assumes that a get()
     * method has already attempted to find this object's location in the cache,
     * and returned null.
     */
    @Override
    public void put(String key, WeatherData obj) {
	putImpl(key, obj, mDefaultTimeout);
    }

    /**
     * Places the WeatherData object into the cache with a user specified
     * timeout.
     */
    @Override
    public void put(String key, WeatherData obj, int timeout) {
	putImpl(key, obj, timeout * 1000 * 1000 * 1000);
    }

    @Override
    public void remove(String key) {
	// Delete the WeatherDataEntries.
	mContext.getContentResolver().delete(
		WeatherContract.WeatherDataEntry.WEATHER_DATA_CONTENT_URI,
		LOCATION_SELECTION_CLAUSE, new String[] { key });

	// Delete WeatherConditionEntries
	mContext.getContentResolver()
		.delete(WeatherContract.WeatherConditionEntry.WEATHER_CONDITION_CONTENT_URI,
			WEATHER_COND_LOCATION_SELECTION_CLAUSE,
			new String[] { key });
    }

    /**
     * Return the current number of WeatherData objects in Database.
     * 
     * @return size
     */
    @Override
    public int size() {
	// Query the db for all rows of the Weather Data table.
	Cursor cursor = mContext.getContentResolver().query(
		WeatherContract.WeatherDataEntry.WEATHER_DATA_CONTENT_URI,
		null, null, null, null);

	// Return the number of rows in the table, which is equivlent to the
	// number of objects
	int size = cursor.getCount();
	cursor.close();
	return size;
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

    /**
     * Remove the expired WeatherData from Database.
     */
    public void removeExpiredWeatherData() {
	// First query the db to find all expired Weather Data objects' ids
	try (Cursor expiredData = mContext.getContentResolver()
		.query(WeatherDataEntry.WEATHER_DATA_CONTENT_URI, 
			new String[] {WeatherDataEntry._ID}, 
			EXPIRATION_SELECTION, 
			new String[] {String.valueOf(System.nanoTime())}, 
			null)) { 
	    
	    // Use the expired data id's to delete the correct entries from 
	    // both tables
	    if (expiredData != null && expiredData.moveToFirst()) {
		do {
		    String deleteId = expiredData.getString(
				    expiredData.getColumnIndex(WeatherDataEntry._ID));
		    
		    mContext.getContentResolver().delete(
			    WeatherDataEntry.WEATHER_DATA_CONTENT_URI, 
			    WEATHER_DATA_ID_SELECTION, 
			    new String [] {deleteId});
		    
		    mContext.getContentResolver().delete(
			    WeatherConditionEntry.WEATHER_CONDITION_CONTENT_URI, 
			    WEATHER_COND_PARENT_ID_SELECTION, 
			    new String [] {deleteId});
		}
		while (expiredData.moveToNext());
	    }
	}
    }
}
