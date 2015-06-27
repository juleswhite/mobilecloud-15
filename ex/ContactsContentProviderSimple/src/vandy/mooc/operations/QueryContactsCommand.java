package vandy.mooc.operations;

import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.common.Utils;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class QueryContactsCommand
       extends GenericAsyncTaskOps<Void, Void, Cursor> {
    /**
     * Store a reference to the ContactsOps object.
     */
    private ContactsOps mOps;

    /**
     * Store a reference to the Application context's ContentResolver.
     */
    private ContentResolver mContentResolver;

    /**
     * The GenericAsyncTask used to query contacts into the
     * ContactContentProvider.
     */
    private GenericAsyncTask<Void, Void, Cursor, QueryContactsCommand> mAsyncTask;

    /**
     * Constructor initializes the fields.
     */
    public QueryContactsCommand(ContactsOps ops) {
        // Store the ContactOps and the ContentResolver from the
        // Application context.
        mOps = ops;
        mContentResolver =
            ops.getActivity().getApplicationContext().getContentResolver();

        // Create a GenericAsyncTask to query the contacts off the UI
        // Thread.
        mAsyncTask = new GenericAsyncTask<>(this);
    }

    /**
     * Run the command.
     */
    public void run() {
        // Execute the GenericAsyncTask.
        mAsyncTask.execute((Void) null);
    }

    /**
     * Run in a background Thread to avoid blocking the UI Thread.
     */
    @Override
    public Cursor doInBackground(Void... v) {
        // Query the Contacts ContentProvider for the contacts and
        // return them.
        return queryAllContacts(mContentResolver);
    }

    /**
     * The results of the query are displayed in the UI Thread.
     */
    @Override
    public void onPostExecute(Cursor cursor) {
        if (cursor == null
            || cursor.getCount() == 0)
            Utils.showToast(mOps.getActivity(),
                            "Contacts not found");
        else {
            mOps.setCursor(cursor);
            mOps.getActivity().displayCursor(cursor);
        }
    }

    /**
     * Synchronously query for contacts in the Contacts
     * ContentProvider.
     */
    public Cursor queryAllContacts(ContentResolver cr) {
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

        // Perform a synchronous (blocking) query on the
        // ContactsContentProvider.
        return cr.query(ContactsContract.Contacts.CONTENT_URI, 
                        columnsToQuery, 
                        selection,
                        null, 
                        ContactsContract.Contacts._ID
                        + " ASC");
    }
}

