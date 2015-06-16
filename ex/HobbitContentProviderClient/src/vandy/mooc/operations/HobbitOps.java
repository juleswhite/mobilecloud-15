package vandy.mooc.operations;

import java.lang.ref.WeakReference;

import vandy.mooc.R;
import vandy.mooc.activities.HobbitActivity;
import vandy.mooc.provider.CharacterContract;
import vandy.mooc.provider.HobbitContentProvider;
import vandy.mooc.utils.ConfigurableOps;
import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.widget.SimpleCursorAdapter;

/**
 * Class that implements the operations for inserting, querying,
 * updating, and deleting characters from the HobbitContentProvider.
 * It implements ConfigurableOps so it can be managed by the
 * GenericActivity framework.
 */
public class HobbitOps implements ConfigurableOps {
    /**
     * Stores a Weak Reference to the HobbitActivity so the garbage
     * collector can remove it when it's not in use.
     */
    private WeakReference<HobbitActivity> mActivity;

    /**
     * An optimized path to access the ContentProvider.
     */
    private ContentProviderClient mCpc;

    /**
     * Contains the most recent result from a query so the display can
     * be updated after a runtime configuration change.
     */
    private Cursor mCursor;

    /**
     * This default constructor must be public for the GenericOps
     * class to work properly.
     */
    public HobbitOps() {
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
    @Override
    public void onConfiguration(Activity activity,
                                boolean firstTimeIn) {
        // Create a WeakReference to the activity.
        mActivity = new WeakReference<>((HobbitActivity) activity);
        
        if (firstTimeIn) {
            // Get this Activity's ContentResolver.
            ContentResolver cr =
                activity.getApplicationContext().getContentResolver();

            // Get the ContentProviderClient associated with this
            // ContentResolver.
            mCpc = cr.acquireContentProviderClient
                (CharacterContract.CharacterEntry.CONTENT_URI);
        } else if (mCursor != null)
            // Redisplay the contents of the cursor after a runtime
            // configuration change.
            mActivity.get().displayCursor(makeCursorAdapter());
    }
    
    /**
    * Release the ContentProviderClient to prevent leaks.
    */
    public void close() {
        mCpc.release();
    }

    /**
     * Insert a Hobbit @a character of a particular @a race into the
     * HobbitContentProvider.
     */
    public Uri insert(String character,
                      String race) throws RemoteException {
        final ContentValues cvs = new ContentValues();

        // Insert data.
        cvs.put(CharacterContract.CharacterEntry.COLUMN_NAME,
                character);
        cvs.put(CharacterContract.CharacterEntry.COLUMN_RACE,
                race);
        return mCpc.insert(CharacterContract.CharacterEntry.CONTENT_URI,
                           cvs);
    }

    /**
     * Insert an array of Hobbit @a characters of a particular @a race
     * into the HobbitContentProvider.
     */
    public int bulkInsert(String[] characters,
                          String race) throws RemoteException {
        // Use ContentValues to store the values in appropriate
        // columns, so that ContentResolver can process it.  Since
        // more than one rows needs to be inserted, an Array of
        // ContentValues is needed.
        ContentValues[] cvsArray =
            new ContentValues[characters.length];

        // Insert all the characters into a ContentValues array.
        for (int i = 0; i < characters.length; i++) {
            ContentValues cvs = new ContentValues();
            cvs.put(CharacterContract.CharacterEntry.COLUMN_NAME,
                   characters[i]);
            cvs.put(CharacterContract.CharacterEntry.COLUMN_RACE,
                   race);
            cvsArray[i] = cvs;            
          }

        return mCpc.bulkInsert(CharacterContract.CharacterEntry.CONTENT_URI,
                               cvsArray);
    }
    
    /**
     * Delete an array of Hobbit @a characterNames from the
     * HobbitContentProvider.
     */
    public int deleteByName(String[] characterNames) throws RemoteException {
        final String selection =
            CharacterContract.CharacterEntry.COLUMN_NAME;
        final String[] selectionArgs = characterNames;

        return mCpc.delete
            (CharacterContract.CharacterEntry.CONTENT_URI,
             selection,
             selectionArgs);
    }

    /**
     * Delete an array of Hobbit @a characterRaces from the
     * HobbitContentProvider.
     */
    public int deleteByRace(String[] characterRaces) throws RemoteException {
        final String selection =
            CharacterContract.CharacterEntry.COLUMN_RACE;
        final String[] selectionArgs = characterRaces;

        return mCpc.delete
            (CharacterContract.CharacterEntry.CONTENT_URI,
             selection,
             selectionArgs);
    }

    /**
     * Update the @a name and @a race of a Hobbit character at a designated 
     * @a uri from the HobbitContentProvider.
     */
    public int update(Uri uri,
                      String name,
                      String race) throws RemoteException {
        final ContentValues cvs = new ContentValues();
        cvs.put(CharacterContract.CharacterEntry.COLUMN_NAME,
                name);
        cvs.put(CharacterContract.CharacterEntry.COLUMN_RACE,
                race);
        return mCpc.update(uri,
                             cvs,
                             (String) null,
                             (String[]) null);
    }

    /**
     * Display the current contents of the HobbitContentProvider.
     */
    public void display()
        throws RemoteException {
        // Query for all the characters in the HobbitContentProvider.
        mCursor = mCpc.query(CharacterContract.CharacterEntry.CONTENT_URI,
                             null,
                             null,
                             null,
                             null);
        // Display the results of the query.
        mActivity.get().displayCursor(makeCursorAdapter());
    }

    /**
     * A factory method that creates a SimpleCursorAdapter to display
     * the results of a query.
     */
    private SimpleCursorAdapter makeCursorAdapter() {
        return new SimpleCursorAdapter
            (mActivity.get(),
             R.layout.list_layout,
             mCursor,
             HobbitContentProvider.sCOLUMNS,
             HobbitContentProvider.sCOLUMNS_TYPES,
             1);
    }
}
