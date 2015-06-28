package vandy.mooc.operations.ContactsOpsImplAsync;

import vandy.mooc.common.AsyncCommand;
import vandy.mooc.common.GenericArrayIterator;
import vandy.mooc.common.MutableInt;
import vandy.mooc.common.Utils;
import vandy.mooc.operations.ContactsOpsImpl;
import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;

/**
 * Class that implements the operations for inserting, querying,
 * modifying, and deleting contacts from the Android Contacts
 * ContentProvider using Android AsyncQueryHanders.  It implements
 * ConfigurableOps so it can be managed by the GenericActivity
 * framework.  It plays the role of the "Concrete Implementor" in the
 * Bridge pattern and also applies an variant of the Command pattern
 * to asynchronous dispatch the various operations on the Contacts
 * ContentProvider.
 */
public class ContactsOpsImplAsync extends ContactsOpsImpl {
    /**
     * Contains the most recent result from a query so the display can
     * be updated after a runtime configuration change.
     */
    private Cursor mCursor;

    /**
     * Keeps track of the number of contacts inserted, deleted, and
     * modified.
     */
    private MutableInt mCounter = new MutableInt(0);

    /**
     * Observer that's dispatched by the ContentResolver when Contacts
     * change (e.g., are inserted or deleted).
     */
    private final ContentObserver contactsChangeContentObserver =
        new ContentObserver(new Handler()) {
            /**
             * Trigger a query and display the results.
             */
            @Override
            public void onChange (boolean selfChange) {
                queryContacts();
            }
        };

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the ContactsOps object after it's been created.
     *
     * @param activity     The currently active Activity.  
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    public void onConfiguration(Activity activity,
                                boolean firstTimeIn) {
        super.onConfiguration(activity,
                              firstTimeIn);

        if (firstTimeIn) 
            // Initialize the ContentObserver.
            initializeContentObserver();
        else if (mCursor != null)
            // Redisplay the contents of the cursor after a runtime
            // configuration change.
            displayCursor(mCursor);
    }

    /**
     * Initialize the ContentObserver.
     */
    public void initializeContentObserver() {
        // Register a ContentObserver that's notified when Contacts
        // change (e.g., are inserted or deleted).
        mActivity.get().getContentResolver().registerContentObserver
            (ContactsContract.Contacts.CONTENT_URI,
             true,
             contactsChangeContentObserver);
    }

    /**
     * Insert the contacts asynchronously.
     */
    public void insertContacts() {
        mCounter.setValue(0);

        // Start executing InsertAsyncCommand to insert the contacts
        // into the Contacts Provider.
        executeAsyncCommands
            (new AsyncCommand[] {
                new InsertAsyncCommand(this,
                                       mContacts.iterator(),
                                       mCounter),
                // Print a toast after all the contacts are inserted.
                makeToastAsyncCommand(" contact(s) inserted")
            });
    }

    /**
     * Query the contacts asynchronously.
     */
    public void queryContacts() {
        // Start executing the QueryAsyncCommand asynchronously, which
        // print out the inserted contacts when the query is done.
        executeAsyncCommands
            (new AsyncCommand[] {
                new QueryAsyncCommand(this)
            });
    }

    /**
     * Modify the contacts asynchronously.
     */
    public void modifyContacts() {
        mCounter.setValue(0);

        // Start executing the ModifyAsyncCommand, which runs
        // asynchronously.
        executeAsyncCommands
            (new AsyncCommand[] {
                new ModifyAsyncCommand(this,
                                       mModifyContacts.iterator(),
                                       mCounter),
                // Print a toast after all the contacts are modified.
                makeToastAsyncCommand(" contact(s) modified")
            });
    }

    /**
     * Delete the contacts asynchronously.
     */
    public void deleteContacts() {
        mCounter.setValue(0);

        // Start executing the DeleteAsyncCommand, which runs
        // asynchronously.
        executeAsyncCommands
            (new AsyncCommand[] {
                new DeleteAsyncCommand(this,
                                       mModifyContacts.iterator(),
                                       mCounter),
                // Print a toast after all the contacts are deleted.
                makeToastAsyncCommand(" contact(s) deleted")
            });
    }

    /**
     * Execute the array of asyncCommands passed as a parameter.
     */
    protected void executeAsyncCommands(AsyncCommand[] asyncCommands) {
        GenericArrayIterator<AsyncCommand> asyncCommandsIter = 
             new GenericArrayIterator<>(asyncCommands);

        // Pass the Iterator to each of the AsyncCommands passed as a
        // parameter.
        for (AsyncCommand asyncCommand : asyncCommands)
            asyncCommand.setIterator(asyncCommandsIter);

        // Start executing the first AsyncCommand in the chain of
        // AsyncCommands.
        asyncCommandsIter.next().execute();
    }

    /**
     * Print a toast after all the contacts are deleted.
     */
    private AsyncCommand makeToastAsyncCommand(final String message) {
        return new AsyncCommand(null) {
            public void execute() {
                Utils.showToast(mActivity.get(),
                                mCounter.getValue()
                                + message);
            }
        };
    }

    /**
     * Display the contents of the cursor as a ListView.
     */
    public void displayCursor(Cursor cursor) {
    	// Display the designated columns in the cursor as a List in
        // the ListView connected to the SimpleCursorAdapter.
        mAdapter.changeCursor(cursor);
    }

    /**
     * Set the cursor.
     */
    public void setCursor(Cursor cursor) {
        mCursor = cursor;
    }
}
