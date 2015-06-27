package vandy.mooc.activities;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.operations.ContactsOps;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * This Activity provides a facade for an application that shows how
 * to insert, query, and delete contacts into the Android
 * ContactsContentProvider.  It extends GenericActivity that provides
 * a framework for automatically handling runtime configuration
 * changes.
 */
public class ContactsActivity
       extends GenericActivity<ContactsOps> {
    /**
     * ListView displays the Contacts List.
     */
    private ListView mListView;

    /**
     * Columns to display.
     */
    private static final String sColumnsToDisplay [] = 
        new String[] {
        "_id", 	
        ContactsContract.Contacts.DISPLAY_NAME
    };
    
    /**
     * Resource Ids of the columns to display.
     */
    private static final int[] sColumnResIds = 
        new int[] { 
        R.id.idString, 	
        R.id.name 
    };

    /**
     * Used to display the results of contacts queried from the
     * ContactsContentProvider.
     */
    private SimpleCursorAdapter mAdapter;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., initializing
     * views.
     *
     * @param Bundle
     *            object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Set the layout of the MainActivity.
        setContentView(R.layout.contacts_activity);

        // Initialize the List View.
        mListView = (ListView) findViewById(R.id.list);

        // Initialize the SimpleCursorAdapter.
        mAdapter = new SimpleCursorAdapter(this,
                                           R.layout.list_layout, 
                                           null,
                                           sColumnsToDisplay, 
                                           sColumnResIds,
                                           1);

        // Connect the ListView with the SimpleCursorAdapter.
        mListView.setAdapter(mAdapter);

        // Call the special onCreate() method in GenericActivity,
        // passing in the ContactsOps class to instantiate and
        // manage.
        super.onCreate(savedInstanceState, 
                       ContactsOps.class);
    }

    /**
     * This click handler method inserts contacts into the
     * ContactsContentProvider.
     */
    public void insertContacts(View v) {
        // Insert contacts.
        getOps().runInsertContactCommand();
    }

    /**
     * This click handler method deletes contacts from the
     * ContactsContentProvider.
     */
    public void deleteContacts(View v) {
        // Delete contacts.
        getOps().runDeleteContactCommand();
    }

    /**
     * Display the contents of the cursor as a ListView.
     */
    public void displayCursor(Cursor cursor) {
    	// Display the designated columns in the cursor as a List in
        // the ListView connected to the SimpleCursorAdapter.
        mAdapter.changeCursor(cursor);
    }
}
