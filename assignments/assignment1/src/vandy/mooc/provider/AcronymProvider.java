package vandy.mooc.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Content Provider to access Acronym Database.
 */
public class AcronymProvider extends ContentProvider {
    /**
     * Debugging tag used by the Android logger.
     */
    private static final String TAG =
        AcronymProvider.class.getSimpleName();

    /**
     * Use AcronymDatabaseHelper to manage database creation and version
     * management.
     */
    private AcronymDatabaseHelper mOpenHelper;

    /**
     * The code that is returned when a URI for more than 1 items is
     * matched against the given components.  Must be positive.
     */
    private static final int ACRONYMS = 100;

    /**
     * The code that is returned when a URI for exactly 1 item is
     * matched against the given components.  Must be positive.
     */
    private static final int ACRONYM = 101;

    /**
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher =
        buildUriMatcher();

    /**
     * Helper method to match each URI to the ACRONYM integers
     * constant defined above.
     * 
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code
        // to return when a match is found.  The code passed into the
        // constructor represents the code to return for the rootURI.
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = 
            new UriMatcher(UriMatcher.NO_MATCH);

        // For each type of URI that is added, a corresponding code is
        // created.
        matcher.addURI(AcronymContract.CONTENT_AUTHORITY,
                       AcronymContract.PATH_ACRONYM,
                       ACRONYMS);
        matcher.addURI(AcronymContract.CONTENT_AUTHORITY,
                       AcronymContract.PATH_ACRONYM 
                       + "/#",
                       ACRONYM);
        return matcher;
    }

    /**
     * Hook method called when Database is created to initialize the
     * Database Helper that provides access to the Acronym Database.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper =
            new AcronymDatabaseHelper(getContext());
        return true;
    }

    /**
     * Hook method called to handle requests for the MIME type of the
     * data at the given URI.  The returned MIME type should start
     * with vnd.android.cursor.item for a single item or
     * vnd.android.cursor.dir/ for multiple items.
     */
    @Override
    public String getType(Uri uri) {
        // Use Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        // Match the id returned by UriMatcher to return appropriate
        // MIME_TYPE.
        switch (match) {
        case ACRONYMS:
            return AcronymContract.AcronymEntry.CONTENT_ITEMS_TYPE;
        case ACRONYM:
            return AcronymContract.AcronymEntry.CONTENT_ITEM_TYPE;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }
    }

