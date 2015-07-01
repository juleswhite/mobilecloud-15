package vandy.mooc.common;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * This class provides the target .
 */
public class AsyncProviderCommandAdapter<ArgType>
       extends AsyncQueryHandler {
    /**
     * Constructor initializes the AsyncQueryHandler superclass.
     */
    public AsyncProviderCommandAdapter(ContentResolver contentResolver) {
        super(contentResolver);
    }
        
    /**
     * This method begins an asynchronous query. When the query is done
     * {@link #onQueryComplete} is called.
     *
     * @param token A token passed into {@link #onQueryComplete} to identify
     *  the query.
     * @param uri The URI, using the content:// scheme, for the content to
     *         retrieve.
     * @param projection A list of which columns to return. Passing null will
     *         return all columns, which is discouraged to prevent reading data
     *         from storage that isn't going to be used.
     * @param selection A filter declaring which rows to return, formatted as an
     *         SQL WHERE clause (excluding the WHERE itself). Passing null will
     *         return all rows for the given URI.
     * @param selectionArgs You may include ?s in selection, which will be
     *         replaced by the values from selectionArgs, in the order that they
     *         appear in the selection. The values will be bound as Strings.
     * @param orderBy How to order the rows, formatted as an SQL ORDER BY
     *         clause (excluding the ORDER BY itself). Passing null will use the
     *         default sort order, which may be unordered.
     */
    public void startQuery(AsyncProviderCommand<ArgType> command,
                           int token,
                           Uri uri,
                           String[] projection,
                           String selection,
                           String[] selectionArgs,
                           String orderBy) {
        startQuery(token,
                   command,
                   uri,
                   projection,
                   selection,
                   selectionArgs,
                   orderBy);
    }

    /**
     * This method begins an asynchronous insert. When the insert operation is
     * done {@link #onInsertComplete} is called.
     *
     * @param token A token passed into {@link #onInsertComplete} to identify
     *  the insert operation.
     * @param uri the Uri passed to the insert operation.
     * @param initialValues the ContentValues parameter passed to the insert operation.
     */
    public final void startInsert(AsyncProviderCommand<ArgType> command,
                                  int token,
                                  Uri uri,
                                  ContentValues initialValues) {
        startInsert(token,
                    command,
                    uri,
                    initialValues);
    }

    /**
     * This method begins an asynchronous update. When the update operation is
     * done {@link #onUpdateComplete} is called.
     *
     * @param token A token passed into {@link #onUpdateComplete} to identify
     *  the update operation.
     * @param uri the Uri passed to the update operation.
     * @param values the ContentValues parameter passed to the update operation.
     */
    public final void startUpdate(AsyncProviderCommand<ArgType> command,
                                  int token, 
                                  Uri uri,
                                  ContentValues values,
                                  String selection,
                                  String[] selectionArgs) {
        startUpdate(token,
                    command,
                    uri,
                    values,
                    selection,
                    selectionArgs);
    }

    /**
     * This method begins an asynchronous delete. When the delete operation is
     * done {@link #onDeleteComplete} is called.
     *
     * @param token A token passed into {@link #onDeleteComplete} to identify
     *  the delete operation.
     * @param uri the Uri passed to the delete operation.
     * @param selection the where clause.
     */
    public final void startDelete(AsyncProviderCommand<ArgType> command,
                                  int token,
                                  Uri uri,
                                  String selection,
                                  String[] selectionArgs) {
        startDelete(token,
                    command,
                    uri,
                    selection,
                    selectionArgs);
    }

    /**
     * Called when an asynchronous query is completed.
     *
     * @param token the token to identify the query, passed in from
     *            {@link #startQuery}.
     * @param cookie the cookie object passed in from {@link #startQuery}.
     * @param cursor The cursor holding the results from the query.
     */
    @SuppressWarnings("unchecked")
    protected void onQueryComplete(int token,
                                   Object cookie,
                                   Cursor cursor) {
        ((AsyncProviderCommand<ArgType>) cookie).onCompletion(token,
                                                              cursor);
    }

    /**
     * Called when an asynchronous insert is completed.
     *
     * @param token the token to identify the query, passed in from
     *        {@link #startInsert}.
     * @param cookie the cookie object that's passed in from
     *        {@link #startInsert}.
     * @param uri the uri returned from the insert operation.
     */
    @SuppressWarnings("unchecked")
    protected void onInsertComplete(int token,
                                    Object cookie,
                                    Uri uri) {
        ((AsyncProviderCommand<ArgType>) cookie).onCompletion(token,
                                                              uri);
    }

    /**
     * Called when an asynchronous update is completed.
     *
     * @param token the token to identify the query, passed in from
     *        {@link #startUpdate}.
     * @param cookie the cookie object that's passed in from
     *        {@link #startUpdate}.
     * @param result the result returned from the update operation
     */
    @SuppressWarnings("unchecked")
    protected void onUpdateComplete(int token,
                                    Object cookie,
                                    int result) {
        ((AsyncProviderCommand<ArgType>) cookie).onCompletion(token, 
                                                              result);
    }

    /**
     * Called when an asynchronous delete is completed.
     *
     * @param token the token to identify the query, passed in from
     *        {@link #startDelete}.
     * @param cookie the cookie object that's passed in from
     *        {@link #startDelete}.
     * @param result the result returned from the delete operation
     */
    @SuppressWarnings("unchecked")
    protected void onDeleteComplete(int token,
                                    Object cookie,
                                    int result) {
        ((AsyncProviderCommand<ArgType>) cookie).onCompletion(token, 
                                                              result);
    }
}
