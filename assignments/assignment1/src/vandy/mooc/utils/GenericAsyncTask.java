package vandy.mooc.utils;

import android.os.AsyncTask;

/**
 * Defines a generic framework for running an AsyncTask that delegates
 * its operations to the @a Ops parameter.
 */
public class GenericAsyncTask<Params,
                              Progress,
                              Result, 
                              Ops extends GenericAsyncTaskOps<Params, Progress, Result>>
      extends AsyncTask<Params, Progress, Result> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();
    
    /**
     * Params instance.
     */
    private Params mParam;

    /**
     * Reference to the enclosing Ops object.
     */
    protected Ops mOps;

    /**
     * Constructor initializes the field.
     */
    public GenericAsyncTask(Ops ops) {
	mOps = ops;
    }

    /**
     * Run in a background thread to avoid blocking the UI thread.
     */
    @SuppressWarnings("unchecked")
    protected Result doInBackground(Params... params) {
        mParam = params[0];

        return mOps.doInBackground(mParam);
    }

    /**
     * Process results in the UI Thread.
     */
    protected void onPostExecute(Result result) {
        mOps.onPostExecute(result,
                           mParam);
    }
}
