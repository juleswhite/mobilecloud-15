package vandy.mooc.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Content Provider used to store information about Hobbit characters.
 */
public class HobbitContentProvider {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        HobbitContentProvider.class.getSimpleName();
    
    private HobbitContentProviderImpl mImpl;

    /**
     * Method called to handle type requests from client applications.
     * It returns the MIME type of the data associated with each URI.
     */
    public String getType(Uri uri) {
        return mImpl.getType(uri);
    }

    /**
     * Method called to handle insert requests from client
     * applications.
     */
    public Uri insert(Uri uri,
                      ContentValues cvs) {
        return mImpl.insert(uri, cvs);
    }

    /**
     * Method that handles bulk insert requests.
     */
    public int bulkInsert(Uri uri,
                          ContentValues[] cvsArray) {
        return mImpl.bulkInsert(uri, cvsArray);
    }

    /**
     * Method called to handle query requests from client
     * applications.
     */
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        return mImpl.query(uri, 
                           projection,
                           selection,
                           selectionArgs,
                           sortOrder);
    }

    /**
     * Method called to handle update requests from client
     * applications.
     */
    public int update(Uri uri,
                      ContentValues cvs,
                      String selection,
                      String[] selectionArgs) {
        return mImpl.update(uri,
                            cvs,
                            selection,
                            selectionArgs);
    }

    /**
     * Method called to handle delete requests from client
     * applications.
     */
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {
        return mImpl.delete(uri, 
                            selection,
                            selectionArgs);
    }

    /**
     * Return true if successfully started.
     */
    public boolean onCreate() {
        return mImpl.onCreate();
    }
}
