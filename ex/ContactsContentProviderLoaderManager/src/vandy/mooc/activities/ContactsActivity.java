package vandy.mooc.activities;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.operations.ContactsOps;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * This Activity provides a facade for an application that shows how
 * to insert, query, and delete contacts into the Android
 * ContactsContentProvider.  It uses the Android LoaderManager to
 * perform the queries.  It extends GenericActivity that provides a
 * framework for automatically handling runtime configuration changes.
 */
public class ContactsActivity
       extends GenericActivity<ContactsOps> {
    /**
     * ListView displays the Contacts List.
     */
    private ListView mListView;

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

        // Call the special onCreate() method in GenericActivity,
        // passing in the ContactsOps class to instantiate and
        // manage.
        super.onCreate(savedInstanceState, 
                       ContactsOps.class);
        // Initialize the List View.
        mListView = (ListView) findViewById(R.id.list);

        // Connect the ListView with the SimpleCursorAdapter.
        mListView.setAdapter(getOps().makeCursorAdapter());
    }

    /**
     * This click handler method inserts contacts into the
     * ContactsContentProvider.
     */
    public void insertContacts(View v) {
        // Insert contacts.
        getOps().runInsertContactsCommand();
    }

    /**
     * This click handler method modifies contacts in the
     * ContactsContentProvider.
     */
    public void modifyContacts(View v) {
        // Modify contacts.
        getOps().runModifyContactsCommand();
    }

    /**
     * This click handler method deletes contacts from the
     * ContactsContentProvider.
     */
    public void deleteContacts(View v) {
        // Delete contacts.
        getOps().runDeleteContactsCommand();
    }
}
