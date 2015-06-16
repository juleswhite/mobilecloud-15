package vandy.mooc.operations;

import java.lang.ref.WeakReference;

import retrofit.RestAdapter;
import vandy.mooc.activities.WeatherActivity;
import vandy.mooc.provider.cache.WeatherTimeoutCache;
import vandy.mooc.retrofitWeather.WeatherData;
import vandy.mooc.retrofitWeather.WeatherWebServiceProxy;
import vandy.mooc.utils.ConfigurableOps;
import vandy.mooc.utils.RetainedFragmentManager;
import vandy.mooc.utils.Utils;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This class implements all the weather-related operations defined in the
 * WeatherOps interface.
 */
public class WeatherOps implements ConfigurableOps {
    protected static final String RFM_KEY = "doInBackgroundResult";

    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Used to enable garbage collection.
     */
    protected WeakReference<WeatherActivity> mActivity;

    /**
     * Cache
     */
    private WeatherTimeoutCache mCache;

    /**
     * URL to the weather api to use with the Retrofit service.
     */
    private static String sWeather_Service_URL_Retro =
        "http://api.openweathermap.org/data/2.5";

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
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public WeatherOps() {
    }

    /**
     * Called by the WeatherOps constructor and after a runtime configuration
     * change occurs to finish the initialization steps.
     */
    public void onConfiguration(Activity activity, boolean firstTimeIn) {
	// Reset the mActivity WeakReference.
	mActivity = new WeakReference<>((WeatherActivity) activity);

	if (firstTimeIn) {
            // Initialize the TimeoutCache.
	    mCache =
                new WeatherTimeoutCache(activity.getApplicationContext());

	    // Build the RetroFit RestAdapter, which is used to create
	    // the RetroFit service instance, and then use it to build
	    // the RetrofitWeatherServiceProxy.
	    mWeatherWebServiceProxy =
                new RestAdapter.Builder()
                .setEndpoint(sWeather_Service_URL_Retro)
                .build()
                .create(WeatherWebServiceProxy.class);
	} else {
            // Populate the display if a WeatherData object is stored in
            // the WeatherOps instance.
            if (mCurrentWeatherData != null) 
                mActivity.get().displayResults
                    (mCurrentWeatherData);
        }
    }

    /**
     * Initiate the synchronous weather lookup when the user presses
     * the "Get Weather" button.
     */
    public void getCurrentWeather(String location) {
        new AsyncTask<String, Void, WeatherData>() {
            /**
             * Location we're trying to get current weather for.
             */
            private String mLocation;

            /**
             * Retrieve the expanded weather results via a synchronous
             * two-way method call, which runs in a background thread to
             * avoid blocking the UI thread.
             */
            protected WeatherData doInBackground(String... locations) {
                mLocation = locations[0];
                Log.v(TAG,
                      "Checking Cache");

                // First the cache is checked for the location's
                // weather data.
                WeatherData weatherData = mCache.get(mLocation);

                // If the location's data wasn't in the cache or
                // was stale, fetch it from the server.
                if (weatherData == null) {
                    Log.v(TAG, mLocation + ": not in cache");

                    // Get the weather from the Weather Service.
                    weatherData = 
                        mWeatherWebServiceProxy.getWeatherData(mLocation);

                    // Check to make sure the call to the server
                    // succeeded by testing the "name" member to make
                    // sure it was initialized
                    if (weatherData.getName() == null)
                        return null;

                    // Add to cache.
                    mCache.put(mLocation,
                               weatherData);
                }
                return weatherData;
            }

            /**
             * Display the results in the UI Thread.
             */
            protected void onPostExecute(WeatherData weatherData) {
                if (weatherData == null)
                    Utils.showToast(mActivity.get(),
                                    "no weather for "
                                    + mLocation
                                    + " found");
                } else {
                    // Store the weather data in anticipation of
                    // runtime configuration changes.
                    mCurrentWeatherData = weatherData;

                    // If the object was found, display the results.
                    mActivity.get().displayResults(weatherData);
                }
            }
            // Execute the AsyncTask to expand the weather without
            // blocking the caller.
        }.execute(location);
    }
}
