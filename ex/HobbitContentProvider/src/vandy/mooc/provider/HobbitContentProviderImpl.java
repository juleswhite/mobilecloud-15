package vandy.mooc.provider;

import vandy.mooc.R;
import vandy.mooc.provider.CharacterContract.CharacterEntry;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * Content Provider used to store information about Hobbit characters.
 */
public abstract class HobbitContentProviderImpl {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        HobbitContentProvider.class.getSimpleName();

    protected Context mContext;

    /**
     * The code that is returned when a URI for more than 1 items is
     * matched against the given components.  Must be positive.
     */
    protected static final int CHARACTERS = 100;

    /**
     * The code that is returned when a URI for exactly 1 item is
     * matched against the given components.  Must be positive.
     */
    protected static final int CHARACTER = 101;

    /**
     * The URI Matcher used by this content provider.
     */
    protected static final UriMatcher sUriMatcher =
        buildUriMatcher();

    public HobbitContentProviderImpl(Context context) {
        mContext = context;
    }

    /**
     * Helper method to match each URI to the ACRONYM integers
     * constant defined above.
     * 
     * @return UriMatcher
     */
    protected static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code
        // to return when a match is found.  The code passed into the
        // constructor represents the code to return for the rootURI.
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = 
            new UriMatcher(UriMatcher.NO_MATCH);

        // For each type of URI that is added, a corresponding code is
        // created.
        matcher.addURI(CharacterContract.CONTENT_AUTHORITY,
                       CharacterContract.PATH_CHARACTER,
                       CHARACTERS);
        matcher.addURI(CharacterContract.CONTENT_AUTHORITY,
                       CharacterContract.PATH_CHARACTER
                       + "/#",
                       CHARACTER);
        return matcher;
    }

    /**
     * Columns in the "table".
     */
    public static final String[] sCOLUMNS =
        new String[] { "_ID",
                       CharacterEntry.COLUMN_NAME,
                       CharacterEntry.COLUMN_RACE };

    /**
     * Types of the columns in the "table".
     */
    public static final int[] sCOLUMNS_TYPES =
        new int[] { R.id.idString,
                    R.id.name,
                    R.id.race };

    /**
     * Method called to handle type requests from client applications.
     * It returns the MIME type of the data associated with each URI.
     */
    public synchronized String getType(Uri uri) {
        // Use Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        // Match the id returned by UriMatcher to return appropriate
        // MIME_TYPE.
        switch (match) {
        case CHARACTERS:
            return CharacterContract.CharacterEntry.CONTENT_ITEMS_TYPE;
        case CHARACTER:
            return CharacterContract.CharacterEntry.CONTENT_ITEM_TYPE;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }
    }

    /**
     * Method called to handle insert requests from client
     * applications.
     */
    public Uri insert(Uri uri,
                      ContentValues cvs) {
        Uri returnUri;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert a new
        // row.
        switch (sUriMatcher.match(uri)) {
        case CHARACTERS:
            returnUri = insertCharacters(uri, cvs);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Notifies registered observers that a row was inserted.
        mContext.getContentResolver().notifyChange(uri, 
                                                   null);
        return returnUri;
    }
    
    protected abstract Uri insertCharacters(Uri uri,
                                            ContentValues cvs);

    /**
     * Method that handles bulk insert requests.
     */
    public int bulkInsert(Uri uri,
                          ContentValues[] cvsArray) {
        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert new
        // rows.
        switch (sUriMatcher.match(uri)) {
        case CHARACTERS:
            int returnCount = bulkInsertCharacters(uri, cvsArray);

            // Notifies registered observers that rows were inserted.
            mContext.getContentResolver().notifyChange(uri, 
                                                       null);
            return returnCount;
        default:
            throw new UnsupportedOperationException();
        }
    }

    public abstract int bulkInsertCharacters(Uri uri,
                                             ContentValues[] cvsArray);
    
    /**
     * Method called to handle query requests from client
     * applications.
     */
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        MatrixCursor cursor =
            new MatrixCursor(sCOLUMNS);

        // Match the id returned by UriMatcher to query appropriate
        // rows.
        switch (sUriMatcher.match(uri)) {
        case CHARACTERS:
            cursor = (MatrixCursor) queryCharacters(uri, projection, selection, selectionArgs, sortOrder);
            break;
        case CHARACTER:
            cursor = (MatrixCursor) queryCharacter(uri, projection, selection, selectionArgs, sortOrder);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Register to watch a content URI for changes.
        cursor.setNotificationUri(mContext.getContentResolver(), 
                                  uri);
        return cursor;
    }
    
    public abstract Cursor queryCharacters(Uri uri,
                String[] projection,
                String selection,
                String[] selectionArgs,
                String sortOrder);
    
    public abstract Cursor queryCharacter(Uri uri,
                String[] projection,
                String selection,
                String[] selectionArgs,
                String sortOrder);

    /**
     * Method called to handle update requests from client
     * applications.
     */
    public int update(Uri uri,
                      ContentValues cvs,
                      String selection,
                      String[] selectionArgs) {
        int recsUpdated = 0;

        // Match the id returned by UriMatcher to update appropriate
        // rows.
        switch (sUriMatcher.match(uri)) {
        case CHARACTERS:
            recsUpdated = updateCharacters(uri, cvs, selection, selectionArgs);
            break;
        case CHARACTER:
            recsUpdated = updateCharacter(uri, cvs, selection, selectionArgs);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Notifies registered observers that a row was inserted.
        mContext.getContentResolver().notifyChange(uri, 
                                                   null);
        return recsUpdated;
    }

    public abstract int updateCharacters(Uri uri,
                ContentValues cvs,
                String selection,
                String[] selectionArgs);
    
    public abstract int updateCharacter(Uri uri,
                ContentValues cvs,
                String selection,
                String[] selectionArgs);
    /**
     * Method called to handle delete requests from client
     * applications.
     */
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {
        int recsDeleted = 0;
        
        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI) or -1 if
        // there is no matched node.  If a match is found delete the
        // appropriate rows.
        switch (sUriMatcher.match(uri)) {
        case CHARACTERS:
            recsDeleted = deleteCharacters(uri, selection, selectionArgs);
            break;
        case CHARACTER:
            recsDeleted = deleteCharacter(uri, selection, selectionArgs);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Notifies registered observers that rows were deleted.
        if (selection == null 
            || recsDeleted != 0) 
            mContext.getContentResolver().notifyChange(uri,
                                                       null);
        return recsDeleted;
    }
    
    public abstract int deleteCharacters(Uri uri,
                String selection,
                String[] selectionArgs);
    
    public abstract int deleteCharacter(Uri uri,
                String selection,
                String[] selectionArgs);

    /**
     * Return true if successfully started.
     */
    public boolean onCreate() {
        return true;
    }
}
