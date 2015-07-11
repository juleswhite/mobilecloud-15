package vandy.mooc.model.provider;

import android.content.ContentUris;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This contract defines the metadata for the Weather Content
 * Provider, including the provider's access URIs and its database
 * constants.  The following two tables are managed by the Weather
 * Content Provider:
 *
 * 1. Weather Values -- This table stores the weather information that
 *    has a one-to-one relationship with a given location.
 *
 * 2. Weather Conditions -- This table stores each location's various
 *    weather conditions (such as "broken clouds" or "sky is clear").
 *    This table is separate from the Weather Values table because
 *    each location can have multiple Weather Conditions.
 */
public final class WeatherContract {
    /**
     * The WeatherProvider's unique authority identifier.
     */
    public static final String AUTHORITY =
        "vandy.mooc.weatherprovider";

    /**
     * The base of all URIs that are used to communicate with the
     * WeatherProvider.
     */
    private static final Uri BASE_URI =
        Uri.parse("content://"
                  + AUTHORITY);

    /**
     * Constant for a directory MIME type.
     */
    private static final String MIME_TYPE_DIR =
        "vnd.android.cursor.dir/";

    /**
     * Constant for a single item MIME type.
     */
    private static final String MIME_TYPE_ITEM =
        "vnd.android.cursor.item/";

    /**
     * Path that accesses all the WeatherData for a given location,
     * which is used to join both tables over the location.
     */
    public static final String ACCESS_ALL_DATA_FOR_LOCATION_PATH =
        "access_all_for_location";

    /**
     * URI used to access all the data for a given location.  This
     * will access a cursor that concatenates the row of the Weather
     * Data entry corresponding to the location with all the rows of
     * its associated Weather Conditions entries.
     */
    public static final Uri ACCESS_ALL_DATA_FOR_LOCATION_URI = 
        BASE_URI.buildUpon().appendPath
            (ACCESS_ALL_DATA_FOR_LOCATION_PATH).build();

    /**
     * MIME type for accessing all the data for a location.
     */
    public static final String ACCESS_ALL_DATA_FOR_LOCATION =
        MIME_TYPE_DIR
        + AUTHORITY 
        + "/" 
        + ACCESS_ALL_DATA_FOR_LOCATION_PATH;

    /**
     * Inner class defining the contents of the Weather Values table.
     */
    public static final class WeatherValuesEntry 
                        implements BaseColumns {
        /**
         * Weather Values's Table name.
         */
        public static String WEATHER_VALUES_TABLE_NAME =
            "weather_values";

        /**
         * Unique URI for the Weather Values table.
         */
        public static final Uri WEATHER_VALUES_CONTENT_URI =
            BASE_URI.buildUpon()
                    .appendPath(WEATHER_VALUES_TABLE_NAME)
                    .build();

        /**
         * MIME type for multiple Weather Values rows.
         */
        public static final String WEATHER_VALUES_ITEMS =
            MIME_TYPE_DIR
            + AUTHORITY 
            + "/" 
            + WEATHER_VALUES_TABLE_NAME;

        /**
         * MIME type for a single Weather Values row
         */
        public static final String WEATHER_VALUES_ITEM =
            MIME_TYPE_ITEM
            + AUTHORITY 
            + "/" 
            + WEATHER_VALUES_TABLE_NAME;

        /*
         * Weather Values Table's Columns.
         */
        public static final String COLUMN_LOCATION_KEY = "loc_key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_COD = "cod";

        public static final String COLUMN_SUNRISE = "sunrise";
        public static final String COLUMN_SUNSET = "sunset";
        public static final String COLUMN_COUNTRY = "country";

        public static final String COLUMN_TEMP = "temp";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";

        public static final String COLUMN_SPEED = "speed";
        public static final String COLUMN_DEG = "deg";

        public static final String COLUMN_EXPIRATION_TIME = "expiration_time";

        /**
         * Return a URI that points to the row containing the given
         * ID.
         */
        public static Uri buildRowAccessUri(Long id) {
            return ContentUris.withAppendedId(WEATHER_VALUES_CONTENT_URI,
                                              id);
        }
    }

    /**
     * Inner class defining the contents of the Weather Conditions
     * table.
     */
    public static final class WeatherConditionsEntry 
                        implements BaseColumns {
        /**
         * Weather Conditions's Table name.
         */
        public static String WEATHER_CONDITIONS_TABLE_NAME =
            "weather_conditions";

        /**
         * Unique URI for the Weather Conditions table.
         */
        public static final Uri WEATHER_CONDITIONS_CONTENT_URI = 
            BASE_URI.buildUpon()
                    .appendPath(WEATHER_CONDITIONS_TABLE_NAME)
                    .build();

        /**
         * MIME type for multiple Weather Conditions rows
         */
        public static final String WEATHER_CONDITIONS_ITEMS = 
            MIME_TYPE_DIR
            + AUTHORITY 
            + "/" 
            + WEATHER_CONDITIONS_TABLE_NAME;

        /**
         * MIME type for a single Weather Conditions row.
         */
        public static final String WEATHER_CONDITIONS_ITEM = 
            MIME_TYPE_ITEM
            + AUTHORITY 
            + "/" 
            + WEATHER_CONDITIONS_TABLE_NAME;

        /*
         * Weather Conditions Table's Columns
         */
        public static final String COLUMN_WEATHER_CONDITIONS_OBJECT_ID =
            "weather_cond_object_id";
        public static final String COLUMN_MAIN =
            "main";
        public static final String COLUMN_DESCRIPTION =
            "description";
        public static final String COLUMN_ICON =
            "icon";
        public static final String COLUMN_LOCATION_KEY =
            "loc_key_sub";
        public static final String COLUMN_EXPIRATION_TIME = 
            "expiration_time_sub";

        /**
         * Return a URI that points to the row containing the given ID.
         */
        public static Uri buildRowAccessUri(Long id) {
            return ContentUris.withAppendedId
                (WEATHER_CONDITIONS_CONTENT_URI,
                 id);
        }
    }
}
