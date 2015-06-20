package vandy.mooc.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Content Provider used to store information about Hobbit characters.
 */
public class HobbitProviderSQLite extends HobbitProviderImpl  {
    /**
     * Use HobbitDatabaseHelper to manage database creation and version
     * management.
     */
    private HobbitDatabaseHelper mOpenHelper;

    /**
     * Constructor initializes the super class.
     */
    public HobbitProviderSQLite(Context context) {
        super(context);
    }

    /**
     * Return true if successfully started.
     */
    public boolean onCreate() {
        mOpenHelper =
            new HobbitDatabaseHelper(mContext);
        return true;
    }

    /**
     * Method called to handle insert requests from client
     * applications.
     */
    @Override
    public Uri insertCharacters(Uri uri,
                                ContentValues cvs) {
        final SQLiteDatabase db =
            mOpenHelper.getWritableDatabase();

        long id =
            db.insert(CharacterContract.CharacterEntry.TABLE_NAME,
                      null,
                      cvs);

        // Check if a new row is inserted or not.
        if (id > 0)
            return CharacterContract.CharacterEntry.buildUri(id);
        else
            throw new android.database.SQLException
                ("Failed to insert row into " 
                 + uri);
    }

    /**
     * Method that handles bulk insert requests.
     */
    @Override
    public int bulkInsertCharacters(Uri uri,
                                    ContentValues[] cvsArray) {
        // Create and/or open a database that will be used for reading
        // and writing. Once opened successfully, the database is
        // cached, so you can call this method every time you need to
        // write to the database.
        final SQLiteDatabase db =
            mOpenHelper.getWritableDatabase();

        int returnCount = 0;

            // Begins a transaction in EXCLUSIVE mode. 
            db.beginTransaction();
            try {
                for (ContentValues cvs : cvsArray) {
                    final long id =
                        db.insert(CharacterContract.CharacterEntry.TABLE_NAME,
                                  null,
                                  cvs);
                    if (id != -1)
                        returnCount++;
                }
                // Marks the current transaction as successful.
                db.setTransactionSuccessful();
            } finally {
                // End a transaction.
                db.endTransaction();
            }
        return returnCount;
        // return super.bulkInsert(uri, cvsArray);
    }

    /**
     * Method called to handle query requests from client
     * applications.
     */
    @Override
    public Cursor queryCharacters(Uri uri,
                                  String[] projection,
                                  String selection,
                                  String[] selectionArgs,
                                  String sortOrder) {
        final MatrixCursor cursor =
            new MatrixCursor(sCOLUMNS);

        return cursor;
    }

    /**
     * Method called to handle query requests from client
     * applications.
     */
    @Override
    public Cursor queryCharacter(Uri uri,
                                 String[] projection,
                                 String selection,
                                 String[] selectionArgs,
                                 String sortOrder) {
        final MatrixCursor cursor =
            new MatrixCursor(sCOLUMNS);

        // Just return a single item from the database.
        long requestId = ContentUris.parseId(uri);

        return cursor;
        // throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    /**
     * Method called to handle update requests from client
     * applications.
     */
    @Override
    public int updateCharacters(Uri uri,
                                ContentValues cvs,
                                String selection,
                                String[] selectionArgs) {
        int recsUpdated = 0;

        return recsUpdated;
    }

    /**
     * Method called to handle update requests from client
     * applications.
     */
    @Override
    public int updateCharacter(Uri uri,
                               ContentValues cvs,
                               String selection,
                               String[] selectionArgs) {
        // Just update a single item in the database.
        final long requestId = ContentUris.parseId(uri);
        return 0;
    }

    /**
     * Method called to handle delete requests from client
     * applications.
     */
    @Override
    public int deleteCharacters(Uri uri,
                                String selection,
                                String[] selectionArgs) {
        int recsDeleted = 0;
        
        return recsDeleted;
    }

    /**
     * Method called to handle delete requests from client
     * applications.
     */
    @Override
    public int deleteCharacter(Uri uri,
                               String selection,
                               String[] selectionArgs) {
        // Just delete a single item in the database.
        final long requestId = ContentUris.parseId(uri);
        return 0;
    }
}
