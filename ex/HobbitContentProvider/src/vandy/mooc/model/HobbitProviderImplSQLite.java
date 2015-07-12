package vandy.mooc.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.net.Uri;
import android.util.Log;

/**
 * Content Provider implementation that uses SQLite to manage Hobbit
 * characters.  This class plays the role of the "Concrete
 * Implementor" in the Bridge pattern and the "Concrete Class" in the
 * TemplateMethod pattern.
 */
public class HobbitProviderImplSQLite 
       extends HobbitProviderImpl  {
    /**
     * Use HobbitDatabaseHelper to manage database creation and version
     * management.
     */
    private HobbitDatabaseHelper mOpenHelper;

    /**
     * Constructor initializes the super class.
     */
    public HobbitProviderImplSQLite(Context context) {
        super(context);
    }

    /**
     * Return true if successfully started.
     */
    public boolean onCreate() {
        // Create the HobbitDatabaseHelper.
        mOpenHelper =
            new HobbitDatabaseHelper(mContext);
        return true;
    }

    /**
     * Method called to handle insert requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
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
     * Method that handles bulk insert requests.  This plays the role
     * of the "concrete hook method" in the Template Method pattern.
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
    }

    /**
     * Method called to handle query requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public Cursor queryCharacters(Uri uri,
                                  String[] projection,
                                  String selection,
                                  String[] selectionArgs,
                                  String sortOrder) {
        // Expand the selection if necessary.
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
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public Cursor queryCharacter(Uri uri,
                                 String[] projection,
                                 String selection,
                                 String[] selectionArgs,
                                 String sortOrder) {
        // Query the SQLite database for the particular rowId based on
        // (a subset of) the parameters passed into the method.
        return mOpenHelper.getReadableDatabase().query
            (CharacterContract.CharacterEntry.TABLE_NAME,
             projection,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs,
             null,
             null,
             sortOrder);
    }

    /**
     * Method called to handle update requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int updateCharacters(Uri uri,
                                ContentValues cvs,
                                String selection,
                                String[] selectionArgs) {
        // Expand the selection if necessary.
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
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int updateCharacter(Uri uri,
                               ContentValues cvs,
                               String selection,
                               String[] selectionArgs) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection,
                                     selectionArgs,
                                     " OR ");
        // Just update a single row in the database.
        return mOpenHelper.getWritableDatabase().update
            (CharacterContract.CharacterEntry.TABLE_NAME,
             cvs,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs);
    }

    /**
     * Method called to handle delete requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int deleteCharacters(Uri uri,
                                String selection,
                                String[] selectionArgs) {
        // Expand the selection if necessary.
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
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public int deleteCharacter(Uri uri,
                               String selection,
                               String[] selectionArgs) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     " OR ");
        // Just delete a single row in the database.
        return mOpenHelper.getWritableDatabase().delete
            (CharacterContract.CharacterEntry.TABLE_NAME,
             addKeyIdCheckToWhereStatement(selection,
                                           ContentUris.parseId(uri)),
             selectionArgs);
    }

    /**
     * Return a selection string that concatenates all the
     * @a selectionArgs for a given @a selection using the given @a
     * operation.
     */
    private String addSelectionArgs(String selection,
                                    String [] selectionArgs,
                                    String operation) {
        // Handle the "null" case.
        if (selection == null
            || selectionArgs == null)
            return null;
        else {
            String selectionResult = "";

            // Properly add the selection args to the selectionResult.
            for (int i = 0;
                 i < selectionArgs.length - 1;
                 ++i)
                selectionResult += (selection 
                           + " = ? " 
                           + operation 
                           + " ");
            
            // Handle the final selection case.
            selectionResult += (selection
                       + " = ?");

            // Output the selectionResults to Logcat.
            Log.d(TAG,
                  "selection = "
                  + selectionResult
                  + " selectionArgs = ");
            for (String args : selectionArgs)
                Log.d(TAG,
                      args
                      + " ");

            return selectionResult;
        }
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

        // Append the key id to the end of the WHERE statement.
        return newWhereStatement 
            + CharacterContract.CharacterEntry._ID
            + " = '"
            + id 
            + "'";
    }
}
