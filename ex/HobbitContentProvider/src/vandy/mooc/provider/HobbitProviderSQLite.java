package vandy.mooc.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.net.Uri;
import android.util.Log;

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
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     "OR");
        return mOpenHelper.getReadableDatabase().query
            (CharacterContract.CharacterEntry.TABLE_NAME,
             projection,
             selection,
             selectionArgs,
             null,
             null,
             sortOrder);
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
        // Selection clause that matches row id with id passed
        // from Uri.
        final String rowId =
            ""
            + CharacterContract.CharacterEntry._ID
            + " = '"
            + ContentUris.parseId(uri)
            + "'";

        // Query the SQLite database for the particular rowId based on
        // (a subset of) the parameters passed into the method.
        return mOpenHelper.getReadableDatabase().query
            (CharacterContract.CharacterEntry.TABLE_NAME,
             projection,
             rowId,
             null,
             null,
             null,
             sortOrder);
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
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     " OR ");
        return mOpenHelper.getWritableDatabase().update
            (CharacterContract.CharacterEntry.TABLE_NAME,
             cvs,
             selection,
             selectionArgs);
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
        selection = addSelectionArgs(selection,
                                     selectionArgs,
                                     " OR ");
        return mOpenHelper.getWritableDatabase().update
            (CharacterContract.CharacterEntry.TABLE_NAME,
             cvs,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs);
    }

    /**
     * Method called to handle delete requests from client
     * applications.
     */
    @Override
    public int deleteCharacters(Uri uri,
                                String selection,
                                String[] selectionArgs) {
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     " OR ");
        return mOpenHelper.getWritableDatabase().delete
            (CharacterContract.CharacterEntry.TABLE_NAME,
             selection,
             selectionArgs);
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
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     " OR ");
        return mOpenHelper.getWritableDatabase().delete
            (CharacterContract.CharacterEntry.TABLE_NAME,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs);
    }

    /**
     * Return a selection string that concatenates all the selectionArgs.
     */
    private String addSelectionArgs(String selection,
                                    String [] selectionArgs,
                                    String operation) {
        if (selection == null
            || selectionArgs == null)
            return null;
        String result = "";
        for (int i = 0;
             i < selectionArgs.length - 1;
             ++i)
            result += (selection + " = ? " + operation + " ");
        result += (selection + " = ?");

        Log.d(TAG,
              "selection = "
              + result
              + " selectionArgs = ");
        for (String args : selectionArgs)
            Log.d(TAG,
                  args
                  + " ");

        return result;
    }        

    /**
     * Helper method that appends a given key id to the end of the
     * WHERE statement parameter.
     */
    private static String addKeyIdCheckToWhereStatement(String whereStatement,
                                                        long id) {
        String newWhereStatement;
        if (TextUtils.isEmpty(whereStatement)) 
            newWhereStatement = "";
        else 
            newWhereStatement = whereStatement + " AND ";

        return newWhereStatement 
            + CharacterContract.CharacterEntry._ID
            + " = '"
            + id 
            + "'";
    }
}