    /**
     * Hook method called to handle requests to insert a new row.  As
     * a courtesy, notifyChange() is called after inserting.
     */
    @Override
    public Uri insert(Uri uri,
                      ContentValues values) {
    	// Create and/or open a database that will be used for reading
        // and writing. Once opened successfully, the database is
        // cached, so you can call this method every time you need to
        // write to the database.
        final SQLiteDatabase db =
            mOpenHelper.getWritableDatabase();

        Uri returnUri;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert a new
        // row.
        switch (sUriMatcher.match(uri)) {
        case ACRONYMS:
            // TODO - replace 0 with code that inserts a row in Table
            // and returns the row id.
            long id = 0;

            // Check if a new row is inserted or not.
            if (id > 0)
                returnUri = 
                    AcronymContract.AcronymEntry.buildAcronymUri(id);
            else
                throw new android.database.SQLException
                    ("Failed to insert row into " 
                     + uri);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Notifies registered observers that a row was inserted.
        getContext().getContentResolver().notifyChange(uri, 
                                                       null);
        return returnUri;
    }

    // Hook method to handle requests to insert a set of new rows, or
    // the default implementation will iterate over the values and
    // call insert on each of them. As a courtesy, call notifyChange()
    // after inserting.
    @Override
    public int bulkInsert(Uri uri,
                          ContentValues[] contentValues) {
        // Create and/or open a database that will be used for reading
        // and writing. Once opened successfully, the database is
        // cached, so you can call this method every time you need to
        // write to the database.
        final SQLiteDatabase db =
            mOpenHelper.getWritableDatabase();
		
        // Try to match against the path in a uri.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If a match occurs update the
        // appropriate rows.
        switch (sUriMatcher.match(uri)) {
        case ACRONYMS:
            // Begins a transaction in EXCLUSIVE mode. 
            db.beginTransaction();
            int returnCount = 0;

            try {
                // TODO -- write the code that inserts all the
                // contentValues into the SQLite database.

                // Marks the current transaction as successful.
                db.setTransactionSuccessful();
            } finally {
                // End a transaction.
                db.endTransaction();
            }
			   
            // Notifies registered observers that rows were updated.
            getContext().getContentResolver().notifyChange(uri,
                                                           null);
            return returnCount;
        default:
            return super.bulkInsert(uri,
                                    contentValues);
        }
    }

    /**
     * Hook method called to handle query requests from clients.
     */
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;

        // Match the id returned by UriMatcher to query appropriate
        // rows.
        switch (sUriMatcher.match(uri)) {
        case ACRONYMS: 
            // TODO -- replace "null" by writing code to query the
            // entire SQLite database based on the parameters passed
            // into the method.
            retCursor = null;
            break;
        case ACRONYM: 
            // Selection clause that matches row id with id passed
            // from Uri.
            final String rowId =
                ""
                + AcronymContract.AcronymEntry._ID
                + " = '"
                + ContentUris.parseId(uri)
                + "'";

            // TODO -- replace "null" by writing code to query the
            // SQLite database for the particular rowId based on (a
            // subset of) the parameters passed into the method.
            retCursor = null;
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Register to watch a content URI for changes.
        retCursor.setNotificationUri(getContext().getContentResolver(), 
                                     uri);
        return retCursor;
    }

    /**
     * Hook method called to handle requests to update one or more
     * rows. The implementation should update all rows matching the
     * selection to set the columns according to the provided values
     * map. As a courtesy, notifyChange() is called after updating .
     */
    @Override
    public int update(Uri uri,
                      ContentValues values,
                      String selection,
                      String[] selectionArgs) {
        // Create and/or open a database that will be used for reading
        // and writing. Once opened successfully, the database is
        // cached, so you can call this method every time you need to
        // write to the database.
        final SQLiteDatabase db =
            mOpenHelper.getWritableDatabase();

        int rowsUpdated;

        // Try to match against the path in a uri.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If a match occurs update the
        // appropriate rows.
        switch (sUriMatcher.match(uri)) {
        case ACRONYMS:
            // Updates the rows in the Database and returns no of rows
            // updated.
            // TODO -- replace "0" with a call to the SQLite database
            // to update the row(s) in the database based on the
            // parameters passed into this method.
            rowsUpdated = 0;
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Notifies registered observers that rows were updated.
        if (rowsUpdated != 0) 
            getContext().getContentResolver().notifyChange(uri,
                                                           null);
        return rowsUpdated;
    }
    
    /**
     * Hook method to handle requests to delete one or more rows.  The
     * implementation should apply the selection clause when
     * performing deletion, allowing the operation to affect multiple
     * rows in a directory.  As a courtesy, notifyChange() is called
     * after deleting.
     */
    @Override
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {
    	// Create and/or open a database that will be used for reading
    	// and writing. Once opened successfully, the database is
    	// cached, so you can call this method every time you need to
    	// write to the database.
        final SQLiteDatabase db =
            mOpenHelper.getWritableDatabase();

        // Keeps track of the number of rows deleted.
        int rowsDeleted = 0;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI) or -1 if
        // there is no matched node.  If a match is found delete the
        // appropriate rows.
        switch (sUriMatcher.match(uri)) {
        case ACRONYMS:
            // TODO -- replace "0" with code that deletes the row(s)
            // in the SQLite database table based on the parameters
            // passed into the method.
            rowsDeleted = 0;
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Notifies registered observers that rows were deleted.
        if (selection == null || rowsDeleted != 0) 
            getContext().getContentResolver().notifyChange(uri, 
                                                           null);
        return rowsDeleted;
    }
}
