package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.model.webdata.WeatherData;
import vandy.mooc.presenter.WeatherOps;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * The main Activity that prompts the user for a location and then
 * displays WeatherData about this location via either retrieving the
 * WeatherData from a ContentProvider-based cache or from the Weather
 * Service web service via the use of Retrofit.  This class plays the
 * role of the "View" in the Model-View-Presenter (MVP) pattern.  It
 * extends GenericActivity that provides a framework for automatically
 * handling runtime configuration changes of a WeatherOps object,
 * which plays the role of the "Presenter" in the MVP pattern.  The
 * WeatherOps.View interface is used to minimize dependencies between
 * the View and Presenter layers.
 */
public class DownloadWeatherActivity
       extends GenericActivity<WeatherOps.View, WeatherOps>
       implements WeatherOps.View {
    /**
     * Weather location entered by the user.
     */
    protected EditText mEditText;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., initializing
     * views.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get references to the UI components.
        setContentView(R.layout.download_weather_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = ((EditText) findViewById(R.id.locationQuery));
        
        // Invoke the special onCreate() method in GenericActivity,
        // passing in the WeatherOps class to instantiate/manage and
        // "this" to provide WeatherOps with the WeatherOps.View
        // instance.
        super.onCreate(savedInstanceState,
                       WeatherOps.class,
                       this);
    }

    /**
     * Hook method called by Android when this Activity becomes
     * invisible.
     */
    @Override
    protected void onDestroy() {
    	 // Always call super class for necessary operations when
        // stopping.
        super.onDestroy();
    }

    /*
     * Initiate the synchronous weather lookup when the user presses
     * the "Get Weather Sync" button.
     */
    public void getWeatherSync(View v) {
        // Hide the keyboard.
        Utils.hideKeyboard(this,
                           mEditText.getWindowToken());

        // Get the location entered by the user.
        final String location =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim(),
                                 true);
        if (location != null) {
            // Synchronously get the weather for the location.
            if (getOps().getWeatherSync(location) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Call already in progress");

            // Return focus to edit box and select all text in it
            // after query.
            mEditText.requestFocus();
            mEditText.selectAll();
        }
    }

    /*
     * Initiate the asynchronous weather lookup when the user presses
     * the "Get Weather Async" button.
     */
    public void getWeatherAsync(View v) {
        // Hide the keyboard.
        Utils.hideKeyboard(this,
                           mEditText.getWindowToken());

        // Get the location entered by the user.
        final String location =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim(),
                                 true);
        if (location != null) {
            // Asynchronously get the weather for the location.
            if (getOps().getWeatherAsync(location) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Call already in progress");

            // Return focus to edit box and select all text in it
            // after query.
            mEditText.requestFocus();
            mEditText.selectAll();
        }
    }

    /**
     * Start a new Activity that displays the WeatherData to the user.
     *
     * @param weatherList
     *            List of WeatherData to be displayed, which should not be null.
     */
    @Override
    public void displayResults(WeatherData weatherData,
                               String errorMessage) {
        // Only display the results if we got valid WeatherData.
        if (weatherData == null) 
            Utils.showToast(this,
                            errorMessage);
        else {
            // Create an intent that will start an Activity to display
            // the WeatherData to the user.
            final Intent intent = DisplayWeatherActivity.makeIntent
                ((WeatherData) weatherData);
       
            // Verify that the intent will resolve to an Activity.
            if (intent.resolveActivity(getPackageManager()) != null)
                // Start the DisplayWeatherActivity with this implicit
                // intent.
                startActivity(intent);
            else
                // Show error message to user.
                Utils.showToast(this,
                                "No Activity found to display Weather Data");
        }
    }
}
