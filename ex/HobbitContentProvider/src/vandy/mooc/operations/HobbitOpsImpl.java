package vandy.mooc.operations;

import java.lang.ref.WeakReference;

import vandy.mooc.R;
import vandy.mooc.activities.HobbitActivity;
import vandy.mooc.provider.CharacterContract;
import vandy.mooc.provider.HobbitContentProvider;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.widget.SimpleCursorAdapter;

/**
 * Class that implements the operations for inserting, querying,
 * updating, and deleting characters from the HobbitContentProvider.
 * This class plays the role of the "Implementor" in the Bridge
 * pattern and the "Abstract Class" in the TemplateMethod pattern.
 * It's also an example of the "External Polymorphism" pattern.
 */
public abstract class HobbitOpsImpl {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        HobbitOpsImpl.class.getSimpleName();

    /**
     * Stores a Weak Reference to the HobbitActivity so the garbage
     * collector can remove it when it's not in use.
     */
    protected WeakReference<HobbitActivity> mActivity;

    /**
     * Contains the most recent result from a query so the display can
     * be updated after a runtime configuration change.
     */
    private Cursor mCursor;

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the HobbitOpsImpl object after it's been created.
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
        mActivity = new WeakReference<>((HobbitActivity) activity);
        
        if (firstTimeIn == false 
            && mCursor != null)
            // Redisplay the contents of the cursor after a runtime
            // configuration change.
            mActivity.get().displayCursor(makeCursorAdapter());
    }
    
    /**
    * Release resources to prevent leaks.
    */
    public void close() {
        // No-op.
    }

    /**
     * Insert a Hobbit @a character of a particular @a race into the
     * HobbitContentProvider.  Plays the role of a "template method"
     * in the Template Method pattern.
     */
    public Uri insert(String character,
                      String race) throws RemoteException {
        final ContentValues cvs = new ContentValues();

        // Insert data.
        cvs.put(CharacterContract.CharacterEntry.COLUMN_NAME,
                character);
        cvs.put(CharacterContract.CharacterEntry.COLUMN_RACE,
                race);

        // Call to the hook method.
        return insert(CharacterContract.CharacterEntry.CONTENT_URI,
                      cvs);
    }

    /**
     * Insert @a ContentValues into the HobbitContentProvider at
     * the @a uri.  Plays the role of an "abstract hook method" in the
     * Template Method pattern.
     */
    protected abstract Uri insert(Uri uri,
                                  ContentValues cvs)
        throws RemoteException;

    /**
     * Insert an array of Hobbit @a characters of a particular @a race
     * into the HobbitContentProvider.  Plays the role of a "template
     * method" in the Template Method pattern.
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

        return bulkInsert
            (CharacterContract.CharacterEntry.CONTENT_URI,
             cvsArray);
    }
    
    /**
     * Insert an array of @a ContentValues into the
     * HobbitContentProvider at the @a uri.  Plays the role of an
     * "abstract hook method" in the Template Method pattern.
     */
    protected abstract int bulkInsert(Uri uri,
                                      ContentValues[] cvsArray)
        throws RemoteException;

    /**
     * Return a Cursor from a query on the HobbitContentProvider at
     * the @a uri.  Plays the role of an "abstract hook method" in the
     * Template Method pattern.
     */
    public abstract Cursor query(Uri uri,
                                 String[] projection,
                                 String selection,
                                 String[] selectionArgs,
                                 String sortOrder) 
        throws RemoteException;

    /**
     * Update the @a name and @a race of a Hobbit character at a
     * designated @a uri from the HobbitContentProvider.  Plays the
     * role of a "template method" in the Template Method pattern.
     */
    public int updateByUri(Uri uri,
                           String name,
                           String race) throws RemoteException {
        final ContentValues cvs = new ContentValues();
        cvs.put(CharacterContract.CharacterEntry.COLUMN_NAME,
                name);
        cvs.put(CharacterContract.CharacterEntry.COLUMN_RACE,
                race);
        return update(uri,
                      cvs,
                      CharacterContract.CharacterEntry.COLUMN_NAME,
                      new String[] { name });
    }

    /**
     * Update the @a name and @a race of a Hobbit character at a designated 
     * @a uri from the HobbitContentProvider.  Plays the role of a
     * "template method" in the Template Method pattern.
     */
    public int updateByName(String name,
                            String race) throws RemoteException {
        final ContentValues cvs = new ContentValues();
        cvs.put(CharacterContract.CharacterEntry.COLUMN_NAME,
                name);
        cvs.put(CharacterContract.CharacterEntry.COLUMN_RACE,
                race);
        return update(CharacterContract.CharacterEntry.CONTENT_URI,
                      cvs,
                      CharacterContract.CharacterEntry.COLUMN_NAME,
                      new String[] { name });
    }

    /**
     * Delete the @a selection and @a selectionArgs with the @a
     * ContentValues in the HobbitContentProvider at the @a uri.
     * Plays the role of an "abstract hook method" in the Template
     * Method pattern.
     */
    public abstract int update(Uri uri,
                               ContentValues cvs,
                               String selection,
                               String[] selectionArgs)
        throws RemoteException;

    /**
     * Delete an array of Hobbit @a characterNames from the
     * HobbitContentProvider.  Plays the role of a "template method"
     * in the Template Method pattern.
     */
    public int deleteByName(String[] characterNames)
        throws RemoteException {
        final String selection =
            CharacterContract.CharacterEntry.COLUMN_NAME;
        final String[] selectionArgs = characterNames;

        return delete(CharacterContract.CharacterEntry.CONTENT_URI,
                      selection,
                      selectionArgs);
    }

    /**
     * Delete an array of Hobbit @a characterRaces from the
     * HobbitContentProvider.  Plays the role of a "template method"
     * in the Template Method pattern.
     */
    public int deleteByRace(String[] characterRaces)
        throws RemoteException {
        final String selection =
            CharacterContract.CharacterEntry.COLUMN_RACE;
        final String[] selectionArgs = characterRaces;

        return delete(CharacterContract.CharacterEntry.CONTENT_URI,
                      selection,
                      selectionArgs);
    }

    /**
     * Delete the @a selection and @a selectionArgs from the
     * HobbitContentProvider at the @a uri.  Plays the role of an
     * "abstract hook method" in the Template Method pattern.
     */
    protected abstract int delete(Uri uri,
                                  String selection,
                                  String[] selectionArgs)
        throws RemoteException;

    /**
     * Display the current contents of the HobbitContentProvider.
     */
    public void display(String selection,
                        String[] selectionArgs)
        throws RemoteException {
        // Query for all the characters in the HobbitContentProvider.
        mCursor = query(CharacterContract.CharacterEntry.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null);
        // Display the results of the query.
        mActivity.get().displayCursor
            (makeCursorAdapter());
    }

    /**
     * Factory method that returns a SimpleCursorAdapter.
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
