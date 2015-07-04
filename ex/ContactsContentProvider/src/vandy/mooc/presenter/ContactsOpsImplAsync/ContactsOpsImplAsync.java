package vandy.mooc.presenter.ContactsOpsImplAsync;

import vandy.mooc.common.AsyncProviderCommandAdapter;
import vandy.mooc.presenter.ContactsOps;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.database.Cursor;

/**
 * Implements operations for inserting, querying, modifying, and
 * deleting contacts from the Android Contacts ContentProvider using
 * Android AsyncQueryHanders.  It plays the role of the "Concrete
 * Implementor" in the Bridge pattern and also applies an variant of
 * the Command pattern to asynchronously dispatch the various
 * operations on the Contacts ContentProvider.
 */
public class ContactsOpsImplAsync
       extends ContactsOpsImpl {
    /**
     * Provides the target for asynchronous operations on a
     * ContentProvider and serves as the dispatcher of callbacks to
     * the onCompletion methods of AsyncProviderCommands after those
     * operations complete.
     */
    private AsyncProviderCommandAdapter<CommandArgs> mAdapter;

    /**
     * Accessor method that returns the AsyncProviderCommandAdapter.
     */
    public AsyncProviderCommandAdapter<CommandArgs> getAdapter() {
        return mAdapter;
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the ContactsOps object after it's been created.
     *
     * @param view         The currently active ContactsOps.View.  
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    public void onConfiguration(ContactsOps.View view,
                                boolean firstTimeIn) {
        super.onConfiguration(view,
                              firstTimeIn);

        if (firstTimeIn) {
            // Initialize the ContactsCommands.
            initializeCommands();

            // Unregister the ContentObserver.
            unregisterContentObserver();
            
            // Register the ContentObserver.
            registerContentObserver();
        } else if (mCursor != null)
            // Redisplay the contents of the cursor after a runtime
            // configuration change.
            displayCursor(mCursor);
    }

    /**
     * Initialize all the ContactsCommands.
     */
    private void initializeCommands() {
        // Create the AsyncProviderCommandAdapter.  This call *must*
        // come before the following calls that initialize the
        // commands.
        mAdapter =
            new AsyncProviderCommandAdapter<CommandArgs>
                (getApplicationContext().getContentResolver());

        // Create a command that executes a GenericAsyncTask to
        // perform the insertions off the UI Thread.
        mCommands[ContactsCommandType.INSERT_COMMAND.ordinal()] =
            new InsertContactsCommand(this);

        // Create a command that executes a GenericAsyncTask to
        // perform the queries off the UI Thread.
        mCommands[ContactsCommandType.QUERY_COMMAND.ordinal()] =
            new QueryContactsCommand(this);

        // Create a command that executes a GenericAsyncTask to
        // perform the modifications off the UI Thread.
        mCommands[ContactsCommandType.MODIFY_COMMAND.ordinal()] =
            new ModifyContactsCommand(this);

        // Create a command that executes a GenericAsyncTask to
        // perform the deletions off the UI Thread.
        mCommands[ContactsCommandType.DELETE_COMMAND.ordinal()] =
            new DeleteContactsCommand(this);
    }

    /**
     * Insert the contacts.
     */
    public void insertContacts() {
        // Execute the INSERT_COMMAND.
        mCommands[ContactsCommandType.INSERT_COMMAND.ordinal()].execute
            (mContacts.iterator());
    }

    /**
     * Query the contacts.
     */
    public void queryContacts() {
        // Execute the QUERY_COMMAND (which doesn't use the mContacts
        // iterator).
        mCommands[ContactsCommandType.QUERY_COMMAND.ordinal()].execute(null);
    }

    /**
     * Modify the contacts.
     */
    public void modifyContacts() {
        // Execute the MODIFY_COMMAND.
        mCommands[ContactsCommandType.MODIFY_COMMAND.ordinal()].execute
            (mModifyContacts.iterator());
    }

    /**
     * Delete the contacts.
     */
    public void deleteContacts() {
        // Execute the DELETE_COMMAND.
        mCommands[ContactsCommandType.DELETE_COMMAND.ordinal()].execute
            (mModifyContacts.iterator());
    }

    /**
     * Display the contents of the cursor as a ListView.
     */
    public void displayCursor(Cursor cursor) {
        // Store the most recent result from a query so the display
        // can be updated after a runtime configuration change.
        mCursor = cursor;

    	// Display the designated columns in the cursor as a List in
        // the ListView connected to the SimpleCursorAdapter.
        mCursorAdapter.changeCursor(cursor);
    }
}
