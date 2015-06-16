package vandy.mooc.provider;

import java.util.HashMap;
import java.util.Iterator;

import vandy.mooc.R;
import vandy.mooc.provider.CharacterContract.CharacterEntry;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * Content Provider used to store information about Hobbit characters.
 */
public class HobbitContentProvider extends ContentProvider {
    /**
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher =
        buildUriMatcher();

    /**
     * The code that is returned when a URI for more than 1 items is
     * matched against the given components.  Must be positive.
     */
    private static final int CHARACTERS = 100;

    /**
     * The code that is returned when a URI for exactly 1 item is
     * matched against the given components.  Must be positive.
     */
    private static final int CHARACTER = 101;

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

        // The "Content authority" is a name for the entire content
        // provider, similar to the relationship between a domain name
        // and its website.  A convenient string to use for the
        // content authority is the package name for the app, which is
        // guaranteed to be unique on the device.
        final String authority =
            CharacterContract.CONTENT_AUTHORITY;

        // For each type of URI that is added, a corresponding code is
        // created.
        matcher.addURI(authority,
                       CharacterContract.PATH_CHARACTER,
                       CHARACTERS);
        matcher.addURI(authority,
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
     * This implementation uses a simple HashMap to map IDs to
     * CharacterRecords.  A "real" solution would likely use an SQLite
     * database.
     */
    private static final HashMap<Long, CharacterRecord> mCharacterDB =
        new HashMap<>();

    /**
     * Method called to handle type requests from client applications.
     * It returns the MIME type of the data associated with each URI.
     */
    @Override
    public synchronized String getType(Uri uri) {
        // Use Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        // Match the id returned by UriMatcher to return appropriate
        // MIME_TYPE.
        switch (match) {
        case CHARACTERS:
            return CharacterContract.CharacterEntry.CONTENT_TYPE;
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
    @Override
    public Uri insert(Uri uri,
                      ContentValues cvs) {
        Uri returnUri;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert a new
        // row.
        switch (sUriMatcher.match(uri)) {
        case CHARACTERS:
            synchronized (this) {
                if (cvs.containsKey(CharacterEntry.COLUMN_NAME)) {
                    CharacterRecord rec =
                        new CharacterRecord(cvs.getAsString
                                            (CharacterEntry.COLUMN_NAME),
                                            cvs.getAsString
                                            (CharacterEntry.COLUMN_RACE));
                    mCharacterDB.put(rec.getId(), 
                                     rec);
                    returnUri =
                        CharacterContract.CharacterEntry.buildUri(rec.getId());
                } else
                    throw new RuntimeException("Failed to insert row into " 
                                               + uri);
            }
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

    /**
     * Method that handles bulk insert requests.
     */
    @Override
    public int bulkInsert(Uri uri,
                          ContentValues[] cvsArray) {
        Uri returnUri;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert new
        // rows.
        switch (sUriMatcher.match(uri)) {
        case CHARACTERS:
            int returnCount = 0;
            synchronized (this) {
                for (ContentValues cvs : cvsArray) {
                    if (cvs.containsKey(CharacterEntry.COLUMN_NAME)) {
                        CharacterRecord rec =
                            new CharacterRecord(cvs.getAsString
                                                (CharacterEntry.COLUMN_NAME),
                                                cvs.getAsString
                                                (CharacterEntry.COLUMN_RACE));
                        mCharacterDB.put(rec.getId(),
                                         rec);
                        returnCount++;
                    } else 
                        throw new RuntimeException("Failed to insert row into " 
                                                   + uri);
                }
            }

            // Notifies registered observers that rows were inserted.
            getContext().getContentResolver().notifyChange(uri, 
                                                           null);
            return returnCount;
        default:
            return super.bulkInsert(uri,
                                    cvsArray);
        }
    }

    /**
     * Method called to handle query requests from client
     * applications.
     */
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(sCOLUMNS);

        // Match the id returned by UriMatcher to query appropriate
        // rows.
        switch (sUriMatcher.match(uri)) {
        case CHARACTERS:
            synchronized (this) {
                // Return all the items in the database.
                for (CharacterRecord rec : mCharacterDB.values()) 
                    cursor.addRow(new Object[] { 
                            rec.getId(), 
                            rec.getName(),
                            rec.getRace()
                        });
            }
            break;
        case CHARACTER:
            // Just return a single item from the database.
            long requestId = ContentUris.parseId(uri);

            synchronized (this) {
                if (mCharacterDB.containsKey(requestId)) {
                    CharacterRecord rec = mCharacterDB.get(requestId);
                    cursor.addRow(new Object[] {
                            rec.getId(),
                            rec.getName(),
                            rec.getRace() 
                        });
                }
            }
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Register to watch a content URI for changes.
        cursor.setNotificationUri(getContext().getContentResolver(), 
                                  uri);
        return cursor;
    }

    /**
     * Method called to handle update requests from client
     * applications.
     */
    @Override
    public int update(Uri uri,
                      ContentValues cvs,
                      String selection,
                      String[] selectionArgs) {
        int recsUpdated = 0;

        // Match the id returned by UriMatcher to update appropriate
        // rows.
        switch (sUriMatcher.match(uri)) {
        case CHARACTER:
            synchronized (this) {
                if (cvs.containsKey(CharacterEntry.COLUMN_NAME)) {
                    long requestId = ContentUris.parseId(uri);
                    CharacterRecord rec = 
                        new CharacterRecord(requestId,
                                            cvs.getAsString
                                            (CharacterEntry.COLUMN_NAME),
                                            cvs.getAsString
                                            (CharacterEntry.COLUMN_RACE));
                    mCharacterDB.put(rec.getId(), 
                                     rec);
                    recsUpdated = 1;
                }
            }
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Notifies registered observers that a row was inserted.
        getContext().getContentResolver().notifyChange(uri, 
                                                       null);
        return recsUpdated;
    }

    /**
     * Method called to handle delete requests from client
     * applications.
     */
    @Override
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
            synchronized (this) {
                for (String character : selectionArgs) {
                    Iterator<CharacterRecord> iter =
                        mCharacterDB.values().iterator();

                    while (iter.hasNext()) {
                        CharacterRecord rec = iter.next();
                        if ((selection.equals(CharacterContract.CharacterEntry.COLUMN_NAME) 
                             && character.equals(rec.getName()))
                            || (selection.equals(CharacterContract.CharacterEntry.COLUMN_RACE)
                                && character.equals(rec.getRace()))) {
                            iter.remove();
                            recsDeleted++;
                        }
                    }
                }
            }
            break;
        case CHARACTER:
            long requestId = ContentUris.parseId(uri);
            synchronized (this) {
                if (mCharacterDB.containsKey(requestId)) {
                    mCharacterDB.remove(requestId);
                    recsDeleted++;
                }
            }
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " 
                                                    + uri);
        }

        // Notifies registered observers that rows were deleted.
        if (selection == null || recsDeleted != 0) 
            getContext().getContentResolver().notifyChange(uri, 
                                                           null);
        return recsDeleted;
    }
    
    /**
     * Return true if successfully started.
     */
    @Override
    public boolean onCreate() {
        return true;
    }
}
