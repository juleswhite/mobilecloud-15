package vandy.mooc.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Useful helper methods and fields.
 */
public class Utils {
    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) activity
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }


    /**
     * Helper method that appends a given key id to the end of the
     * WHERE statement parameter.
     */
    public static String addKeyIdCheckToWhereStatement(String whereStatement, String id) {
        String newWhereStatement;
        if (TextUtils.isEmpty(whereStatement))
            newWhereStatement = "";
        else
            newWhereStatement = whereStatement + " AND ";
        // Append the key id to the end of the WHERE statement.
        return newWhereStatement+ id;
    }

    /**
     * Return a selection string that concatenates all the
     * @a selectionArgs for a given @a selection using the given @a
     * operation.
     */
    public static String addSelectionArgs(String selection,
                                    String [] selectionArgs,
                                    String operation) {
        //eg where id=2 or id=3 or id=3 , ....
        // Handle the "null" case.
        if (selection == null || selectionArgs == null)
            return null;
        else {
            String selectionResult = "";

            // Properly add the selection args to the selectionResult.
            for (int i = 0;  i < selectionArgs.length - 1; ++i)
                selectionResult += (selection
                        + " = ? "
                        + operation
                        + " ");

            // Handle the final selection case., so that it ends with ?
            selectionResult += (selection
                    + " = ?");


            return selectionResult;//return the result as a selection
        }
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
     * Make UtilsNet a utility class by preventing instantiation.
     */
    private Utils() {
        throw new AssertionError();
    }
}
