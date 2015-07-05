package vandy.mooc.presenter;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import vandy.mooc.common.ConfigurableOps;
import vandy.mooc.common.ContextView;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.model.cache.WeatherTimeoutCache;
import vandy.mooc.model.webdata.WeatherData;
import vandy.mooc.model.webdata.WeatherWebServiceProxy;
import android.util.Log;

/**
 * This class implements the client-side operations that obtain
 * WeatherData from the Weather Sevice web service.  It implements
 * ConfigurableOps so it can be managed by the GenericActivity
 * framework.  It also implements GenericAsyncTaskOps so its
 * doInBackground() method will run in a background thread.  This
 * class plays the role of the "Presenter" in the Model-View-Presenter
 * pattern.
 */
public class WeatherOps 
       implements GenericAsyncTaskOps<String, Void, WeatherData>,
                  ConfigurableOps<WeatherOps.View>,
                  Callback<WeatherData> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        WeatherOps.class.getSimpleName();

    /**
     * This interface defines the minimum interface needed by the
     * WeatherOps class in the "Presenter" layer to interact with the
     * WeatherActivity in the "View" layer.
     */
    public interface View extends ContextView {
        /**
         * Displays the weather data to the user
         *
         * @param weatherList
         *            List of WeatherData to be displayed, which
         *            should not be null.
         */
        public void displayResults(WeatherData wd,
                                   String errorReason);
    }

    /**
     * Stores a Weak Reference to the WeatherOps.View so the garbage
     * collector can remove it when it's not in use.
     */
    protected WeakReference<WeatherOps.View> mWeatherView;

    /**
     * Content Provider-based cache for the WeatherData.
     */
    private WeatherTimeoutCache mCache;

    /**
     * Retrofit proxy that sends requests to the Weather Service web
     * service and converts the Json response to an instance of
     * AcronymData POJO class.
     */
    private WeatherWebServiceProxy mWeatherWebServiceProxy;

    /**
     * WeatherData object that is being displayed, which is used to
     * re-populate the UI after a runtime configuration change.
     */
    private WeatherData mCurrentWeatherData;

    /**
     * The GenericAsyncTask used to get the current weather from the
     * Weather Service web service.
     */
    private GenericAsyncTask<String,
                             Void,
                             WeatherData,
                             WeatherOps> mAsyncTask;

    /**
     * Store the requested weather location for error handling.
     */
    private String mLocation;

    /**
     * Keeps track of whether a call is already in progress and
     * ignores subsequent calls until the first call is done.
     */
    private boolean mCallInProgress;

    /**
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public WeatherOps() {
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the HobbitOpsImpl object after it's been created.
     *
     * @param view     The currently active HobbitOps.View.
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    public void onConfiguration(WeatherOps.View view,
                                boolean firstTimeIn) {
	// Reset the mWeatherView WeakReference.
	mWeatherView = new WeakReference<>(view);

	if (firstTimeIn) {
            // Initialize the WeatherTimeoutCache.  We use the
            // Application context to avoid dependencies on the
            // Activity context, which will change if/when a runtime
            // configuration change occurs.
	    mCache =
                new WeatherTimeoutCache(view.getApplicationContext());

	    // Build the RetroFit RestAdapter, which is used to create
	    // the RetroFit service instance, and then use it to build
	    // the RetrofitWeatherServiceProxy.
	    mWeatherWebServiceProxy =
                new RestAdapter
                    .Builder()
                    .setEndpoint(WeatherWebServiceProxy.sWeather_Service_URL_Retro)
                    .build()
                    .create(WeatherWebServiceProxy.class);
	} else if (mCurrentWeatherData != null) 
            // Populate the display if a WeatherData object is stored
            // in the WeatherOps instance.
            mWeatherView.get().displayResults
                (mCurrentWeatherData,
                 "");
    }

    /**
     * Initiate the asynchronous weather lookup when the user presses
     * the "Get Weather Async" button.
     * 
     * @return false if a call is already in progress, else true.
     */
    public boolean getWeatherAsync(String location) {
        if (mCallInProgress)
            return false;
        else {
            // Don't allow concurrent calls to get the weather.
            mCallInProgress = true;

            // Store this for error reporting purposes.
            mLocation = location;

            // Try to get the WeatherData from the cache.
            final WeatherData results = 
                getFromCache(location);
                
            if (results != null)
                // If the location is in the cache then handle a
                // successful result.
                handleResults(results,
                            "no weather for " 
                            + mLocation
                            + " found");      
            else 
                // If the location's data wasn't in the cache or was
                // stale then get the results from Weather Service web
                // service using a two-way asynchronous Retrofit RPC
                // call.
                mWeatherWebServiceProxy.getWeatherData(location,
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
                      mLocation);
    }

    /**
     * Called by Retrofit for handling success result.
     */
    @Override
    public void success(WeatherData results,
                        Response response) {
        Log.v(TAG,
              "Retrofit success");

        // Handle a successful result.
        handleResults(results,
                      mLocation);
    }

    /**
     * Initiate the synchronous weather lookup when the user presses
     * the "Get Weather Sync" button.
     * 
     * @return false if a call is already in progress, else true.
     */
    public boolean getWeatherSync(String location) {
        if (mCallInProgress)
            return false;
        else {
            // Don't allow concurrent calls to get the weather.
            mCallInProgress = true;

            if (mAsyncTask != null)
                // Cancel an ongoing operation to avoid having two
                // requests run concurrently.
                mAsyncTask.cancel(true);

            // Execute the AsyncTask to get the weather without
            // blocking the caller.
            mAsyncTask = new GenericAsyncTask<>(this);
            mAsyncTask.execute(location);
            return true;
        }
    }

    /**
     * Get the current weather either from the ContentProvider cache
     * or from the Weather Service web service.
     */
    @Override
    public WeatherData doInBackground(String... location) {
        mLocation = location[0];
        try {
            // First the cache is checked for the location's weather
            // data.
            WeatherData weatherData =
                getFromCache(mLocation);

            // If data is in cache return it.
            if (weatherData != null) {
                Log.v(TAG,
                      location 
                      + ": in cache");

                return weatherData;
            }

            // If the location's data wasn't in the cache or was
            // stale, use Retrofit to fetch it from the Weather
            // Service web service.
            else {
                Log.v(TAG,
                      location 
                      + ": not in cache");

                // Get the weather from the Weather Service web
                // service.
                return mWeatherWebServiceProxy
                       .getWeatherData(mLocation);
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
    @Override
    public void onPostExecute(WeatherData results) {
        // Indicate we're done with the AsyncTask.
        mAsyncTask = null;

        // Handle the result.
        handleResults(results,
                      "no weather for " 
                      + mLocation
                      + " found");      
    }

    /**
     * Try to get the @a location from the cache.
     */
    private WeatherData getFromCache(String location) {
        // Try to get the results from the cache.
        WeatherData weatherData =
            mCache.get(location);

        // If data is in cache return it.
        if (weatherData != null) {
            Log.v(TAG,
                  location
                  + ": in cache");

            // Return the results from the cache.
            return weatherData;
        } else {
            Log.v(TAG,
                  location
                  + ": not in cache");
            return null;
        }
    }

    /**
     * Handle the results by putting them into the cache (if they
     * aren't null) and displaying them.
     */
    private void handleResults(WeatherData results,
                               String reason) {
        // Put in cache if result is not null.
        if (results != null
            && results.getName() != null) {
            mCurrentWeatherData = results;
            mCache.put(mLocation,
                       results);
        }

        // Try to display the results.
        mWeatherView.get().displayResults(results,
                                          reason);
        
        // Allow another call to proceed when this method returns.
        mCallInProgress = false;
    }
}
