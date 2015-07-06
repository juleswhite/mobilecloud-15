package vandy.mooc.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * @@ 
 */
public class VideoGalleryUtils {
    /** 
     * Get a Video ID from a Uri. This will get the the VideoId for
     * the Storage Access Framework Documents and MediaStore Content
     * Provider.
     * 
     * @param context The context. 
     * @param uri The Uri to query. 
     * 
     * return videoId
     */ 
    public static Long getVideoId(final Context context,
                                  final Uri uri) {
        final boolean isKitKat =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
     
        // DocumentProvider 
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider 
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
     
                if ("primary".equalsIgnoreCase(type)) {
                    return Long.parseLong(split[1]);
                } 
                  
            } 
            // DownloadsProvider 
            else if (isDownloadsDocument(uri)) {
                return Long.parseLong(DocumentsContract.getDocumentId(uri));
            } 
            
            // MediaProvider 
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                
                return Long.parseLong(split[1]);
            }
        }
       
        return null; 
    } 
    
    /** 
     * Get a Video file path from a Uri. This will get the the path
     * for Storage Access Framework Documents, as well as the _data
     * field for the MediaStore and other file-based ContentProviders.
     * 
     * @param context The context. 
     * @param uri The Uri to query. 
     * 
     * return videoFilePath
     */ 
    public static String getPath(final Context context, final Uri uri) {
     
        final boolean isKitKat =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
     
        // DocumentProvider 
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider 
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
     
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } 
     
                
            } 
            // DownloadsProvider 
            else if (isDownloadsDocument(uri)) {
     
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
     
                return getVideoDataColumn(context, contentUri, null, null);
            } 
            // MediaProvider 
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                
                Uri contentUri = 
                   MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
               
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                }; 
     
                return getVideoDataColumn(context, contentUri, selection, selectionArgs);
            } 
        } 
        // MediaStore (and general) 
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getVideoDataColumn(context, uri, null, null);
        }
        
        // File 
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        } 
     
        return null; 
    } 
     
    /** 
     * Get the value of the data column for this Uri. This is useful for 
     * MediaStore Uris, and other file-based ContentProviders. 
     * 
     * @param context The context. 
     * @param uri The Uri to query. 
     * @param selection (Optional) Filter used in the query. 
     * @param selectionArgs (Optional) Selection arguments used in the query. 
     * @return The value of the _data column, which is typically a file path. 
     */ 
    private static String getVideoDataColumn(Context context, Uri uri, String selection,
            String[] selectionArgs) {
     
        final String[] projection = {
                MediaStore.Video.Media.DATA
        }; 
     
        try (Cursor cursor = context.getContentResolver().query(uri,
                                                                projection,
                                                                selection,
                                                                selectionArgs,
                                                                null)){ 
            if (cursor != null && cursor.moveToFirst()) {
                final int index =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                return cursor.getString(index);
            } 
        } 
        
        return null; 
    } 
     
     
    /** 
     * @param uri The Uri to check. 
     * @return Whether the Uri authority is ExternalStorageProvider. 
     */ 
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    } 
     
    /** 
     * @param uri The Uri to check. 
     * @return Whether the Uri authority is DownloadsProvider. 
     */ 
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    } 
     
    /** 
     * @param uri The Uri to check. 
     * @return Whether the Uri authority is MediaProvider. 
     */ 
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    } 
}
