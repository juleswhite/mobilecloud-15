package vandy.mooc.activities;

import java.util.List;
import java.util.Locale;

import vandy.mooc.R;
import vandy.mooc.operations.AcronymOps;
import vandy.mooc.retrofit.AcronymData.AcronymExpansion;
import vandy.mooc.utils.AcronymDataArrayAdapter;
import vandy.mooc.utils.GenericActivity;
import vandy.mooc.utils.Utils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

/**
 * This Activity prompts the user for Acronyms to expand via Retrofit
 * and view via the results. Extends LifecycleLoggingActivity so its
 * lifecycle hook methods are logged automatically.
 */
public class AcronymActivity extends GenericActivity<AcronymOps> {
    /**
     * Acronym entered by the user.
     */
    protected EditText mEditText;
	
    /**
     * The ListView that will display the results to the user.
     */
    private ListView mListView;

    /**
     * A custom ArrayAdapter used to display the list of AcronymData objects.
     */
    private AcronymDataArrayAdapter mAdapter;

    /**
     * Hook method called when a new instance of Activity is created. One time
     * initialization code goes here, e.g., runtime configuration changes.
     * 
     * @param Bundle
     *            object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get references to the UI components.
        setContentView(R.layout.main_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = (EditText) findViewById(R.id.editText1);

        // Store the ListView for displaying the results entered.
        mListView = (ListView) findViewById(R.id.listView1);
        
        // Create a local instance of our custom Adapter for our
        // ListView.
        mAdapter = new AcronymDataArrayAdapter(this);

        // Set the adapter to the ListView.
        mListView.setAdapter(mAdapter);

    	// Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState,
                       AcronymOps.class);
    }

    /**
     * Initiate the synchronous acronym lookup when the user presses
     * the "Look Up" button.
     */
    public void expandAcronym(View v) {
        // Get the acronym entered by the user and convert to so it's
        // consistent with what we get back from the Acronym web
        // service.
        final String acronym =
            mEditText.getText().toString().toUpperCase(Locale.ENGLISH);

        if (acronym.isEmpty())
            Utils.showToast(this,
                            "no acronym provided");
        else {
            // Reset the display for the next acronym expansion.
            resetDisplay();

            // Expand the acronym.
            getOps().expandAcronym(acronym);
        }
    }

    /**
     * Display the acronym expansions to the user.
     * 
     * @param results
     *            List of acronym expansions to display.
     */
    public void displayResults(List<AcronymExpansion> results,
                               String errorMessage) {   	
        Log.d(TAG, "results = " + results);
        if (results == null 
            || results.size() == 0)
            Utils.showToast(this,
                            errorMessage);
        else {
            Log.d(TAG,
                  "displayResults() with number of acronyms = "
                  + results.size());

            // Add the results to the Adapter and notify changes.
            mAdapter.clear();
            mAdapter.addAll(results);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Reset the display prior to attempting to expand a new acronym.
     */
    private void resetDisplay() {
        Utils.hideKeyboard(this,
                           mEditText.getWindowToken());
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }
}
