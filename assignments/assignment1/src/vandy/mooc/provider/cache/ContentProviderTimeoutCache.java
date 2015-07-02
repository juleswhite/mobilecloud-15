package vandy.mooc.provider.cache;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.provider.AcronymContract;
import vandy.mooc.provider.AcronymContract.AcronymEntry;
import vandy.mooc.retrofit.AcronymData.AcronymExpansion;
import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;

/**
 * Timeout cache that uses Content Providers to cache data and uses
 * AlarmManager and Broadcast Receiver to remove expired cache entries
 * periodically.
 */
public class ContentProviderTimeoutCache
       implements TimeoutCache<String, List<AcronymExpansion>> {
    private static final String TAG = ContentProviderTimeoutCache.class.getSimpleName();
    /**
     * Cache is cleaned up twice a day to remove expired acronyms.
     */
    public static final long CLEANUP_SCHEDULER_TIME_INTERVAL = 
        AlarmManager.INTERVAL_HALF_DAY;
   
    /**
     * Store the context to allow access to application-specific
     * resources and classes.
     */
    private Context mContext;

    /**
     * Time after which the data will expire (in nanoseconds).
     */
    private long mDefaultTimeout;

    /**
     * AlarmManager provides access to the system alarm services.
     * Used to schedule Cache cleanup at regular intervals to remove
     * expired Acronym Expansions.
     */
    private AlarmManager mAlarmManager;

    /**
     * This constructor sets the default timeout to the designated @a
     * timeout parameter and initialises local variables.
     * 
     * @param context
     */
    public ContentProviderTimeoutCache(Context context) {
        // Store the context.
        mContext = context;

        // Set the timeout to 10 seconds in nanoseconds.
        mDefaultTimeout = Long.valueOf(10000000000L);
        
        // Get the AlarmManager system service.
        mAlarmManager = (AlarmManager)
            context.getSystemService(Context.ALARM_SERVICE);

        // If Cache Cleanup is not scheduled, then schedule it.
        scheduleCacheCleanup(context);
    }
    
    /**
     * Gets the @a List of Acronym Expansions from the cache having
     * given @a acronym. Remove it if expired.
     * 
     * @param acronym
     * @return List of Acronym Data
     */
    public List<AcronymExpansion> get(final String acronym) {
        // Selection clause to find rows with given acronym.
        final String SELECTION_ACRONYM = 
            AcronymEntry.COLUMN_ACRONYM
            + " = ?";
    	
        // Initializes an array to contain selection arguments.
        String[] selectionArgs = { acronym };
       
        // Cursor that is returned as a result of database query which
        // points to one or more rows.
        try (Cursor cursor =
             mContext.getContentResolver().query
             (AcronymEntry.CONTENT_URI,
              null,
              SELECTION_ACRONYM,
              selectionArgs,
              null)) {
            // If there are not matches in the database return false. 
            if (!cursor.moveToFirst())
                return null;

            // Since all rows with same acronym have same expiration
            // time, check the expiration of first row.  If its
            // expired, then return null and start a thread to delete
            // all rows having that acronym else get the Acronym Data
            // from first row and add it to the List.
            else {
                // TODO -- replace "0" with the expiration time of
                // given acronym that's obtained from the cursor.

                //     0     |   1  |   2   |    3    |   4
                // acronymn  |  lf  | freq  |  since  |  exp
                Long expirationTime = cursor.getLong(
                    cursor.getColumnIndex(AcronymEntry.COLUMN_EXPIRATION_TIME));
                
                // Check if the acronym is expired. If true, then
                // remove it.
                if (System.nanoTime() > expirationTime) {
                    // Start a thread to delete all rows having that
                    // acronym.
                    new Thread(new Runnable() {
                            public void run() {
                                remove(acronym);		
                            }
                        }).start();
                    return null;
                } else {
                    List<AcronymExpansion> longForms =
                        new ArrayList<AcronymExpansion>();

                    // Now we're sure that the acronym has not
                    // expired, get the List of AcronymExpansions.
                    do 
                        longForms.add(getAcronymExpansion(cursor));
                    while (cursor.moveToNext());

                    return longForms;
                }
            }
        }
    }

    /**
     * Get the acronym expansions data from the database.
     * 
     * @param cursor
     * @return AcronymExpansion
     */
    private AcronymExpansion getAcronymExpansion(Cursor cursor) {
        // TODO -- replace "null" with the "long form" of the acronym
        // obtained from the cursor.
        String longForm = cursor.getString(
            cursor.getColumnIndex(AcronymEntry.COLUMN_LONG_FORM));
        // TODO -- replace "0" with the "frequency" value of the acronym
        // obtained from the cursor.
        int frequency = cursor.getInt(
            cursor.getColumnIndex(AcronymEntry.COLUMN_FREQUENCY));
        // TODO -- replace "0" with the "since" value of the acronym
        // obtained from the cursor.
        int since = cursor.getInt(
            cursor.getColumnIndex(AcronymEntry.COLUMN_SINCE));
        return new AcronymExpansion(longForm,
                                    frequency,
                                    since);
    }

    /**
     * Put the @a longForms into the cache at the designated @a
     * acronym with the default timeout.
     * 
     * @param acronym
     * @param longForms
     */
    public void put(String acronym,
                    List<AcronymExpansion> longForms) {
        putValues(acronym,
                  longForms,
                  mDefaultTimeout);
    }

    /**
     * Put the @a longForms into the Database at the designated @a
     * acronym with a certain timeout after which the cached data will
     * expire.
     * 
     * @param acronym
     * @param longForms
     * @param timeout
     */
    public void put(String acronym,
                    List<AcronymExpansion> longForms,
                    int timeout) {
        putValues(acronym,
                  longForms,
                  // Represent the timeout in nanoseconds.
                  timeout * 1000 * 1000 * 1000);
    }

    /**
     * Helper method that puts a @a List of Acronym Expansions into
     * the cache at the designated @a acronym with a certain timeout,
     * after which the cached data expires.
     * 
     * @param acronym
     * @param longForms
     * @param timeout
     * @return number of rows inserted
     */
    private int putValues(String acronym,
                          List<AcronymExpansion> longForms,
                          long timeout) {
        // Check if the List is not null or empty.
        if (longForms == null
            || longForms.isEmpty()) 
            return -1;

        // Calculate the Expiration time.
        Long expirationTime =
            System.nanoTime() + timeout;

        // Use ContentValues to store the values in appropriate
        // columns, so that ContentResolver can process it.  Since
        // more than one rows needs to be inserted, an Array of
        // ContentValues is needed.
        ContentValues[] cvArray =
            new ContentValues[longForms.size()];

        for (int i = 0; i < longForms.size(); i++) {
            // TODO -- as you loop through the list of acronym
            // expansions create a ContentValues object that contains
            // their contents, and store this into the appropriate
            // location the cvArray.
            ContentValues cv = new ContentValues();
            cv.put(AcronymEntry.COLUMN_ACRONYM, acronym);
            cv.put(AcronymEntry.COLUMN_LONG_FORM, longForms.get(i).getLf());
            cv.put(AcronymEntry.COLUMN_FREQUENCY, longForms.get(i).getFreq());
            cv.put(AcronymEntry.COLUMN_SINCE, longForms.get(i).getSince());
            cv.put(AcronymEntry.COLUMN_EXPIRATION_TIME, expirationTime);
            cvArray[i] = cv;
        }

        // Use ContentResolver to bulk insert the ContentValues into
        // the Acronym database and return the number of rows inserted.
        return mContext.getContentResolver().bulkInsert(AcronymEntry.CONTENT_URI,
                                                        cvArray);
    }
    
    /**
     * Removes each expansion associated with the designated @a acronym.
     * 
     * @param acronym
     */
    public void remove(String acronym) {
        // Selection clause to find rows with given acronym.
        final String SELECTION_ACRONYM = 
            AcronymEntry.COLUMN_ACRONYM
            + " = ?";

        // Initializes an array to contain selection arguments
        String[] selectionArgs = { acronym };

        // TODO - delete the row(s) associated with an acronym.
        mContext.getContentResolver().delete(
            AcronymEntry.CONTENT_URI, SELECTION_ACRONYM, selectionArgs);
    }

    /**
     * Return the current number of rows in Database.
     * 
     * @return size
     */
    public int size() {
        // Use ContentResolver to get a Cursor that points to all rows
        // of Acronym table.
        try (Cursor cursor =
             mContext.getContentResolver().query
             (AcronymEntry.CONTENT_URI,
              null,
              null,
              null,
              null)) {
            return cursor.getCount();
        }
    }

    /**
     * Remove the expired Acronyms from Database.
     */
    public void removeExpiredAcronyms() {
        // Selection clause to delete acronym expansions that have
        // expired.
        final String SELECTION_EXPIRATION = 
            AcronymEntry.COLUMN_EXPIRATION_TIME
            + " <= ?";

        String[] selectionArgs = { 
            String.valueOf(System.nanoTime()) 
        };

        // TODO -- delete expired acronym expansions.
        try {
            Cursor expiredAcronyms = mContext.getContentResolver().query(AcronymEntry.CONTENT_URI,
                null, SELECTION_EXPIRATION, selectionArgs, null);
            if (expiredAcronyms != null && expiredAcronyms.moveToFirst()) {
                do {
                    final String deleteAcronym = expiredAcronyms.getString(
                        expiredAcronyms.getColumnIndex(AcronymEntry.COLUMN_ACRONYM));
                    remove(deleteAcronym);
                } while (expiredAcronyms.moveToNext());
            }
        } catch (Exception e) {
            android.util.Log.d(TAG, "Error caught: " + e.toString());
        }

    }

    /**
     * Helper method that uses AlarmManager to schedule Cache Cleanup
     * at regular intervals.
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
                 DeleteCacheReceiver.makeReceiverPendingIntent(context));
        }
    }

    /**
     * Helper method to check whether the Alarm is already active or
     * not.
     * 
     * @param context
     * @return boolean, whether the Alarm is already active or not
     */
    private boolean isAlarmActive(Context context) {        
    	return DeleteCacheReceiver.makeCheckAlarmPendingIntent
            (context) != null;
    }
}
