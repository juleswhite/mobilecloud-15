package vandy.mooc.operations.ContactsOpsImplSimple;

import java.util.Iterator;

import vandy.mooc.common.Command;
import vandy.mooc.operations.ContactsOpsImpl;
import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;

/**
 * Class that implements the operations for inserting, querying,
 * modifying, and deleting contacts from the Android Contacts
 * ContentProvider using basic Android ContentResolver methods.  It
 * plays the role of the "Concrete Implementor" in the Bridge pattern
 * and also applies the Command pattern to dispatch the various
 * operations on the Contacts ContentProvider.
 */
public class ContactsOpsImplSimple 
       extends ContactsOpsImpl {
    /**
     * The types of ContactCommands.
     */
    private enum ContactsCommandType {
        INSERT_COMMAND,
        QUERY_COMMAND,
        MODIFY_COMMAND,
        DELETE_COMMAND,
    }

    /**
     * Contains the most recent result from a query so the display can
     * be updated after a runtime configuration change.
     */
    private Cursor mCursor;

    /**
     * An array of Commands that are used to dispatch user button
     * presses to the right command.
     */
    @SuppressWarnings("unchecked")
    private Command<Iterator<String>> mCommands[] = (Command<Iterator<String>>[]) 
        new Command[ContactsCommandType.values().length];

    /**
     * Observer that's dispatched by the ContentResolver when Contacts
     * change (e.g., are inserted or deleted).
     */
    private final ContentObserver contactsChangeContentObserver =
        new ContentObserver(new Handler()) {
            /**
             * Trigger a query and display.
             */
            @Override
            public void onChange (boolean selfChange) {
                queryContacts();
            }
        };

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the ContactsOpsImpl object after it's been created.
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

        if (firstTimeIn) {
            // Initialize the ContactsCommands.
            initializeCommands();

            // Initialize the ContentObserver.
            initializeContentObserver();
        } else if (mCursor != null)
            // Redisplay the contents of the cursor after a runtime
            // configuration change.
            displayCursor(mCursor);
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
