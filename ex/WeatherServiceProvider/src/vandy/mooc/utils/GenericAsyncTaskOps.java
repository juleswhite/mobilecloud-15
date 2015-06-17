package vandy.mooc.utils;

public interface GenericAsyncTaskOps<Params, Progress, Result> {
    /**
     * Runs in a background thread.
     */
    Result doInBackground(Params param);

    /**
     * Runs in the UI Thread.
     */
    void onPostExecute(Result result,
                       Params param);
}
