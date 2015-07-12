package vandy.mooc.presenter;

import vandy.mooc.model.CharacterContract;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

/**
 * Class that uses a ContentProviderClient to insert, query, update,
 * and delete characters from the HobbitContentProvider.  This class
 * plays the role of the "Concrete Implementor" in the Bridge pattern
 * and the "Concrete Class" in the TemplateMethod pattern.  It's also
 * an example of the "External Polymorphism" pattern.
 */
public class HobbitOpsContentProviderClient 
             extends HobbitOpsImpl {
    /**
     * Define an optimized Proxy for accessing the
     * HobbitContentProvider.
     */
    private ContentProviderClient mCpc;

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the HobbitOpsContentProviderClient object after it's
     * been created.
     *
     * @param view     The currently active HobbitOps.View.
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    @Override
    public void onConfiguration(HobbitOps.View view,
                                boolean firstTimeIn) {
        super.onConfiguration(view,
                              firstTimeIn);
        
        if (firstTimeIn) {
            // Get this Application context's ContentResolver.
            ContentResolver cr =
                view.getApplicationContext().getContentResolver();

            // Get the ContentProviderClient associated with this
            // ContentResolver.
            mCpc = cr.acquireContentProviderClient
                (CharacterContract.CharacterEntry.CONTENT_URI);
        } 
    }
    
    /**
    * Release the ContentProviderClient to prevent leaks.
    */
    public void close() {
        mCpc.release();
    }

    /**
     * Insert @a ContentValues into the HobbitContentProvider at
     * the @a uri.  Plays the role of an "concrete hook method" in the
     * Template Method pattern.
     */
    public Uri insert(Uri uri,
                      ContentValues cvs)
        throws RemoteException {
        return mCpc.insert(uri,
                           cvs);
    }

    /**
     * Insert an array of @a ContentValues into the
     * HobbitContentProvider at the @a uri.  Plays the role of an
     * "concrete hook method" in the Template Method pattern.
     */
    protected int bulkInsert(Uri uri,
                             ContentValues[] cvsArray)
        throws RemoteException {
        return mCpc.bulkInsert(uri,
                               cvsArray);
    }
    
    /**
     * Return a Cursor from a query on the HobbitContentProvider at
     * the @a uri.  Plays the role of an "concrete hook method" in the
     * Template Method pattern.
     */
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) 
        throws RemoteException {
        // Query for all the characters in the HobbitContentProvider.
        return mCpc.query(uri,
                          projection,
                          selection,
                          selectionArgs,
                          sortOrder);
    }

    /**
     * Delete the @a selection and @a selectionArgs with the @a
     * ContentValues in the HobbitContentProvider at the @a uri.
     * Plays the role of an "concrete hook method" in the Template
     * Method pattern.
     */
    public int update(Uri uri,
                      ContentValues cvs,
                      String selection,
                      String[] selectionArgs)
        throws RemoteException {
        return mCpc.update(uri,
                           cvs,
                           selection,
                           selectionArgs);
    }

    /**
     * Delete the @a selection and @a selectionArgs from the
     * HobbitContentProvider at the @a uri.  Plays the role of an
     * "concrete hook method" in the Template Method pattern.
     */
    protected int delete(Uri uri,
                         String selection,
                         String[] selectionArgs)
        throws RemoteException {
        return mCpc.delete
            (uri,
             selection,
             selectionArgs);
    }
}
