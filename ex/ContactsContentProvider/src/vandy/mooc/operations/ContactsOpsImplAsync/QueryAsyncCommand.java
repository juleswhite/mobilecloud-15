package vandy.mooc.operations.ContactsOpsImplAsync;

import vandy.mooc.common.AsyncCommand;
import vandy.mooc.operations.ContactsOpsImpl;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

/**
 * Defines a command that asynchronously queries the ContentResolver
 * for all the starred contacts and displays the results via a
 * ListActivity passed as a parameter.  This class plays the role of
 * the Concrete Command in the Command pattern.
 */
public class QueryAsyncCommand extends AsyncCommand {
    /**
     * Store a reference to the ContactsOps object.
     */
    final private ContactsOpsImpl mOps;

   /**
     * Constructor stores the ContentResolver and ListActivity.
     */
    public QueryAsyncCommand(ContactsOpsImpl ops) {
        // Set the ContentResolver from the Activity context.
        super(ops.getActivity().getContentResolver());

        // Store the ContactOps.
        mOps = ops;
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
        startQuery(0,
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
        // Display the results if there are any.
        if (cursor != null
            && cursor.getCount() != 0) {
            mOps.setCursor(cursor);
            mOps.displayCursor(cursor);
        }

        // Execute the next AsyncCommand (if any) in the Iterator.
        super.executeNext();
    }
}

