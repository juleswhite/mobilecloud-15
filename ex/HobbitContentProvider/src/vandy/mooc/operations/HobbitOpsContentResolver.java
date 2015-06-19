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
public class HobbitOpsContentResolver
             extends HobbitOpsImpl {
    /**
     * 
     */
    private ContentResolver mCr;

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
        
        if (firstTimeIn) 
            // Store the Application context's ContentResolver.
            mCr = 
                activity.getApplicationContext().getContentResolver();
    }
    
    /**
     * Insert @a ContentValues into the HobbitContentProvider at
     * the @a uri.
     */
    public Uri insert(Uri uri,
                      ContentValues cvs)
        throws RemoteException {
        return mCr.insert(uri,
                          cvs);
    }

    /**
     * Insert an array of @a ContentValues into the
     * HobbitContentProvider at the @a uri.
     */
    protected int bulkInsert(Uri uri,
                             ContentValues[] cvsArray)
        throws RemoteException {
        return mCr.bulkInsert(uri,
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
        return mCr.query(uri,
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
        return mCr.update(uri,
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
        return mCr.delete
            (uri,
             selection,
             selectionArgs);
    }
}
