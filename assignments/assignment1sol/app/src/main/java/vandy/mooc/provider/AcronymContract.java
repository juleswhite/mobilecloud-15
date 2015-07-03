package vandy.mooc.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the Acronym database.
 */
public final class AcronymContract {
    /**
     * The "Content authority" is a name for the entire content
     * provider, similar to the relationship between a domain name and
     * its website.  A convenient string to use for the content
     * authority is the package name for the app, which must be unique
     * on the device.
     */
    public static final String CONTENT_AUTHORITY =
        "vandy.mooc.acronym.provider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's that apps
     * will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI =
        Uri.parse("content://"
                  + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible
     * URI's), e.g., content://vandy.mooc.acronym.provider/acronym/ is a valid path for
     * Acronym data. However, content://vandy.mooc.acronym.provider/givemeroot/ will
     * fail since the ContentProvider hasn't been given any
     * information on what to do with "givemeroot".
     */
    public static final String PATH_ACRONYM =
        AcronymEntry.TABLE_NAME;

    /**
     * Inner class that defines the contents of the Acronym table.
     * Note: the number of this form of innner classes is dependent on the amount of Table present in the database
     */
    public static final class AcronymEntry implements BaseColumns {
        /**
         * Use BASE_CONTENT_URI to create the unique URI for Acronym
         * Table that apps will use to contact the content provider.
         */
        public static final Uri CONTENT_URI =   BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ACRONYM)
                .build();

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 0..x items.  note : vnd.android.curso.dir  is a general convention
         * it is in the documentation. This retrve items using their vendor(vnd) specific MIME types which contains type part,subtype part and provider specific
		 * subtype part(optionally)
         * retuns string in MIME format
         * Note: you can choose to add provider specific  part by appending with the subtype part
         * using the form : vnd.<name>.<type>
         *     therefor the resulting vendor mime type will look like vnd.android.cursor.dir/vnd.com.example.provider.table1
         */
        public static final String CONTENT_ITEMS_TYPE =
            "vnd.android.cursor.dir/"
            + CONTENT_AUTHORITY 
            + "/" 
            + PATH_ACRONYM;

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 1 item.  vnd.android.cursor.item/ retrieve a singular item
         * using its vendor(vnd) specific MIME types
         * using the form : vnd.<name>.<type>
         *     therefor the resulting vendor mime type will look like vnd.android.cursor.item/vnd.com.example.provider.table1
         *
         * retuns string in MIME format
         */
        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/"
            + CONTENT_AUTHORITY 
            + "/" 
            + PATH_ACRONYM;

        /**
         * Name of the database table.
         */
        public static final String TABLE_NAME =
            "acronym_table";

        /**
         * Columns to store Data of each Acronym Expansion.
         */
        public static final String COLUMN_ACRONYM = "acronym";
        public static final String COLUMN_LONG_FORM = "long_form";
        public static final String COLUMN_FREQUENCY = "frequency";
        public static final String COLUMN_SINCE = "since";
        public static final String COLUMN_EXPIRATION_TIME = "expiration_time";

        /**
         * Return a Uri that points to the row containing a given id.
         * 
         * @param id
         * @return Uri
         */
        public static Uri buildAcronymUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI,
                                              id);
        }
    }
}
