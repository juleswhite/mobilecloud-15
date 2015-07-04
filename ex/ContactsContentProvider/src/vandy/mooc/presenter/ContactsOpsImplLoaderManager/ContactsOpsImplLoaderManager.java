package vandy.mooc.presenter.ContactsOpsImplLoaderManager;

import vandy.mooc.presenter.ContactsOps;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.database.Cursor;

/**
 * Implements operations for inserting, querying, modifying, and
 * deleting contacts from the Android Contacts ContentProvider using
 * the Android LoaderManager.  It plays the role of the "Concrete
 * Implementor" in the Bridge pattern and also applies the Command
 * pattern to dispatch the various operations on the Contacts
 * ContentProvider.
 */
public class ContactsOpsImplLoaderManager 
       extends ContactsOpsImpl {
    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the ContactsOps object after it's been created.
     *
     * @param view     The currently active ContactsOps.View.  
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
            // Initialize all the ContactsCommand objects.
            initializeCommands();

            // Unregister the ContentObserver.
            unregisterContentObserver();
        }        else
            // Rerun the query to display anything relevant that's in
            // the ContentsProvider.
            queryContacts();
    }

    /**
     * Initialize all the ContactsCommands.
     */
    private void initializeCommands() {
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
        mCommands[ContactsCommandType.INSERT_COMMAND.ordinal()].execute
            (mContacts.iterator());
    }

    /**
     * Query the contacts.
     */
    public void queryContacts() {
        mCommands[ContactsCommandType.QUERY_COMMAND.ordinal()].execute
            (mContacts.iterator());
    }

    /**
     * Modify the contacts.
     */
    public void modifyContacts() {
        mCommands[ContactsCommandType.MODIFY_COMMAND.ordinal()].execute
            (mModifyContacts.iterator());
    }

    /**
     * Delete the contacts.
     */
    public void deleteContacts() {
        mCommands[ContactsCommandType.DELETE_COMMAND.ordinal()].execute
            (mModifyContacts.iterator());
    }

    /**
     * Display the contents of the cursor as a ListView.
     */
    public void displayCursor(Cursor cursor) {
    	// Display the designated columns in the cursor as a List in
        // the ListView connected to the SimpleCursorAdapter.
        mCursorAdapter.swapCursor(cursor);
    }
}
