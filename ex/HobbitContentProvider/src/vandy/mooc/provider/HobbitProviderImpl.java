package vandy.mooc.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * Content Provider implementation used to manage Hobbit characters.
 * This class plays the role of the "Implementor" in the Bridge
 * pattern and the "Abstract Class" in the Template Method pattern.
 */
public abstract class HobbitProviderImpl {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        HobbitProvider.class.getSimpleName();

    /**
     * Context used for various ContentResolver activities.
     */
    protected Context mContext;

    /**
     * Constructor initializes the Context field.
     */
    public HobbitProviderImpl(Context context) {
        mContext = context;
    }

    /**
     * Method called to handle type requests from client applications.
     * It returns the MIME type of the data associated with each URI.
     */
    public String getType(Uri uri) {
        // Match the id returned by UriMatcher to return appropriate
        // MIME_TYPE.
        switch (CharacterContract.sUriMatcher.match(uri)) {
        case CharacterContract.CHARACTERS:
            return CharacterContract.CharacterEntry.CONTENT_ITEMS_TYPE;
        case CharacterContract.CHARACTER:
            return CharacterContract.CharacterEntry.CONTENT_ITEM_TYPE;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }
    }

    /**
     * Method called to handle insert requests from client
     * applications.  This method plays the role of the "template
     * method" in the Template Method pattern.
     */
    public Uri insert(Uri uri,
                      ContentValues cvs) {
        Uri returnUri;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert a new
        // row.
        switch (CharacterContract.sUriMatcher.match(uri)) {
        case CharacterContract.CHARACTERS:
            returnUri = insertCharacters(uri,
                                         cvs);
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

    /** 
     * Inserts @a ContentValues into the table.  This method plays the
     * role of the "abstract hook method" in the Template Method pattern.
     */   
    protected abstract Uri insertCharacters(Uri uri,
                                            ContentValues cvs);

    /**
     * Method that handles bulk insert requests.  This method plays
     * the role of the "template method" in the Template Method
     * pattern.
     */
    public int bulkInsert(Uri uri,
                          ContentValues[] cvsArray) {
        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert new
        // rows.
        switch (CharacterContract.sUriMatcher.match(uri)) {
        case CharacterContract.CHARACTERS:
            int returnCount = bulkInsertCharacters(uri,
                                                   cvsArray);

            if (returnCount > 0)
                // Notifies registered observers that row(s) were
                // inserted.
                mContext.getContentResolver().notifyChange(uri, 
                                                           null);
            return returnCount;
        default:
            throw new UnsupportedOperationException();
        }
    }

    /** 
     * Inserts an array of @a ContentValues into the table.  This
     * method plays the role of the "abstract hook method" in the
     * Template Method pattern.
     */   
    public abstract int bulkInsertCharacters(Uri uri,
                                             ContentValues[] cvsArray);
    
    /**
     * Method called to handle query requests from client
     * applications.  This method plays the role of the "template
     * method" in the Template Method pattern.
     */
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;

        // Match the id returned by UriMatcher to query appropriate
        // rows.
        switch (CharacterContract.sUriMatcher.match(uri)) {
        case CharacterContract.CHARACTERS:
            cursor = queryCharacters(uri,
                                     projection,
                                     selection,
                                     selectionArgs,
                                     sortOrder);
            break;
        case CharacterContract.CHARACTER:
            cursor = queryCharacter(uri,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    sortOrder);
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
    
    /** 
     * Queries for a @a selection in the entire table, relative to
     * the @a selectionArgs.  This method plays the role of the
     * "abstract hook method" in the Template Method pattern.
     */   
    public abstract Cursor queryCharacters
        (Uri uri,
         String[] projection,
         String selection,
         String[] selectionArgs,
         String sortOrder);
    
    /** 
     * Queries for a @a selection in a particular row of the table,
     * relative to the @a selectionArgs.  This method plays the role
     * of the "abstract hook method" in the Template Method pattern.
     */   
    public abstract Cursor queryCharacter
        (Uri uri,
         String[] projection,
         String selection,
         String[] selectionArgs,
         String sortOrder);

    /**
     * Method called to handle update requests from client
     * applications.  This method plays the role of the "template
     * method" in the Template Method pattern.
     */
    public int update(Uri uri,
                      ContentValues cvs,
                      String selection,
                      String[] selectionArgs) {
        int recsUpdated = 0;

        // Match the id returned by UriMatcher to update appropriate
        // rows.
        switch (CharacterContract.sUriMatcher.match(uri)) {
        case CharacterContract.CHARACTERS:
            recsUpdated = updateCharacters(uri,
                                           cvs,
                                           selection,
                                           selectionArgs);
            break;
        case CharacterContract.CHARACTER:
            recsUpdated = updateCharacter(uri,
                                          cvs,
                                          selection,
                                          selectionArgs);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        if (recsUpdated > 0)
            // Notifies registered observers that row(s) were
            // inserted.
            mContext.getContentResolver().notifyChange(uri, 
                                                       null);
        return recsUpdated;
    }

    /** 
     * Update a @a selection in the entire table, relative to the @a
     * selectionArgs.  This method plays the role of the "abstract hook method"
     * in the Template Method pattern.
     */   
    public abstract int updateCharacters
        (Uri uri,
         ContentValues cvs,
         String selection,
         String[] selectionArgs);
    
    /** 
     * Update a @a selection in a particular row of the table,
     * relative to the @a selectionArgs.  This method plays the role
     * of the "abstract hook method" in the Template Method pattern.
     */   
    public abstract int updateCharacter
        (Uri uri,
         ContentValues cvs,
         String selection,
         String[] selectionArgs);

    /**
     * Method called to handle delete requests from client
     * applications.  This method plays the role of the "template
     * method" in the Template Method pattern.
     */
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {
        int recsDeleted = 0;
        
        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI) or -1 if
        // there is no matched node.  If a match is found delete the
        // appropriate rows.
        switch (CharacterContract.sUriMatcher.match(uri)) {
        case CharacterContract.CHARACTERS:
            recsDeleted = deleteCharacters(uri,
                                           selection,
                                           selectionArgs);
            break;
        case CharacterContract.CHARACTER:
            recsDeleted = deleteCharacter(uri,
                                          selection,
                                          selectionArgs);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Notifies registered observers that row(s) were deleted.
        if (selection == null
            || recsDeleted != 0) 
            mContext.getContentResolver().notifyChange(uri,
                                                       null);
        return recsDeleted;
    }
    
    /** 
     * Delete a @a selection in the entire table, relative to the @a
     * selectionArgs.  This method plays the role of the "abstract
     * hook method" in the Template Method pattern.
     */   
    public abstract int deleteCharacters(Uri uri,
                String selection,
                String[] selectionArgs);
    
    /** 
     * Delete a @a selection in a particular row of the table,
     * relative to the @a selectionArgs.  This method plays the role
     * of the "abstract hook method" in the Template Method pattern.
     */   
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
