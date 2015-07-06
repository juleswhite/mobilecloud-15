package vandy.mooc.model.provider;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Defines methods that access the Android MediaStore Video Content
 * Provider and do CRUD operations on it.
 */
public class AndroidVideoCache {
    /**
     * Allows access to application-specific resources and classes.
     */
    private Context mContext;
    
    /**
     * Constructor that initializes the AndroidVideoCache.
     * 
     * @param context
     */
    public AndroidVideoCache(Context context){
        mContext = context;
    }
  
    /**
     * Gets the video from Android Video Content Provider by a given
     * Id.
     * 
     * @param videoId
     * @return Video having the given Id
     */
    public Video getVideoById(long videoId) {
        // Build the Uri to the Video having given video Id.
        Uri videoUri = 
            ContentUris.withAppendedId
               (MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoId);
        
        // Cursor that is returned as a result of database query which
        // points to one row.
        try (Cursor cursor =
                 mContext.getContentResolver().query(videoUri,
                                                     null,
                                                     null,
                                                     null,
                                                     null)) {
            // Check if there is any row returned by the query.
            if (cursor.moveToFirst()) 
               // Get the Video metadata from Android Video Content
               // Provider
               return getVideo(cursor);
            else
                // Return null if there id no row returned by the
                // Query.
                return null;
        }
    }
    
    /**
     * Returns the filePath of the Video with a given videoTitle from
     * Android Video Content Provider.
     * 
     * @param videoTitle
     * @return FilePath of the Video having given Title.
     */
    public String getVideoFilePath(String videoTitle){
        // Selection clause that will return video having given Name.
        String mSelectionClause =
            MediaStore.Video.Media.DISPLAY_NAME + "=?";
        
        // Selection Argument that will be used by Selection statement
        // to return video having given Name.
        String[] args = { videoTitle };
        
        // Cursor returned as a result of database query that points
        // to one row.
        try (Cursor cursor =
                 mContext.getContentResolver().query
                     (MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                      null,
                      mSelectionClause,
                      args,
                      null)) {
            if (cursor.moveToFirst()) 
                // Get the file Path of Video having given title from
                // Android Video Content Provider.
                return cursor.getString
                    (cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            else
                // Return null if there id no row returned by the
                // Query.
                return null;
        }
    }
    
    /**
     * Get the Video metadata from the given Cursor.
     * 
     * @param cursor
     * @return Video metadata from the Cursor.
     * @throws IllegalArgumentException
     */
    private Video getVideo(Cursor cursor) throws IllegalArgumentException {
        // Get the Name of the Video, which is "videoName.mp4"
        String name =
            cursor.getString
             (cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
        
        // Get the duration of the video.
        long duration =
            cursor.getLong
              (cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
        
         // Get the MIME_TYPE of the video.
        String contentType =
            cursor.getString
              (cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));

        // Return the instance of the Video having the given fields.
        // This object hasn't set the Id or dataUrl of the Video,
        // which will be null for uploading since the Video Service
        // will generate these fields.
        return new Video(name,
                         duration,
                         contentType);
    }
}
