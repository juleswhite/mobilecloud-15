package vandy.mooc.operations;

import vandy.mooc.retrofitWeather.WeatherData;
import vandy.mooc.utils.Utils;
import android.os.AsyncTask;
import android.util.Log;

public class GetWeatherAsyncTask 
      extends AsyncTask<String, Void, WeatherData> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final String TAG = getClass().getSimpleName();
    
    /**
     * Location we're trying to get current weather for.
     */
    private String mLocation;

    /**
     * Reference to the enclosing WeatherOps object.
     */
    protected WeatherOps mOps;

    /**
     * Constructor initializes the field.
     */
    public GetWeatherAsyncTask(WeatherOps ops) {
	mOps = ops;
    }

    /**
     * Retrieve the expanded weather results via a synchronous
     * two-way method call, which runs in a background thread to
     * avoid blocking the UI thread.
     */
    protected WeatherData doInBackground(String... locations) {
        mLocation = locations[0];

        Log.v(TAG,
              "Checking Cache");
        
        return mOps.getWeather(mLocation);
    }

    /**
     * Display the results in the UI Thread.
     */
    protected void onPostExecute(WeatherData weatherData) {
        mOps.displayResults(weatherData,
                            mLocation);
    }
}
