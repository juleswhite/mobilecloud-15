package vandy.mooc.common;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * @class Utils
 *
 * @brief Helper methods shared by various Activities.
 */
public class Utils {
    
    /**
     * Return an uppercase version of the input or null if user gave
     * no input (in which case a toast is displayed to this effect).
     */
    public static String uppercaseInput(Context context, 
                                        String input) {
        if (input.isEmpty()) {
            Utils.showToast(context,
                            "no input provided");
            return null;
        } else
            // Convert the input entered by the user so it's in
            // uppercase.
            return input.toUpperCase(Locale.ENGLISH);
    }

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
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
            (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
        
    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    } 
}
