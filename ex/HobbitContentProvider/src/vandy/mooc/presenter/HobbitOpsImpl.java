package vandy.mooc.presenter;

import java.lang.ref.WeakReference;

import vandy.mooc.R;
import vandy.mooc.model.CharacterContract;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Class that implements the operations for inserting, querying,
 * updating, and deleting characters from the HobbitContentProvider.
 * This class plays the role of the "Implementor" in the Bridge
 * pattern and the "Abstract Class" in the Template Method pattern.
 * It's also an example of the "External Polymorphism" pattern.
 */
public abstract class HobbitOpsImpl {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        HobbitOpsImpl.class.getSimpleName();

    /**
     * Stores a Weak Reference to the HobbitOps.View so the garbage
     * collector can remove it when it's not in use.
     */
    protected WeakReference<HobbitOps.View> mHobbitView;

    /**
     * Contains the most recent result from a query so the display can
     * be updated after a runtime configuration change.
     */
    private Cursor mCursor;

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the HobbitOpsImpl object after it's been created.
     *
     * @param view     The currently active HobbitOps.View.
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    public void onConfiguration(HobbitOps.View view,
                                boolean firstTimeIn) {
        // Create a WeakReference to the HobbitView.
        mHobbitView = new WeakReference<>(view);
        
        if (firstTimeIn == false 
            && mCursor != null)
            // Redisplay the contents of the cursor after a runtime
            // configuration change.
            mHobbitView.get().displayCursor(mCursor);
    }
    
    /**
    * Release resources to prevent leaks.
    */
    public void close() {
        // No-op.
    }

    /**
     * Return a @a SimpleCursorAdapter that can be used to display the
     * contents of the Hobbit ContentProvider.
     */
    public SimpleCursorAdapter makeCursorAdapter() {
        return new SimpleCursorAdapter
            (mHobbitView.get().getActivityContext(),
             R.layout.list_layout, 
             null,
             CharacterContract.CharacterEntry.sColumnsToDisplay,
             CharacterContract.CharacterEntry.sColumnResIds,
             1);
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

        // Index counter.
        int i = 0;

        // Insert all the characters into the ContentValues array.
        for (String character : characters) {
            ContentValues cvs = new ContentValues();
            cvs.put(CharacterContract.CharacterEntry.COLUMN_NAME,
                    character);
            cvs.put(CharacterContract.CharacterEntry.COLUMN_RACE,
                   race);
            cvsArray[i++] = cvs;            
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
                      null,
                      null);
    }

    /**
     * Update the @a race of a Hobbit character with a given
     * @a name in the HobbitContentProvider.  Plays the role of a
     * "template method" in the Template Method pattern.
     */
    public int updateRaceByName(String name,
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
        return delete(CharacterContract.CharacterEntry.CONTENT_URI,
                      CharacterContract.CharacterEntry.COLUMN_NAME,
                      characterNames);
    }

    /**
     * Delete an array of Hobbit @a characterRaces from the
     * HobbitContentProvider.  Plays the role of a "template method"
     * in the Template Method pattern.
     */
    public int deleteByRace(String[] characterRaces)
        throws RemoteException {
        return delete(CharacterContract.CharacterEntry.CONTENT_URI,
                      CharacterContract.CharacterEntry.COLUMN_RACE,
                      characterRaces);
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
     * Delete all characters from the HobbitContentProvider.  Plays
     * the role of a "template method" in the Template Method pattern.
     */
    public int deleteAll() 
        throws RemoteException {
        return delete(CharacterContract.CharacterEntry.CONTENT_URI,
                      null,
                      null);
    }

    /**
     * Display the current contents of the HobbitContentProvider.
     */
    public void displayAll()
        throws RemoteException {
        // Query for all the characters in the HobbitContentProvider.
        mCursor = query(CharacterContract.CharacterEntry.CONTENT_URI,
                        CharacterContract.CharacterEntry.sColumnsToDisplay,
                        CharacterContract.CharacterEntry.COLUMN_RACE,
                        new String[] { 
                                 "Dwarf",
                                 "Maia",
                                 "Hobbit",
                                 "Dragon",
                                 "Man",
                                 "Bear"
                             },
                       /* The following three null parameters could
                          also be this:

                        null,
                        null,
                        null,
                        */
                        null);
        if (mCursor.getCount() == 0) {
            Toast.makeText(mHobbitView.get().getActivityContext(), 
                           "No items to display",
                           Toast.LENGTH_SHORT).show();
            // Remove the display if there's nothing left to show.
            mHobbitView.get().displayCursor
                (mCursor = null);
        } else
            // Display the results of the query.
            mHobbitView.get().displayCursor
                (mCursor);
    }
}
