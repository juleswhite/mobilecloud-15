package vandy.mooc.view;

import java.util.List;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.model.AcronymData.AcronymExpansion;
import vandy.mooc.presenter.AcronymOps;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * This Activity prompts the user for Acronyms to expand via Retrofit
 * and view via the results via the DisplayAcronymActivity.  This
 * class plays the role of the "View" in the Model-View-Presenter
 * (MVP) pattern.  It extends GenericActivity that provides a
 * framework for automatically handling runtime configuration changes
 * of an AcronymOps object, which plays the role of the "Presenter" in
 * the MVP pattern.  The AcronymOps.View interface is used to minimize
 * dependencies between the View and Presenter layers.
 */
public class AcronymExpansionActivity
       extends GenericActivity<AcronymOps.View, AcronymOps>
       implements AcronymOps.View {
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

        // Invoke the special onCreate() method in GenericActivity,
        // passing in the AcronymOps class to instantiate/manage and
        // "this" to provide AcronymOps with the AcronymOps.View
        // instance.
        super.onCreate(savedInstanceState,
                       AcronymOps.class,
                       this);
   }

    /**
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Async" button.
     */
    public void expandAcronymAsync(View v) {
        // Try to get an acronym entered by the user.
        final String acronym =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim(),
                                 true);

        if (acronym != null) {
            Log.d(TAG,
                  "calling expandAcronymAsync() for "
                  + acronym);

            // Synchronously expand the acronym.
            if (getOps().expandAcronymAsync(acronym) == false)
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
     * Initiate the synchronous acronym lookup when the user presses
     * the "Lookup Acronym Sync" button.
     */
    public void expandAcronymSync(View v) {
        // Try to get an acronym entered by the user.
        final String acronym =
            Utils.uppercaseInput(this,
                                 mEditText.getText().toString().trim(),
                                 true);

        if (acronym != null) {
            Log.d(TAG,
                  "calling expandAcronymSync() for "
                  + acronym);

            // Synchronously expand the acronym.
            if (getOps().expandAcronymSync(acronym) == false)
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
     * Start a new Activity that displays the Acronym Expansions to
     * the user.
     * 
     * @param results
     *            List of AcronymExpansions to display.
     */
    @Override
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
                DisplayExpansionActivity.makeIntent(results);

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
