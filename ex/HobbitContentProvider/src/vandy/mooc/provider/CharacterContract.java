package vandy.mooc.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This contract defines the metadata for the HobbitContentProvider,
 * including the provider's access URIs and its "database" constants.
 */
public class CharacterContract {
    /**
     * This ContentProvider's unique identifier.
     */
    public static final String CONTENT_AUTHORITY =
        "vandy.mooc.hobbitcontentprovider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which
     * apps will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI =
        Uri.parse("content://"
                  + CONTENT_AUTHORITY);

    /*
     * Columns
     */

    /**
     * Possible paths (appended to base content URI for possible
     * URI's).  For instance, content://vandy.mooc/character_map/ is a
     * valid path for looking at Character data.  Conversely,
     * content://vandy.mooc/givemeroot/ will fail, as the
     * ContentProvider hasn't been given any information on what to do
     * with "givemeroot".
     */
    public static final String PATH_CHARACTER =
        CharacterEntry.MAP_NAME;

    /**
     * Inner class that defines the table contents of the Acronym
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
        public static final String CONTENT_TYPE =
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
         * Name of the database table.
         */
        public static final String MAP_NAME =
            "character_map";

        /**
         * Selection clause to find rows with given acronym.
         */
        public static final String SELECTION_CHARACTER =
            CharacterEntry.COLUMN_NAME
            + " = ?";
    	
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
}
