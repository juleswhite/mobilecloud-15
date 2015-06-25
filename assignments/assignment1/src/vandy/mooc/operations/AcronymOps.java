package vandy.mooc.operations;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import vandy.mooc.activities.AcronymActivity;
import vandy.mooc.provider.cache.ContentProviderTimeoutCache;
import vandy.mooc.retrofit.AcronymData;
import vandy.mooc.retrofit.AcronymData.AcronymExpansion;
import vandy.mooc.retrofit.AcronymWebServiceProxy;
import vandy.mooc.utils.ConfigurableOps;
import vandy.mooc.utils.GenericAsyncTask;
import vandy.mooc.utils.GenericAsyncTaskOps;
import android.app.Activity;
import android.util.Log;

/**
 * This class implements all the acronym-related operations defined in
 * the AcronymOps interface.
 */
public class AcronymOps
       implements ConfigurableOps,
                  GenericAsyncTaskOps<String, Void, List<AcronymExpansion>> {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();
   
    /**
     * Used to enable garbage collection.
     */
    private WeakReference<AcronymActivity> mActivity;

    /**
     * List of results to display (if any).
     */
    private List<AcronymExpansion> mResults;

    /**
     * Timeout cache that uses Content Providers to cache data and
     * uses AlarmManager and Service to remove expired cache entries.
     * Default timeout is 10 seconds.
     */
    private ContentProviderTimeoutCache mAcronymCache;

    /**
     * Retrofit proxy that sends requests to the Acronym web service
     * and converts the Json response to an instance of AcronymData
     * POJO class.
     */
    private AcronymWebServiceProxy mAcronymWebServiceProxy;

    /**
     * The GenericAsyncTask used to expand an acronym in a background
     * thread via the Acronym web service.
     */
    private GenericAsyncTask<String, Void, List<AcronymExpansion>, AcronymOps> mAsyncTask;

    /**
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public AcronymOps() {
    }

    /**
     * Called after a runtime configuration change occurs to finish
     * the initialisation steps.
     */
    public void onConfiguration(Activity activity,
                                boolean firstTimeIn) {
        final String time = firstTimeIn ? "first time" : "second+ time";
        Log.d(TAG,
              "onConfiguration() called the "
              + time
              + " with activity = "
              + activity);

        // (Re)set the mActivity WeakReference.
        mActivity = new WeakReference<>((AcronymActivity) activity);

        if (firstTimeIn) {
            // Initialize the TimeoutCache.
            mAcronymCache = 
                new ContentProviderTimeoutCache
                (activity.getApplicationContext());

            // Create a proxy to access the Acronym web service.  TODO
            // -- you fill in here, replacing "null" with the
            // appropriate initialization of the proxy.
            mAcronymWebServiceProxy = null;
        } else
            // Update the results on the UI.
            updateResultsDisplay();
    }

    /**
     * Display results if any (due to runtime configuration change).
     */
    private void updateResultsDisplay() {
        if (mResults != null)
            mActivity.get().displayResults(mResults, 
                                           null);
    }

    /**
     * Initiate the synchronous acronym lookup when the user presses the
     * "Lookup Acronym" button.
     */
    public void expandAcronym(final String acronym) {
        if (mAsyncTask != null)
            // Cancel an ongoing operation to avoid having two
            // requests run concurrently.
            mAsyncTask.cancel(true);

        // Execute the AsyncTask to expand the acronym without
        // blocking the caller.
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute(acronym);
    }

    /**
     * Retrieve the expanded acronym results via a synchronous two-way
     * method call, which runs in a background thread to avoid
     * blocking the UI thread.
     */
    public List<AcronymExpansion> doInBackground(String acronym) {
        try {
            // Try to get the results from the cache.
            List<AcronymExpansion> longForms =
                mAcronymCache.get(acronym);

            // If data is in cache return it.
            if (longForms != null
                && !longForms.isEmpty()) {
                Log.v(TAG,
                      acronym
                      + ": in cache");

                // Return the results from the cache.
                return longForms;
            }

            // If the location's data wasn't in the cache or was
            // stale, use Retrofit to fetch it from the Acronym web
            // service.
            else {
                Log.v(TAG,
                      acronym
                      + ": not in cache");

                // Get the results from Acronym Web service using a
                // two-way Retrofit RPC call.
                // TODO -- you fill in here, replacying "null" with a
                // call to the appropriate method on the proxy.
                AcronymData result = null;
                        
                // Get the "long forms" of the acronym expansion.
                longForms = result.getLfs();

                // Put the long forms into the cache using the "short
                // form" of the acronym.
                mAcronymCache.put(result.getSf(),
                                  longForms);

                // Return the results that were just stored in the
                // cache.
                return longForms;
            }
        } catch (Exception e) {
            Log.v(TAG,
                  "doInBackground() "
                  + e);
            return null;
        }
    }

    /**
     * Display the results in the UI Thread.
     */
    public void onPostExecute(List<AcronymExpansion> acronymExpansionsList,
                              String acronym) {
        // Store the acronym data in anticipation of runtime
        // configuration changes.
        mResults = acronymExpansionsList;

        // Try to display the results.
        mActivity.get().displayResults(acronymExpansionsList,
                                       "no expansions for " 
                                       + acronym 
                                       + " found");

        // Indicate the AsyncTask is done.
        mAsyncTask = null;
    }
}
