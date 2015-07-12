package vandy.mooc.model;

import java.util.HashMap;

import vandy.mooc.model.CharacterContract.CharacterEntry;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * Content Provider implementation that uses a HashMap to manage
 * Hobbit characters.  This class plays the role of the "Concrete
 * Implementor" in the Bridge pattern and the "Concrete Class" in the
 * TemplateMethod pattern.
 */
public class HobbitProviderImplHashMap
       extends HobbitProviderImpl  {
    /**
     * This implementation uses a simple HashMap to map IDs to
     * CharacterRecords.
     */
    private static final HashMap<Long, CharacterRecord> mCharacterMap =
        new HashMap<>();

    /**
     * Constructor initializes the super class.
     */
    public HobbitProviderImplHashMap(Context context) {
        super(context);
    }

    /**
     * Method called to handle insert requests from client
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public Uri insertCharacters(Uri uri,
                                ContentValues cvs) {
        synchronized (this) {
            if (cvs.containsKey(CharacterEntry.COLUMN_NAME)) {
                CharacterRecord rec =
                    new CharacterRecord(cvs.getAsString
                                        (CharacterEntry.COLUMN_NAME),
                                        cvs.getAsString
                                        (CharacterEntry.COLUMN_RACE));
                mCharacterMap.put(rec.getId(), 
                                  rec);
                return CharacterContract.CharacterEntry.buildUri(rec.getId());
            } else
                throw new RuntimeException("Failed to insert row into " 
                                           + uri);
        }
    }

    /**
     * Method that handles bulk insert requests.  This plays the role
     * of the "concrete hook method" in the Template Method pattern.
     */
    @Override
    public int bulkInsertCharacters(Uri uri,
                                    ContentValues[] cvsArray) {
        int returnCount = 0;
        synchronized (this) {
            for (ContentValues cvs : cvsArray) {
                if (cvs.containsKey(CharacterEntry.COLUMN_NAME)) {
                    CharacterRecord rec =
                        new CharacterRecord(cvs.getAsString
                                            (CharacterEntry.COLUMN_NAME),
                                            cvs.getAsString
                                            (CharacterEntry.COLUMN_RACE));
                    mCharacterMap.put(rec.getId(),
                                      rec);
                    returnCount++;
                } else 
                    throw new RuntimeException("Failed to insert row into " 
                                               + uri);
            }
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
        final MatrixCursor cursor =
            new MatrixCursor(CharacterContract.CharacterEntry.sColumnsToDisplay);

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
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
     */
    @Override
    public Cursor queryCharacter(Uri uri,
                                 String[] projection,
                                 String selection,
                                 String[] selectionArgs,
                                 String sortOrder) {
        final MatrixCursor cursor =
            new MatrixCursor(CharacterContract.CharacterEntry.sColumnsToDisplay);

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
    }

    /**
     * Build a MatrixCursor that matches the parameters.  This plays
     * the role of the "concrete hook method" in the Template Method
     * pattern.
     */
    private void buildCursorConditionally(MatrixCursor cursor,
                                          CharacterRecord cr,
                                          String selection,
                                          String[] selectionArgs) {
        if (selectionArgs == null)
            cursor.addRow(new Object[] { 
                    cr.getId(), 
                    cr.getName(),
                    cr.getRace()
                });
        else
            for (String item : selectionArgs) 
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
        int recsUpdated = 0;

        synchronized (this) {
            // Implement a simple update mechanism for the table.
            for (CharacterRecord cr : 
                     mCharacterMap.values().toArray
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
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
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
        if (selectionArgs == null) {
            final CharacterRecord updatedRec = 
                new CharacterRecord(rec.getId(),
                                    cvs.getAsString
                                    (CharacterEntry.COLUMN_NAME),
                                    cvs.getAsString
                                    (CharacterEntry.COLUMN_RACE));
            mCharacterMap.put(updatedRec.getId(), 
                              updatedRec);
            return 1;
        } else
            for (String character : selectionArgs) 
                if ((selection.equals(CharacterContract.CharacterEntry.COLUMN_NAME) 
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
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
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
     * applications.  This plays the role of the "concrete hook
     * method" in the Template Method pattern.
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
        if (selectionArgs == null) {
            mCharacterMap.remove(rec.getId());
            return 1;    
        }
        else
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
