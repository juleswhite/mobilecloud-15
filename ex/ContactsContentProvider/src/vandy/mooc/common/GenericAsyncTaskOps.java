package vandy.mooc.common;

/**
 * The base interface that an operations ("Ops") class can implement
 * so that it can be notified automatically by the GenericAsyncTask
 * framework during the AsyncTask processing.
 */
public abstract class GenericAsyncTaskOps<Params, Progress, Result> {
    /**
     * Called in the UI thread prior to running doInBackground() in a
     * background thread.
     */
     public void onPreExecute() {}
    
    /**
     * Called in a background thread to process the @a params.
     */
    @SuppressWarnings("unchecked")
    public abstract Result doInBackground(Params... params);

    /**
     * Called in the UI thread to process the @a result.
     */
    public void onPostExecute(Result result) {}
}

