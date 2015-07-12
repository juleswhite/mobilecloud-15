package vandy.mooc.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import vandy.mooc.R;
import vandy.mooc.common.Command;
import vandy.mooc.common.Utils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.LoaderManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

/**
 * Class that defines the root of a hierarchy of subclasses that
 * implement the operations for inserting, querying, modifying, and
 * deleting contacts from the Android ContactsContentProvider.  This
 * class plays the role of the "Implementor" in the Bridge pattern.
 */
public abstract class ContactsOpsImpl {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        ContactsOpsImpl.class.getSimpleName();

    /**
     * Stores a Weak Reference to the ContactsActivity so the garbage
     * collector can remove it when it's not in use.
     */
    private WeakReference<ContactsOps.View> mContactsView;

    /**
     * The types of ContactCommands.
     */
    protected enum ContactsCommandType {
        INSERT_COMMAND,
        QUERY_COMMAND,
        MODIFY_COMMAND,
        DELETE_COMMAND,
    }

    /**
     * An array of Commands that are used to dispatch user button
     * presses to the right Command object.
     */
    @SuppressWarnings("unchecked")
    protected Command<Iterator<String>> mCommands[] = (Command<Iterator<String>>[]) 
        new Command[ContactsCommandType.values().length];

    /**
     * Contains the most recent result from a query so the display can
     * be updated after a runtime configuration change.
     */
    protected Cursor mCursor;

    /**
     * Columns to display.
     */
    protected static final String sColumnsToDisplay [] = 
        new String[] {
        "_id", 	
        ContactsContract.Contacts.DISPLAY_NAME
    };
    
    /**
     * Resource Ids of the columns to display.
     */
    protected static final int[] sColumnResIds = 
        new int[] { 
        R.id.idString, 	
        R.id.name 
    };

    /**
     * Must have a Google account set up on the device.
     */
    protected Account[] mAccountList;

    /**
     * The type of the Google account.
     */
    protected String mAccountType;

    /**
     * The name of the Google account.
     */
    protected String mAccountName;

    /**
     * The list of contacts that we'll insert and query.
     */
    protected final List<String> mContacts =
        new ArrayList<String>(Arrays.asList(new String[] 
            { "Jimmy Buffett",
              "Jimmy Carter",
              "Jimmy Choo", 
              "Jimmy Connors", 
              "Jiminy Cricket", 
              "Jimmy Durante",
              "Jimmy Fallon",
              "Jimmy Kimmel", 
              "Jimi Hendrix", 
              "Jimmy Johns",
              "Jimmy Johnson",
              "Jimmy Swaggart", 
            }));

    /**
     * The list of contacts that we'll modify and delete.
     */
    protected final List<String> mModifyContacts =
        new ArrayList<String>(Arrays.asList(new String[] 
            { 
                "Jiminy Cricket", 
                "James Cricket",
                "Jimi Hendrix", 
                "James Hendix",
                "Jimmy Buffett",
                "James Buffett",
                "Jimmy Carter",
                "James Carter",
                "Jimmy Choo", 
                "James Choo", 
                "Jimmy Connors", 
                "James Connors", 
                "Jimmy Durante",
                "James Durante",
                "Jimmy Fallon",
                "James Fallon",
                "Jimmy Kimmel", 
                "James Kimmel", 
                "Jimmy Johns",
                "James Johns",
                "Jimmy Johnson",
                "James Johnson",
                "Jimmy Page", 
                "James Page", 
                "Jimmy Swaggart", 
                "James Swaggart", 
            }));

    /**
     * Used to display the results of contacts queried from the
     * ContactsContentProvider.
     */
    protected SimpleCursorAdapter mCursorAdapter;

    /**
     * Observer that's dispatched by the ContentResolver when Contacts
     * change (e.g., are inserted, modified, or deleted).
     */
    protected final ContentObserver contactsChangeContentObserver =
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
     * initialize the ContactsOpsImpl object after it's been created.
     *
     * @param view     The currently active ContactsOps.View.
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    public void onConfiguration(ContactsOps.View view,
                                boolean firstTimeIn) {
        // Create a WeakReference to the ContactsOps.View.
        mContactsView = 
            new WeakReference<>(view);

        if (firstTimeIn) {
            // Initialize the Google account information.
            initializeAccount();

            // Initialize the SimpleCursorAdapter.
            mCursorAdapter =
                new SimpleCursorAdapter(view.getApplicationContext(),
                                        R.layout.list_layout, 
                                        null,
                                        sColumnsToDisplay, 
                                        sColumnResIds,
                                        1);
        } 
    }

    /**
     * Register the ContentObserver.
     */
    protected void registerContentObserver() {
        // Register a ContentObserver that's notified when Contacts
        // change (e.g., are inserted, modified, or deleted).
        getActivityContext().getContentResolver().registerContentObserver
            (ContactsContract.Contacts.CONTENT_URI,
             true,
             contactsChangeContentObserver);
    }

    /**
     * Unregister the ContentObserver.
     */
    protected void unregisterContentObserver() {
        // Unregister a ContentObserver so it won't be notified when
        // Contacts change (e.g., are inserted, modified, or deleted).
        getActivityContext().getContentResolver().unregisterContentObserver
            (contactsChangeContentObserver);
    }

    /**
     * Insert the contacts.
     */
    public abstract void insertContacts();

    /**
     * Query the contacts.
     */
    public abstract void queryContacts();

    /**
     * Modify the contacts.
     */
    public abstract void modifyContacts();

    /**
     * Delete the contacts.
     */
    public abstract void deleteContacts();

    /**
     * Factory method that returns the SimpleCursorAdapter.
     */ 
    public SimpleCursorAdapter makeCursorAdapter() {
        return mCursorAdapter;
    }
    
    /**
     * Initialize the Google account.
     */
    private void initializeAccount() {
        // Get Account information.  Must have a Google account
        // configured on the device.
        mAccountList = 
            AccountManager.get(getApplicationContext())
            .getAccountsByType("com.google");
        if (mAccountList == null)
            Utils.showToast(getActivityContext(),
                            "google account not configured");
        else {
            try {
                mAccountType = mAccountList[0].type;
                mAccountName = mAccountList[0].name;
            } catch (Exception e) {
                Log.d(TAG, 
                      "google account not configured" 
                      + e);
            }
        }
    }

    /* 
     * Getters and settings for fields contained in ContactsOps.
     */

    /**
     * Get the account type.
     */
    public String getAccountType() {
        return mAccountType;
    }

    /**
     * Get the account name.
     */
    public String getAccountName() {
        return mAccountName;
    }

    /**
     * Get the Activity Context.
     */
    public Context getActivityContext() {
        return mContactsView.get().getActivityContext();
    }

    /**
     * Get the Application Context.
     */
    public Context getApplicationContext() {
        return mContactsView.get().getApplicationContext();
    }

    /**
     * Get the LoaderManager.
     */
    public LoaderManager getLoaderManager() {
        return mContactsView.get().getLoaderManager();
    }

    /**
     * Display the cursor.
     */
    public abstract void displayCursor(Cursor cursor);
}
