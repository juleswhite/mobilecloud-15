package vandy.mooc.operations;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vandy.mooc.activities.ContactsActivity;
import vandy.mooc.common.AsyncCommand;
import vandy.mooc.common.ConfigurableOps;
import vandy.mooc.common.GenericArrayIterator;
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

        if (firstTimeIn)
            // Initialize the Google account information.
            initializeAccount();
        else if (mCursor != null)
            // Redisplay the contents of the cursor after a runtime
            // configuration change.
            mActivity.get().displayCursor(mCursor);
    }

    /**
     * Insert the contacts asynchronously.
     */
    public void runInsertContactAsyncCommands() {
        // Reset mCursor and reset the display to show nothing.
        mActivity.get().displayCursor(mCursor = null);

        // Start executing InsertAsyncCommand to insert the contacts
        // into the Contacts Provider and then execute the
        // QueryAsyncCommand to print out the inserted contacts.  Both
        // commands run asynchronously.
        executeAsyncCommands
            (new AsyncCommand[]{
                new InsertAsyncCommand(this,
                                       mContacts.iterator()),
                new QueryAsyncCommand(this, true)
            });
    }

    /**
     * Query the contacts asynchronously.
     */
    public void runQueryContactsAsyncCommands() {
        // Start executing the QueryAsyncCommand asynchronously, which
        // print out the inserted contacts when the query is done.
        executeAsyncCommands
            (new AsyncCommand[]{
                new QueryAsyncCommand(this, false)
            });
    }

    /**
     * Delete the contacts asynchronously.
     */
    public void runDeleteContactAsyncCommands() {
        // Start executing the DeleteAsyncCommand, which runs
        // asynchronously.
        executeAsyncCommands
            (new AsyncCommand[]{
                new DeleteAsyncCommand(this,
                                       mContacts.iterator()),
                // Print a toast after all the contacts are deleted.
                new AsyncCommand(null) {
                	public void execute() {
                		Utils.showToast(mActivity.get(),
             			                "Contacts deleted");
                	}
                }
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
