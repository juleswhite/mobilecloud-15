package vandy.mooc.operations;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vandy.mooc.activities.ContactsActivity;
import vandy.mooc.utils.ConfigurableOps;
import vandy.mooc.utils.Utils;
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
            { "Jimmy Buffet", 
              "Jimmy Carter",
              "Jimmy Choo", 
              "Jiminy Cricket", 
              "Jimmy Fallon",
              "Jimmy Kimmel", 
              "Jimi Hendrix", 
              "Jimmy Johns",
              "Jimmy Johnson",
              "Jimmy Page", }));

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
     * Insert the contacts.
     */
    public void runInsertContactCommand() {
        // Reset mCursor and reset the display to show nothing.
        mActivity.get().displayCursor(mCursor = null);

        // Create a command that executes a GenericAsyncTask to
        // perform the insertions off the UI Thread.
        new InsertContactsCommand(this, 
                                  mContacts.iterator()).run();
    }

    /**
     * Query the contacts.
     */
    public void runQueryContactsCommand() {
        // Create a command that executes a GenericAsyncTask to
        // perform the Query off the UI Thread.
        new QueryContactsCommand(this).run();
    }

    /**
     * Delete the contacts.
     */
    public void runDeleteContactCommand() {
        // Create a command that executes a GenericAsyncTask to
        // perform the deletions off the UI Thread.
        new DeleteContactsCommand(this,
                                  mContacts.iterator()).run();
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
