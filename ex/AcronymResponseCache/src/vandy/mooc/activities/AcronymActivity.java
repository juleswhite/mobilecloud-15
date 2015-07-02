package vandy.mooc.activities;

import java.util.List;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.operations.AcronymOps;
import vandy.mooc.retrofit.AcronymData.AcronymExpansion;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * This Activity prompts the user for Acronyms to expand via Retrofit
 * and view via the results.  It extends GenericActivity, which
 * provides a framework that automatically handles runtime
 * configuration changes.
 */
public class AcronymActivity extends GenericActivity<AcronymOps> {
    /**
     * Acronym entered by the user.
     */
    protected EditText mEditText;
	
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., storing Views.
     * 
     * @param Bundle
     *            object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get references to the UI components.
        setContentView(R.layout.acronym_activity);

        // Store the EditText that holds the urls entered by the user
        // (if any).
        mEditText = (EditText) findViewById(R.id.editText1);

        // Call up to the special onCreate() method in
        // GenericActivity, passing in the AcronymOps class to
        // instantiate and manage.
        super.onCreate(savedInstanceState,
                       AcronymOps.class);
   }

    /**
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Async" button.
     */
    public void expandAcronymAsync(View v) {
        // Try to get an acronym entered by the user.
        final String acronym =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim());

        if (acronym != null) {
            Log.d(TAG,
                  "calling expandAcronymAsync() for "
                  + acronym);

            // Synchronously expand the acronym.
            if (getOps().expandAcronymAsync(acronym) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Call already in progress");
        }
    }

    /**
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Sync" button.
     */
    public void expandAcronymSync(View v) {
        // Try to get an acronym entered by the user.
        final String acronym =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim());

        if (acronym != null) {
            Log.d(TAG,
                  "calling expandAcronymSync() for "
                  + acronym);

            // Synchronously expand the acronym.
            if (getOps().expandAcronymSync(acronym) == false)
                // Show error message to user.
                Utils.showToast(this,
                                "Call already in progress");
        }
    }

    /**
     * Start a new Activity that displays the Acronym Expansions to
     * the user.
     * 
     * @param results
     *            List of AcronymExapnsions to be displayed.
     */
    public void displayResults(List<AcronymExpansion> results,
                               String errorMessage) {
        if (results == null)
            Utils.showToast(this,
                            errorMessage);
        else {
            Log.d(TAG,
                  "displayResults() with number of acronyms = "
                  + results.size());

            // Create an intent that will start an Activity to display
            // the Acronym Expansions to the user.
            final Intent intent =
                DisplayAcronymActivity.makeIntent(results);

            // Verify that the intent will resolve to an Activity.
            if (intent.resolveActivity(getPackageManager()) != null)
                // Start the DisplayAcronymExpansionsActivity with
                // this implicit intent.
                startActivity(intent);
            else
                // Show error message to user.
                Utils.showToast(this,
                                "No Activity found to display Acronym Expansions");
        }
    }
}
