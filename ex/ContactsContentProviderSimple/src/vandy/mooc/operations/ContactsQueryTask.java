package vandy.mooc.operations;

import vandy.mooc.utils.Utils;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class ContactsQueryTask
       extends AsyncTask<Void, Void, Cursor> {
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
     * Constructor initializes the field.
     */
    public ContactsQueryTask(ContactsOps ops) {
        mOps = ops;
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

    /**
     * Run in a background Thread to avoid blocking the UI Thread.
     */
    @Override
    public Cursor doInBackground(Void... v) {
        // Query the Contacts ContentProvider for the contacts and
        // return them.
        return queryAllContacts
            (mOps.getActivity().getApplicationContext().getContentResolver());
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
}

