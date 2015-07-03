package vandy.mooc.provider.cache;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.provider.AcronymContract.AcronymEntry;
import vandy.mooc.retrofit.AcronymData.AcronymExpansion;

/**
 * Timeout cache that uses Content Providers to cache data and uses
 * AlarmManager and Broadcast Receiver to remove expired cache entries
 * periodically.
 */
public class ContentProviderTimeoutCache
       implements TimeoutCache<String, List<AcronymExpansion>> {
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
        //the cursor selects all results with the expiration time
        try (Cursor cursor =
             mContext.getContentResolver().query
             (AcronymEntry.CONTENT_URI,
              null,//select all the columns
              SELECTION_ACRONYM,//where the acronyms
              selectionArgs,//equals this acronym
              null)) {//use the default order
            // If there are not matches in the database return false. 
            if (!cursor.moveToFirst()){
                System.out.println(" return null; no items in cursor");
                 return null;
            }


            // Since all rows with same acronym have same expiration
            // time, check the expiration of first row.  If its
            // expired, then return null and start a thread to delete
            // all rows having that acronym else get the Acronym Data
            // from first row and add it to the List.
            else {
                 // given acronym that's obtained from the cursor., check the first row if the time is expired
                Long expirationTime = cursor.getLong(cursor.getColumnIndex(AcronymEntry.COLUMN_EXPIRATION_TIME));
                System.out.println("current time"+System.nanoTime()+" expiration time"+ expirationTime);
                // Check if the acronym is expired. If true, then
                // remove it.
                if (System.nanoTime() > expirationTime) {//get the elapsed time. NOte: this retrieves an accurate result but incures overhead of nanoseconds to execute
                    // if acronym is expired, Start a thread to delete all rows having that
                    // acronym.
                    new Thread(new Runnable() {
                            public void run() {
                                remove(acronym);
                                System.out.println("just removed an acronym, bcs has expired");
                            }
                        }).start();
                    return null;
                } else {
                    List<AcronymExpansion> longForms = new ArrayList<>();
                    System.out.println("acronym has not expired");
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
     * This is a Factory method that is used to Get the acronym expansions data from the databaseCursor.
     * 
     * @param cursor
     * @return AcronymExpansion
     */
    private AcronymExpansion getAcronymExpansion(Cursor cursor) {
         // obtained from the cursor.
        String longForm = cursor.getString(cursor.getColumnIndex(AcronymEntry.COLUMN_LONG_FORM));
         // obtained from the cursor.
        int frequency = cursor.getInt(cursor.getColumnIndex(AcronymEntry.COLUMN_FREQUENCY));
         // obtained from the cursor.
        int since = cursor.getInt(cursor.getColumnIndex(AcronymEntry.COLUMN_SINCE));
        return new AcronymExpansion(longForm, frequency, since);
    }

    /**
     * Put the @a longForms into the cache at the designated @a
     * acronym with the default timeout.
     * 
     * @param acronym
     * @param longForms
     */
    public void put(String acronym, List<AcronymExpansion> longForms) {
        putValues(acronym, longForms, mDefaultTimeout);
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
                  timeout * 1000 * 1000 * 1000);//x10^9
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
        if (longForms == null || longForms.isEmpty()) 
            return -1;

        // Calculate the Expiration time starting from now(total time since boot time).
        Long expirationTime = System.nanoTime() + timeout;//get the current elapsed time since some fixed arbitrary point(may even be in future) using the cpu time
		//set the expiration time to some time in the future from the elapsed time.
        System.out.println(" expiration time to put"+ expirationTime);
        // Use ContentValues to store the values in appropriate
        // columns, so that ContentResolver can process it.  Since
        // more than one rows needs to be inserted, an Array of
        // ContentValues is needed.
        ContentValues[] cvArray =
            new ContentValues[longForms.size()];

        for (int i = 0; i < longForms.size(); i++) {
            AcronymExpansion acronymExpansion=longForms.get(i);
            ContentValues contentValues= new ContentValues();
            contentValues.put(AcronymEntry.COLUMN_ACRONYM,acronym);
            contentValues.put(AcronymEntry.COLUMN_LONG_FORM,acronymExpansion.getLf());
            contentValues.put(AcronymEntry.COLUMN_FREQUENCY,acronymExpansion.getFreq());
            contentValues.put(AcronymEntry.COLUMN_SINCE,acronymExpansion.getSince());
            contentValues.put(AcronymEntry.COLUMN_EXPIRATION_TIME,expirationTime);
            cvArray[i]=contentValues;

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

        mContext.getContentResolver().delete(AcronymEntry.CONTENT_URI,SELECTION_ACRONYM,selectionArgs);
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
            String.valueOf(System.nanoTime()) //current time stamp of the most accurate time on the phone. It resets to 0 during boot
        };

        mContext.getContentResolver().delete(AcronymEntry.CONTENT_URI,SELECTION_EXPIRATION,selectionArgs);
        System.out.println(" removedExpirationAcronyms called to delete expired acronyms");
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
            mAlarmManager.setInexactRepeating//android synchronizes multiple time of other apps and fires them at the same time,
                    //this causes an improvement in the battery life of the app
                (AlarmManager.ELAPSED_REALTIME_WAKEUP,//  wake up the device and do it now then go to sleep.
                 SystemClock.elapsedRealtime()//takes millisecons since boot + sleepTime into consideration and schedules for another interval
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
