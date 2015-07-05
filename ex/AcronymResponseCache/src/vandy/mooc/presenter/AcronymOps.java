package vandy.mooc.presenter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import vandy.mooc.common.ConfigurableOps;
import vandy.mooc.common.ContextView;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.model.AcronymData;
import vandy.mooc.model.AcronymData.AcronymExpansion;
import vandy.mooc.model.AcronymWebServiceProxy;
import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

/**
 * This class implements all the acronym-related operations.  It
 * implements ConfigurableOps so it can be created/managed by the
 * GenericActivity framework.  It implements Callback so it can serve
 * as the target of an asynchronous Retrofit RPC call.  It extends
 * GenericAsyncTaskOps so its doInBackground() method runs in a
 * background task.
 */
public class AcronymOps 
       implements GenericAsyncTaskOps<String, Void, List<AcronymExpansion>>,
                  ConfigurableOps<AcronymOps.View>,
                  Callback<List<AcronymData>> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        AcronymOps.class.getSimpleName();

    /**
     * This interface defines the minimum interface needed by the
     * AcronymOps class in the "Presenter" layer to interact with the
     * AcronymActivity in the "View" layer.
     */
    public interface View extends ContextView {
        /**
         * Start a new Activity that displays the Acronym Expansions
         * to the user.
         * 
         * @param results
         *            List of AcronymExpansions to display.
         */
        void displayResults(List<AcronymExpansion> results,
                            String errorMessage);
    }
    
    /**
     * Name of the filename used for the cache.
     */
    private final static String CACHE_FILENAME = 
        "acronym_service_responses";

    /**
     * Used to enable garbage collection.
     */
    private WeakReference<AcronymOps.View> mAcronymView;

    /**
     * HttpResponseCache that will cache responses from 
     * Network calls and data 
     * will expire after a certain timeout.
     */
    private Cache mCache;

    /**
     * Client used by Retrofit to make network calls.  It manages the
     * cached responses and removes expired data.
     */
    private OkHttpClient mOkHttpClient;

    /**
     * It allows access to application-specific resources.
     */
    private Context mContext;

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
    private GenericAsyncTask<String, 
                             Void,
                             List<AcronymExpansion>,
                             AcronymOps> mAsyncTask;

    /**
     * Keeps track of whether a call is already in progress and
     * ignores subsequent calls until the first call is done.
     */
    private boolean mCallInProgress;

    /**
     * Store Acronym for error reporting purposes.
     */
    private String mAcronym;

    /**
     * Default constructor is needed by the GenericActivity framework.
     */
    public AcronymOps() {
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the AcronymOps object after it's been created.
     *
     * @param view         The currently active AcronymOps.View.
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    public void onConfiguration(AcronymOps.View view,
                                boolean firstTimeIn) {
        Log.d(TAG,
              "onConfiguration() called");

        // Reset the mAcronymView WeakReference.
        mAcronymView =
            new WeakReference<>(view);

        if (firstTimeIn) {
            // Store the Application context to avoid problems with
            // the Activity context disappearing during a rotation.
            mContext = view.getApplicationContext();
        
            // Set up the HttpResponse cache that will be used by
            // Retrofit.
            mCache = new Cache(new File(mContext.getCacheDir(),
                                        CACHE_FILENAME),
                               // Cache stores up to 1 MB.
                               1024 * 1024); 
        
            // Set up the client that will use this cache.  Retrofit
            // will use okhttp client to make network calls.
            mOkHttpClient = new OkHttpClient();  
            if (mCache != null) 
                mOkHttpClient.setCache(mCache);

            // Create a proxy to access the Acronym Service web
            // service.
            mAcronymWebServiceProxy =
                new RestAdapter.Builder()
                .setEndpoint(AcronymWebServiceProxy.ENDPOINT)
                .setClient(new OkClient(mOkHttpClient))
                // .setLogLevel(LogLevel.FULL)
                .setLogLevel(LogLevel.NONE)
                .build()
                .create(AcronymWebServiceProxy.class);
        } 
    }

    /**
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Async" button.  It uses Asynchronous
     * Retrofit Callbacks.
     */
    public boolean expandAcronymAsync(final String acronym) {
        if (mCallInProgress)
            return false;
        else {
            // Don't allow concurrent calls to get the weather.
            mCallInProgress = true;

            // Store this for error reporting purposes.
            mAcronym = acronym;

            // Get the results from Acronym Web service using a
            // two-way asynchronous Retrofit RPC call.
            mAcronymWebServiceProxy.getAcronymResults(acronym,
                                                      this);
            return true; 
        }
    }
    
    /**
     * Called by Retrofit for handling error.
     */
    @Override
    public void failure(RetrofitError error) {
        Log.v(TAG,
              "Retrofit failure");

        // Handle a failure result.
        handleResults(null,
                      mAcronym);
    }

    /**
     * Called by Retrofit for handling success result.
     */
    @Override
    public void success(List<AcronymData> results,
                        Response response) {
        Log.v(TAG,
              "Retrofit success");

        // Handle a successful result.
        handleResults(results.get(0).getLfs(),
                      mAcronym);
    }

    /**
     * Initiate the acronym lookup when the user presses the "Lookup
     * Acronym Sync" button.
     */
    public boolean expandAcronymSync(final String acronym) {
        if (mCallInProgress)
            return false;
        else {
            // Don't allow concurrent calls to expand the acronym.
            mCallInProgress = true;

            if (mAsyncTask != null)
                // Cancel an ongoing operation to avoid having two
                // requests run concurrently.
                mAsyncTask.cancel(true);

            // Execute the AsyncTask to expand the acronym without
            // blocking the caller.
            mAsyncTask = new GenericAsyncTask<>(this);
            mAsyncTask.execute(acronym);
            return true;
        }
    }
    
    /**
     * Retrieve the expanded acronym results via a synchronous two-way
     * Retrofit method call to get the Acronym expansions from the
     * Acronym web service, which runs in a background thread to avoid
     * blocking the UI thread.
     */
    public List<AcronymExpansion> doInBackground(String... acronym) {
        try {
            // Store the acronym for error reporting purposes.
            mAcronym = acronym[0];

            // Get the results from Acronym Web service using Retrofit
            // and return the "long forms" containing the expansions
            // of the acronym.
            return mAcronymWebServiceProxy
                        .getAcronymResults(mAcronym)
                        .get(0)
                        .getLfs();
        } catch (Exception e) {
            Log.v(TAG,
                  "doInBackground() "
                  + e);
            return null;
        }
    }

    /**
     * Display the results in the UI thread.
     */
    public void onPostExecute(List<AcronymExpansion> results) {
        // Display the results.
        handleResults(results,
                      mAcronym);
    }

    /**
     * Handle the results by putting them into the cache (if they
     * aren't null) and displaying them.
     */
    private void handleResults(List<AcronymExpansion> results,
                               String acronym) {
        // Try to display the results.
        mAcronymView.get().displayResults(results,
                                          "no expansions for " 
                                          + acronym 
                                          + " found");
        
        // Allow another call to proceed when this method returns.
        mCallInProgress = false;
    }
}
