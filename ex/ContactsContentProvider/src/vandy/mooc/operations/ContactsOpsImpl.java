package vandy.mooc.operations;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vandy.mooc.R;
import vandy.mooc.activities.ContactsActivity;
import vandy.mooc.common.Utils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.database.Cursor;
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
    protected WeakReference<ContactsActivity> mActivity;

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
    protected SimpleCursorAdapter mAdapter;

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
        // Create a WeakReference to the activity.
        mActivity = 
            new WeakReference<>((ContactsActivity) activity);

        if (firstTimeIn) {
            // Initialize the Google account information.
            initializeAccount();

            // Initialize the SimpleCursorAdapter.
            mAdapter = new SimpleCursorAdapter(activity.getApplicationContext(),
                                               R.layout.list_layout, 
                                               null,
                                               sColumnsToDisplay, 
                                               sColumnResIds,
                                               1);
        } 
    }

    /**
     * Insert the contacts.
     */
    public abstract void insertContacts();

    /**
     * Modify the contacts.
     */
    public abstract void modifyContacts();

    /**
     * Delete the contacts.
     */
    public abstract void deleteContacts();

    /**
     * Return the SimpleCursorAdapter.
     */ 
    public SimpleCursorAdapter makeCursorAdapter() {
        return mAdapter;
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
        /* no op */
    }

    /**
     * ...
     */
    public void displayCursor(Cursor cursor) {
        /* no op */
    }
}
