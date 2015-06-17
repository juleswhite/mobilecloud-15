package vandy.mooc.operations;

import vandy.mooc.utils.GenericAsyncTask;
import vandy.mooc.utils.GenericAsyncTaskOps;
import vandy.mooc.utils.Utils;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class QueryContactsCommand
       implements GenericAsyncTaskOps<Void, Void, Cursor> {
    /**
     * Columns to query.
     */
    private final String sColumnsToQuery[] =
        new String[] {
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.STARRED 
    };
	
    /**
     * Contacts to select.
     */
    private final String sSelect = 
        "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
        + Contacts.DISPLAY_NAME + " != '' ) AND (" + Contacts.STARRED
        + "== 1))";

    /**
     * Store a reference to the ContactsOps object.
     */
    private ContactsOps mOps;

    /**
     * The GenericAsyncTask used to query contacts into the
     * ContactContentProvider.
     */
    private GenericAsyncTask<Void, Void, Cursor, QueryContactsCommand> mAsyncTask;

    /**
     * Constructor initializes the fields.
     */
    public QueryContactsCommand(ContactsOps ops) {
        // Store the ContactOps.
        mOps = ops;

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
    public Cursor doInBackground(Void v) {
        // Query the Contacts ContentProvider for the contacts and
        // return them.
        return queryAllContacts
            (mOps.getActivity().getApplicationContext().getContentResolver());
    }

    /**
     * The results of the query are displayed in the UI Thread.
     */
    @Override
    public void onPostExecute(Cursor cursor,
                              Void v) {
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
        // Perform a synchronous (blocking) query on the
        // ContactsContentProvider.
        return cr.query(ContactsContract.Contacts.CONTENT_URI, 
                        sColumnsToQuery, 
                        sSelect,
                        null, 
                        ContactsContract.Contacts._ID 
                        + " ASC");
    }
}

