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
public class HobbitOpsContentProviderClient 
             extends HobbitOpsImpl {
    /**
     * An optimized path to access the ContentProvider.
     */
    private ContentProviderClient mCpc;

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the HobbitOpsContentProviderClient object after it's
     * been created.
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
        super.onConfiguration(activity, firstTimeIn);
        
        if (firstTimeIn) {
            // Get this Application context's ContentResolver.
            ContentResolver cr =
                activity.getApplicationContext().getContentResolver();

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
     * the @a uri.
     */
    public Uri insert(Uri uri,
                      ContentValues cvs)
        throws RemoteException {
        return mCpc.insert(uri,
                           cvs);
    }

    /**
     * Insert an array of @a ContentValues into the
     * HobbitContentProvider at the @a uri.
     */
    protected int bulkInsert(Uri uri,
                             ContentValues[] cvsArray)
        throws RemoteException {
        return mCpc.bulkInsert(uri,
                               cvsArray);
    }
    
    /**
     * Return a Cursor from a query on the HobbitContentProvider at
     * the @a uri.
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
     * HobbitContentProvider at the @a uri.
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
