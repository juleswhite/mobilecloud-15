package vandy.mooc.operations.ContactsOpsImplSimple;

import java.util.Iterator;

import vandy.mooc.operations.ContactsOpsImpl;
import vandy.mooc.utils.ContactsCommand;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

/**
 * Query all the star'd contacts in the UI thread.
 */
public class QueryContactsCommand
       implements ContactsCommand {
    /**
     * Store a reference to the ContactsOpsImpl object.
     */
    private ContactsOpsImpl mOps;

    /**
     * Store a reference to the Application context's ContentResolver.
     */
    private ContentResolver mContentResolver;

    /**
     * Constructor initializes the fields.
     */
    public QueryContactsCommand(ContactsOpsImpl ops) {
        // Store the ContactOps and the ContentResolver from the
        // Application context.
        mOps = ops;
        mContentResolver =
            ops.getActivity().getApplicationContext().getContentResolver();
    }

    /**
     * Run the command.
     */
    @Override
    public void execute(Iterator<String> ignore) {
        // Query the Contacts ContentProvider for the contacts and
        // return them.
        Cursor cursor = queryAllContacts(mContentResolver);

        if (cursor != null
            && cursor.getCount() != 0) {
            mOps.setCursor(cursor);
            mOps.displayCursor(cursor);
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
        return cr.query(ContactsContract.RawContacts.CONTENT_URI, 
                        columnsToQuery, 
                        selection,
                        null, 
                        ContactsContract.Contacts._ID
                        + " ASC");
    }
}

