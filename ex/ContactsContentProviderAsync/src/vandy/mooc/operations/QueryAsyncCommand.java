package vandy.mooc.operations;

import vandy.mooc.common.AsyncCommand;
import vandy.mooc.common.Utils;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

/**
 * @classs QueryAsyncCommmand
 *
 * @brief Defines a command that asynchronously queries the
 *        ContentResolver for all the starred contacts and displays
 *        the results via a ListActivity passed as a parameter.  This
 *        class plays the role of the Concrete Command in the Command
 *        pattern.
 */
public class QueryAsyncCommand extends AsyncCommand {
    /**
     * Store a reference to the ContactsOps object.
     */
    final private ContactsOps mOps;

    /**
     * True if we only print the count of the number of entries that
     * matched the query.
     */
    final private boolean mPrintCountOnly;

   /**
     * Constructor stores the ContentResolver and ListActivity.
     */
    public QueryAsyncCommand(ContactsOps ops,
                             boolean printCountOnly) {
        // Get the ContentResolver from the Activity context.
        super(ops.getActivity().getContentResolver());

        // Store the ContactOps.
        mOps = ops;

        // Keep track of whether to only print the count of the
        // contacts queried, rather than their values.
        mPrintCountOnly = printCountOnly;
    }

    /**
     * Execute the command to asynchronously query the contacts in the
     * Iterator passed to the constructor.
     */
    public void execute() {
        // Columns to query.
        final String columnsToQuery[] = 
            new String[] {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.STARRED 
        };

        // Contacts to select.
        final String selection = 
            "((" 
            + Contacts.DISPLAY_NAME 
            + " NOTNULL) AND ("
            + Contacts.DISPLAY_NAME 
            + " != '' ) AND (" 
            + Contacts.STARRED
            + "== 1))";

        // Initiate an asynchronous query.
        startQuery(mPrintCountOnly == true ? 1 : 0,
                   null, 
                   ContactsContract.Contacts.CONTENT_URI, 
                   columnsToQuery, 
                   selection,
                   // ContactsContract.Contacts.STARRED /* + "= 0" */,
                   null, 
                   ContactsContract.Contacts._ID
                   + " ASC");        
    }

    /**
     * This method is called back by Android after the query on the
     * Contacts Provider finishes to perform the completion task.
     */
    public void onQueryComplete(int token,
                                Object cookie,
                                Cursor cursor) {
        if (cursor == null
            || cursor.getCount() == 0)
            return;
        else if (token == 1) {
            Utils.showToast(mOps.getActivity(),
                            cursor.getCount()
                            + " contact(s) inserted");
        } else {
            mOps.setCursor(cursor);
            mOps.getActivity().displayCursor(cursor);
        }

        // Execute the next AsyncCommand (if any) in the Iterator.
        super.executeNext();
    }
}

