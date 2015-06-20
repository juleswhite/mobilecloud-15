package vandy.mooc.provider;

import java.util.HashMap;

import vandy.mooc.provider.CharacterContract.CharacterEntry;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
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

    public HobbitProviderHashMap(Context context) {
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
            db.insert(HobbitContract.HobbitEntry.TABLE_NAME,
                      null,
                      values);

        // Check if a new row is inserted or not.
        if (id > 0)
            return HobbitContract.HobbitEntry.buildAcronymUri(id);
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

                for (ContentValues cvs : contentValues) {
                    final long id =
                        db.insert(AcronymContract.AcronymEntry.TABLE_NAME,
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

        synchronized (this) {
            // Implement a simple query mechanism for the table.
            for (CharacterRecord cr : mCharacterMap.values())
                buildCursorConditionally(cursor,
                                         cr,
                                         selection,
                                         selectionArgs);
        }
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

        synchronized (this) {
            CharacterRecord cr = 
                mCharacterMap.get(requestId);
            if (cr != null) {
                buildCursorConditionally(cursor,
                                         cr,
                                         selection,
                                         selectionArgs);
            }
        }
        return cursor;
        // throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    /**
     * Build a MatrixCursor that matches the parameters.
     */
    private void buildCursorConditionally(MatrixCursor cursor,
                                          CharacterRecord cr,
                                          String selection,
                                          String[] selectionArgs) {
        for (String item : selectionArgs) {
            if ((selection.equals(CharacterContract.CharacterEntry.COLUMN_NAME) 
                 && item.equals(cr.getName()))
                || (selection.equals(CharacterContract.CharacterEntry.COLUMN_RACE)
                    && item.equals(cr.getRace()))) {
                cursor.addRow(new Object[] { 
                        cr.getId(), 
                        cr.getName(),
                        cr.getRace()
                    });
            }
        }
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

        synchronized (this) {
            // Implement a simple update mechanism for the table.
            for (CharacterRecord cr : 
                     mCharacterMap.values().toArray
                     // @@ 
                     (new CharacterRecord[mCharacterMap.values().size()]))
                recsUpdated += 
                    updateEntryConditionally(cr,
                                             cvs,
                                             selection,
                                             selectionArgs);
        }

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
        synchronized (this) {
            CharacterRecord cr = mCharacterMap.get(requestId);
            if (cr != null) 
                return updateEntryConditionally(cr,
                                                cvs,
                                                selection,
                                                selectionArgs);
            else
                return 0;
        }
    }

    /**
     * Update @a rec in the HashMap with the contents of the @a
     * ContentValues if it matches the @a selection criteria.
     */
    private int updateEntryConditionally(CharacterRecord rec,
                                         ContentValues cvs,
                                         String selection,
                                         String[] selectionArgs) {
        if (selectionArgs == null)
            selectionArgs = new String[] { "" };

        for (String character : selectionArgs) 
            if (selection == null
                || (selection.equals(CharacterContract.CharacterEntry.COLUMN_NAME) 
                 && character.equals(rec.getName()))
                || (selection.equals(CharacterContract.CharacterEntry.COLUMN_RACE)
                    && character.equals(rec.getRace()))) {
                final CharacterRecord updatedRec = 
                    new CharacterRecord(rec.getId(),
                                        cvs.getAsString
                                        (CharacterEntry.COLUMN_NAME),
                                        cvs.getAsString
                                        (CharacterEntry.COLUMN_RACE));
                mCharacterMap.put(updatedRec.getId(), 
                                  updatedRec);
                return 1;
            }

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
        
        // Implement a simple delete mechanism for the table.
        synchronized (this) {
            for (CharacterRecord cr : 
                     mCharacterMap.values().toArray
                     (new CharacterRecord[mCharacterMap.values().size()]))
                recsDeleted += 
                    deleteEntryConditionally(cr,
                                             selection,
                                             selectionArgs);
        }
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
        synchronized (this) {
            CharacterRecord rec = mCharacterMap.get(requestId);
            if (rec != null) 
                return deleteEntryConditionally(rec,
                                                selection,
                                                selectionArgs);
            else
                return 0;
        }
    }

    /**
     * Delete @a rec from the HashMap if it matches the @a selection
     * criteria.
     */
    private int deleteEntryConditionally(CharacterRecord rec,
                                         String selection,
                                         String[] selectionArgs) {
        for (String character : selectionArgs) 
            if ((selection.equals(CharacterContract.CharacterEntry.COLUMN_NAME) 
                 && character.equals(rec.getName()))
                || (selection.equals(CharacterContract.CharacterEntry.COLUMN_RACE)
                    && character.equals(rec.getRace()))) {
                mCharacterMap.remove(rec.getId());
                return 1;
            }
        return 0;
    }
}
