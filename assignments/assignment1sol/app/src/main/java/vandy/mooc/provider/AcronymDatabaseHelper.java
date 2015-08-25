package vandy.mooc.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import vandy.mooc.provider.AcronymContract.AcronymEntry;

/**
 * Manages a local database for Acronym data.
 */
public class AcronymDatabaseHelper extends SQLiteOpenHelper {
    /**
     * If the database schema is changed, the database version must be
     * incremented.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Database name.
     */
    public static final String DATABASE_NAME =
        "acronym.db";

    /**
     * the sqll query to use and create the table
     */

    final String SQL_CREATE_ACRONYM_TABLE =
            "CREATE TABLE "
                    + AcronymEntry.TABLE_NAME + " ("
                    + AcronymEntry._ID + " INTEGER PRIMARY KEY, "//id from the base column
                    + AcronymEntry.COLUMN_ACRONYM + " TEXT NOT NULL, "
                    + AcronymEntry.COLUMN_LONG_FORM + " TEXT NOT NULL, "
                    + AcronymEntry.COLUMN_FREQUENCY + " INTEGER NOT NULL, "
                    + AcronymEntry.COLUMN_SINCE + " INTEGER NOT NULL, "
                    + AcronymEntry.COLUMN_EXPIRATION_TIME + " INTEGER NOT NULL "
                    + " );";

    /**
     * Constructor for AcronymDatabaseHelper.  Store the database in
     * the cache directory so Android can remove it if memory is low.
     * 
     * @param context
     */
    public AcronymDatabaseHelper(Context context) {
    	super(context, 
              context.getCacheDir()//use the cache directory since android usually reclaim the space when running low on memory
              + File.separator 
              + DATABASE_NAME,
              null, //ignore the cursor factory
              DATABASE_VERSION);
    }

    /**
     * Hook method called when Database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        
        // Create the table.
        db.execSQL(SQL_CREATE_ACRONYM_TABLE);
    }

    /**
     * Hook method called when Database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        // This database is only a cache for online data, so its
        // upgrade policy is to simply to discard the data and start
        // over.  This method only fires if you change the version
        // number for your database.  It does NOT depend on the
        // version number for your application.  If the schema is
        // updated without wiping data, commenting out the next 2
        // lines should be the top priority before modifying this
        // method.
        db.execSQL("DROP TABLE IF EXISTS " 
                   + AcronymEntry.TABLE_NAME);
        onCreate(db);
    }
}
