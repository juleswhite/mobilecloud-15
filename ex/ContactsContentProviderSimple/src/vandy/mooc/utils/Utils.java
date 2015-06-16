package vandy.mooc.utils;

import vandy.mooc.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * @class Utils
 *
 * @brief Helper methods shared by various Activities.
 */
public class Utils {
    /**
     * Debugging tag.
     */
    private static final String TAG =
        Utils.class.getCanonicalName();

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                       message,
                       Toast.LENGTH_SHORT).show();
    }
        
    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    } 
}
