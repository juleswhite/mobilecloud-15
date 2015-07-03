package vandy.mooc.provider.cache;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that will remove all the expired acronyms at regular
 * intervals.
 * 
 */
public class DeleteCacheReceiver extends BroadcastReceiver {
    /**
     * Private request code for sender that is passed in an Intent.
     */
    public static final int DELETE_CACHE_REQUEST_CODE = 100;

    /**
     * Factory method to make a Pending Intent that is used by
     * AlarmManager to schedule Cache cleanup at regular intervals.
     * 
     * @param context
     * @return
     */
    public static PendingIntent makeReceiverPendingIntent(Context context) {
        // Use the Pending Intent that will send Intent to Receiver to
        // delete expired data from Database.
         return PendingIntent.getBroadcast(context,
                                          DELETE_CACHE_REQUEST_CODE,
                                          new Intent(context,
                                                     DeleteCacheReceiver.class),//pass in the intent to execute in the future
                                          PendingIntent.FLAG_CANCEL_CURRENT);//cancel current operation if it exist
    }
    
    /**
     * Factory method to make a Pending Intent that is used to check
     * whether the Alarm is already active or not.
     * 
     * @param context
     * @return null if its not created otherwise returns the Pending intent that started the broadcast reciever
     */
    public static PendingIntent makeCheckAlarmPendingIntent(Context context) {
    	// Check whether the Pending Intent already exists or not.
        return PendingIntent.getBroadcast
            (context,
             DELETE_CACHE_REQUEST_CODE,
             new Intent(context,
                        DeleteCacheReceiver.class),
             PendingIntent.FLAG_NO_CREATE);//
    }

    /**
     * Hook method that is called when the BroadcastReceiver is
     * receiving an Intent broadcast.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Create the TimeoutCache and remove all the expired acronyms
        // from the cache.
        // TODO -- you fill in here by calling the appropriate method in 
        // the ContentProviderTimeoutCache
        ContentProviderTimeoutCache timeoutCache= new ContentProviderTimeoutCache(context);
        timeoutCache.removeExpiredAcronyms();
        System.out.println(" Cache onrecieved called to remove the item from the cache");
    }
}
