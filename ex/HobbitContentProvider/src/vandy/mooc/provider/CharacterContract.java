package vandy.mooc.provider;

import vandy.mooc.R;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This contract defines the metadata for the HobbitContentProvider,
 * including the provider's access URIs and its "database" constants.
 */
public final class CharacterContract {
    /**
     * This ContentProvider's unique identifier.
     */
    public static final String CONTENT_AUTHORITY =
        "vandy.mooc.hobbitprovider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which
     * apps will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI =
        Uri.parse("content://"
                  + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible
     * URI's).  For instance, content://vandy.mooc/character_table/ is
     * a valid path for looking at Character data.  Conversely,
     * content://vandy.mooc/givemeroot/ will fail, as the
     * ContentProvider hasn't been given any information on what to do
     * with "givemeroot".
     */
    public static final String PATH_CHARACTER =
        CharacterEntry.TABLE_NAME;

    /*
     * Columns
     */

    /**
     * Inner class that defines the table contents of the Hobbit
     * table.
     */
    public static final class CharacterEntry implements BaseColumns {
        /**
         * Use BASE_CONTENT_URI to create the unique URI for Acronym
         * Table that apps will use to contact the content provider.
         */
        public static final Uri CONTENT_URI = 
            BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_CHARACTER).build();

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 0..x items.
         */
        public static final String CONTENT_ITEMS_TYPE =
            "vnd.android.cursor.dir/"
            + CONTENT_AUTHORITY
            + "/" 
            + PATH_CHARACTER;
            
        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 1 item.
         */
        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/"
            + CONTENT_AUTHORITY
            + "/" 
            + PATH_CHARACTER;

        /**
         * Columns to display.
         */
        public static final String sColumnsToDisplay [] = 
            new String[] {
            CharacterContract.CharacterEntry._ID,
            CharacterContract.CharacterEntry.COLUMN_NAME,
            CharacterContract.CharacterEntry.COLUMN_RACE
        };
    
        /**
         * Resource Ids of the columns to display.
         */
        public static final int[] sColumnResIds = 
            new int[] {
            R.id.idString, 	
            R.id.name, 
            R.id.race
        };

        /**
         * Name of the database table.
         */
        public static final String TABLE_NAME =
            "character_table";

        /**
         * Columns to store data.
         */
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_RACE = "race";

        /**
         * Return a Uri that points to the row containing a given id.
         * 
         * @param id
         * @return Uri
         */
        public static Uri buildUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI,
                                              id);
        }
    }

    /**
     * The code that is returned when a URI for more than 1 items is
     * matched against the given components.  Must be positive.
     */
    public static final int CHARACTERS = 100;

    /**
     * The code that is returned when a URI for exactly 1 item is
     * matched against the given components.  Must be positive.
     */
    public static final int CHARACTER = 101;

    /**
     * The URI Matcher used by this content provider.
     */
    public static final UriMatcher sUriMatcher =
        buildUriMatcher();

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
}
