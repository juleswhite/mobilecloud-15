package vandy.mooc.presenter;

import vandy.mooc.common.ConfigurableOps;
import vandy.mooc.common.ContextView;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.widget.SimpleCursorAdapter;

/**
 * Class that defines operations for inserting, querying, updating,
 * and deleting characters from the HobbitContentProvider.  This class
 * plays the role of the "Abstraction" in the Bridge pattern.  It
 * implements ConfigurableOps so it can be managed by the
 * GenericActivity framework.  This class and the hierarchy it
 * abstracts play the role of the "Presenter" in the
 * Model-View-Presenter pattern.
 */
public class HobbitOps 
             implements ConfigurableOps<HobbitOps.View> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        HobbitOps.class.getSimpleName();

    /**
     * This interface defines the minimum interface needed by the
     * HobbitOps class in the "Presenter" layer to interact with the
     * HobbitActivity in the "View" layer.
     */
    public interface View extends ContextView {
        /**
         * Display the contents of the cursor as a ListView.
         */
        void displayCursor(Cursor cursor);
    }

    /**
     * Type for accessing the ContentProvider (i.e., CONTENT_RESOLVER
     * or CONTENT_PROVIDER_CLIENT) for the HobbitOps implementation.
     */
    public enum ContentProviderAccessType {
        /**
         * Select the ContentResolver implementation.
         */
        CONTENT_RESOLVER,       

        /**
         * Select the ContentProviderClient implementation.
         */
        CONTENT_PROVIDER_CLIENT 
    }

    /**
     * Stores the type for accessing the ContentProvider (i.e.,
     * CONTENT_RESOLVER or CONTENT_PROVIDER_CLIENT) for the HobbitOps
     * implementation.
     */
    private ContentProviderAccessType mAccessType;
        
    /**
     * Reference to the designed Concrete Implementor (i.e., either
     * HobbitOpsContentResolver or HobbitOpsContentProviderClient).
     */
    private HobbitOpsImpl mHobbitOpsImpl;

    /**
     * This default constructor must be public for the GenericOps
     * class to work properly.
     */
    public HobbitOps() {
        setContentProviderAccessType
            (ContentProviderAccessType.CONTENT_RESOLVER);
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the HobbitOps object after it's been created.
     *
     * @param view     The currently active HobbitView.
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    @Override
        public void onConfiguration(HobbitOps.View view,
                                    boolean firstTimeIn) {
        mHobbitOpsImpl.onConfiguration(view,
                                       firstTimeIn);
    }
    
    /**
     * Release resources to prevent leaks.
     */
    public void close() {
        mHobbitOpsImpl.close();
    }

    /**
     * Return a @a SimpleCursorAdapter that can be used to display the
     * contents of the Hobbit ContentProvider.
     */
    public SimpleCursorAdapter makeCursorAdapter() {
        return mHobbitOpsImpl.makeCursorAdapter();
    }

    /**
     * Insert a Hobbit @a character of a particular @a race into the
     * HobbitContentProvider.
     */
    public Uri insert(String character,
                      String race) throws RemoteException {
        return mHobbitOpsImpl.insert(character,
                                     race);
    }

    /**
     * Insert an array of Hobbit @a characters of a particular @a race
     * into the HobbitContentProvider.
     */
    public int bulkInsert(String[] characters,
                          String race) throws RemoteException {
        return mHobbitOpsImpl.bulkInsert(characters,
                                         race);
    }

    /**
     * Update the @a name and @a race of a Hobbit character at a designated 
     * @a uri from the HobbitContentProvider.
     */
    public int updateByUri(Uri uri,
                           String name,
                           String race) throws RemoteException {
        return mHobbitOpsImpl.updateByUri(uri,
                                          name,
                                          race);
    }

    /**
     * Update the @a race of a Hobbit character with the given @a
     * name.
     */
    public int updateRaceByName(String name,
                                String race) throws RemoteException {
        return mHobbitOpsImpl.updateRaceByName(name,
                                               race);
    }

    /**
     * Delete an array of Hobbit @a characterNames from the
     * HobbitContentProvider.
     */
    public int deleteByName(String[] characterNames) 
        throws RemoteException {
        return mHobbitOpsImpl.deleteByName(characterNames);
    }

    /**
     * Delete an array of Hobbit @a characterRaces from the
     * HobbitContentProvider.
     */
    public int deleteByRace(String[] characterRaces)
        throws RemoteException {
        return mHobbitOpsImpl.deleteByRace(characterRaces);
    }

    /**
     * Delete all characters in the HobbitContentProvider.
     */
    public int deleteAll()
        throws RemoteException {
        return mHobbitOpsImpl.deleteAll();
    }

    /**
     * Display the current contents of the HobbitContentProvider.
     */
    public void displayAll()
        throws RemoteException {
        mHobbitOpsImpl.displayAll();
    }

    /**
     * Sets the type for accessing the ContentProvider (i.e.,
     * CONTENT_RESOLVER or CONTENT_PROVIDER_CLIENT) for the HobbitOps
     * implementation.
     */
    public void setContentProviderAccessType(ContentProviderAccessType accessType) {
        // Select the appropriate type of access to the Content
        // Provider.
        if (mAccessType != accessType) {
            mAccessType = accessType;
            switch(mAccessType) {
            case CONTENT_RESOLVER:
                mHobbitOpsImpl =
                    new HobbitOpsContentResolver();
                break;
            case CONTENT_PROVIDER_CLIENT:
                mHobbitOpsImpl =
                    new HobbitOpsContentProviderClient();
                break;
            }
        }
    }
}
