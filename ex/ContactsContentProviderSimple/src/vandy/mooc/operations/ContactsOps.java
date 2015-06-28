package vandy.mooc.operations;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vandy.mooc.activities.ContactsActivity;
import vandy.mooc.common.ConfigurableOps;
import vandy.mooc.common.Utils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

/**
 * Class that implements the operations for inserting, querying, and
 * deleting contacts from the Android ContactsContentProvider.  It
 * implements ConfigurableOps so it can be managed by the
 * GenericActivity framework.
 */
public class ContactsOps implements ConfigurableOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Stores a Weak Reference to the ContactsActivity so the garbage
     * collector can remove it when it's not in use.
     */
    private WeakReference<ContactsActivity> mActivity;

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
     * Contains the most recent result from a query so the display can
     * be updated after a runtime configuration change.
     */
    private Cursor mCursor;

    /**
     * The list of contacts that we'll insert, query, and delete.
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
     * The list of contacts that we'll modify.
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
     * The types of ContactCommands.
     */
    enum ContactsCommandType {
        INSERT_COMMAND,
        QUERY_COMMAND,
        MODIFY_COMMAND,
        DELETE_COMMAND,
    }

    ContactsCommand mCommands[] =
        new ContactsCommand[ContactsCommandType.values().length];

    /**
     * This default constructor must be public for the GenericOps
     * class to work properly.
     */
    public ContactsOps() {
    }

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
        // Create a WeakReference to the activity.
        mActivity = 
            new WeakReference<>((ContactsActivity) activity);

        if (firstTimeIn) {
            // Initialize the Google account information.
            initializeAccount();

            initializeCommands();
        } else if (mCursor != null)
            // Redisplay the contents of the cursor after a runtime
            // configuration change.
            mActivity.get().displayCursor(mCursor);
    }

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
    public void runInsertContactCommand() {
        // Execute the InsertContactsCommand.
        mCommands[ContactsCommandType.INSERT_COMMAND.ordinal()].execute
            (mContacts.iterator());
    }

    /**
     * Modify the contacts.
     */
    public void runModifyContactCommand() {
        // Execute the InsertModifyCommand.
        mCommands[ContactsCommandType.MODIFY_COMMAND.ordinal()].execute
            (mModifyContacts.iterator());
    }

    /**
     * Delete the contacts.
     */
    public void runDeleteContactCommand() {
        // Execute the InsertDeleteCommand.
        mCommands[ContactsCommandType.DELETE_COMMAND.ordinal()].execute
            (mModifyContacts.iterator());
    }

    /**
     * Initialize the Google account.
     */
    private void initializeAccount() {
        // Get Account information.  Must have a Google account
        // configured on the device.
        mAccountList = 
            AccountManager.get(mActivity.get()
                               .getApplicationContext())
            .getAccountsByType("com.google");
        if (mAccountList == null)
            Utils.showToast(mActivity.get(),
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
     * Get the ContactsActivity.
     */
    public ContactsActivity getActivity() {
        return mActivity.get();
    }

    /**
     * Set the cursor.
     */
    public void setCursor(Cursor cursor) {
        mCursor = cursor;
    }
}
