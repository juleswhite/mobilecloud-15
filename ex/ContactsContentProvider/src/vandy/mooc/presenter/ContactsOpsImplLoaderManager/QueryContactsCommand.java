package vandy.mooc.presenter.ContactsOpsImplLoaderManager;

import java.util.Iterator;

import vandy.mooc.common.Command;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class QueryContactsCommand
       implements LoaderManager.LoaderCallbacks<Cursor>,
                  Command<Iterator<String>> {
    /**
     * Store a reference to the ContactsOpsImpl object.
     */
    private ContactsOpsImpl mOps;

    /**
     * Constructor initializes the fields.
     */
    public QueryContactsCommand(ContactsOpsImpl ops) {
        // Store the ContactOps.
        mOps = ops;

        // Initialize the LoaderManager. 
        execute(null);
    }

    /**
     * Run the command to initialize the LoaderManager.
     */
    public void execute(Iterator<String> ignored) {
        // Initialize the LoaderManager. 
        mOps.getLoaderManager().initLoader(0,
                                           null,
                                           this);
    }

    /**
     * This hook method is called back by the LoaderManager when the
     * LoaderManager is initialized.
     */
    public Loader<Cursor> onCreateLoader(int id, 
                                         Bundle args) {
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

        // Create a new CursorLoader that will perform the query
        // asynchronously.
        return new CursorLoader(mOps.getActivityContext(),
                                ContactsContract.Contacts.CONTENT_URI,
                                columnsToQuery,
                                selection,
                                null,
                                Contacts._ID 
                                + " ASC");
    }

    /**
     * This hook method is called back when the query completes.
     */
    public void onLoadFinished(Loader<Cursor> loader,
                               Cursor cursor) {
        if (cursor != null
            && cursor.getCount() != 0)
            // Call displayCursor() to swap the Cursor data with the
            // adapter, which will display the results.
            mOps.displayCursor(cursor);
    }

    /**
     * This hook method is called back when the loader is reset.
     */
    public void onLoaderReset(Loader<Cursor> loader) {
        mOps.displayCursor(null);
    }
}

