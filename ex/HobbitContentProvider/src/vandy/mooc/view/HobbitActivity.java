package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.presenter.HobbitOps;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * This Activity illustrates how to use the HobbitContentProvider to
 * perform various "CRUD" (i.e., insert, query, update, and delete)
 * operations using characters from Tolkien's classic book "The
 * Hobbit."  This class plays the role of the "View" in the
 * Model-View-Presenter (MVP) pattern.  It extends GenericActivity
 * that provides a framework for automatically handling runtime
 * configuration changes of a HobbitOps object, which plays the role
 * of the "Presenter" in the MVP pattern.  The HobbitOps.View
 * interface is used to minimize dependencies between the View and
 * Presenter layers.
 */
public class HobbitActivity 
       extends GenericActivity<HobbitOps.View, HobbitOps>
       implements HobbitOps.View {
    /**
     * ListView displays the Hobbit character information.
     */
    private ListView mListView;

    /**
     * Uri for the "Necromancer".
     */
    private Uri mNecromancerUri;
    
    /**
     * Used to display the results of contacts queried from the
     * HobbitContentProvider.
     */
    private SimpleCursorAdapter mAdapter;

    /**
     * Menu on main screen.
     */
    protected Menu mOpsOptionsMenu;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., initializing
     * views.
     *
     * @param Bundle
     *            object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Set the content view for this Activity.
        setContentView(R.layout.hobbit_activity);

        // Initialize the List View.
        mListView = (ListView) findViewById(R.id.list);

        // Invoke the special onCreate() method in GenericActivity,
        // passing in the HobbitOps class to instantiate/manage and
        // "this" to provide HobbitOps with the HobbitOps.View
        // instance.
        super.onCreate(savedInstanceState,
                       HobbitOps.class,
                       this);

        // Initialize the SimpleCursorAdapter.
        mAdapter = getOps().makeCursorAdapter();

        // Connect the ListView with the SimpleCursorAdapter.
        mListView.setAdapter(mAdapter);
    }

    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads.  This method may not always be called
     * when the Android system kills the hosting process.
     */
    @Override
    public void onDestroy() {
        // Call up to the superclass's onDestroy() hook method.
        super.onDestroy();
        
        // Close down the HobbitOps.
        getOps().close();
    }

    /**
     * This method is run when the user clicks the "Add All" button.
     * It insert various characters from the Hobbit book into the
     * "database".
     */
    public void addAll(View v) {
        try {
            // Insert the main protagonist.
            getOps().insert("Bilbo",
                            "Hobbit");

            // Insert the main wizard.
            getOps().insert("Gandalf",
                            "Maia");

            // Insert all the dwarves.
            getOps().bulkInsert(new String[] { 
                    "Thorin", "Kili", "Fili",
                    "Balin", "Dwalin", "Oin", "Gloin",
                    "Dori", "Nori", "Ori",
                    "Bifur", "Bofur", "Bombur"
                },
                "Dwarf");

            // Insert the main antagonist.
            getOps().insert("Smaug",
                            "Dragon");

            // Insert Beorn.
            getOps().insert("Beorn",
                            "Man");

            // Insert the Master of Laketown
            getOps().insert("Master",
                            "Man");

            // Insert another antagonist.
            mNecromancerUri = 
                getOps().insert("Necromancer",
                                "Maia");

            // Display the results;
            getOps().displayAll();
        } catch (RemoteException e) {
            Log.d(TAG, 
                  "exception " 
                  + e);
        }
    }

    /**
     * This method is run when the user clicks the "Modify All" button
     * to modify certain Hobbit characters from the "database."
     */
    public void modifyAll(View v) {
        try {
            // Update Beorn's race since he's a skinchanger.
            getOps().updateRaceByName("Beorn",
                                      "Bear");

            if (mNecromancerUri != null)
                // The Necromancer is really Sauron the Deceiver.
                getOps().updateByUri(mNecromancerUri,
                                     "Sauron",
                                     "Maia");

            // Delete dwarves who get killed in the Battle of Five
            // Armies.
            getOps().deleteByName(new String[] { 
                    "Thorin",
                    "Kili",
                    "Fili" 
                });

            // Delete Smaug since he gets killed by Bard the Bowman
            // and the "Master" (who's a man) since he's killed later
            // in the book.
            getOps().deleteByRace(new String[] { 
                    "Dragon",
                    "Man" 
                });

            // Display the results;
            getOps().displayAll();
        } catch (RemoteException e) {
            Log.d(TAG, 
                  "exception " 
                  + e);
        }
    }

    /**
     * This method is run when the user clicks the "Delete All" button
     * to remove all Hobbit characters from the "database."
     */
    public void deleteAll(View v) {
        try {
            // Clear out the database.
            int numDeleted = getOps().deleteAll();

            // Inform the user how many characters were deleted.
            Toast.makeText(this,
                           "Deleted "
                           + numDeleted
                           + " Hobbit characters",
                           Toast.LENGTH_SHORT).show();

            // Display the results;
            getOps().displayAll();
        } catch (RemoteException e) {
            Log.d(TAG, 
                  "exception " 
                  + e);
        }
    }

    /**
     * This method is run when the user clicks the "Display All"
     * button to display all races of Hobbit characters from the
     * "database."
     */
    public void displayAll(View v) {
        try {
            // Display the results.
            getOps().displayAll();
        } catch (RemoteException e) {
            Log.d(TAG, 
                  "exception " 
                  + e);
        }
    }

    /**
     * Called by Android framework when menu option is clicked.
     * 
     * @param item
     * @return true
     */
    public boolean chooseOpsOption(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.contentResolver:
            getOps().setContentProviderAccessType
                (HobbitOps.ContentProviderAccessType.CONTENT_RESOLVER);
            Toast.makeText(this,
                           "ContentResolver selected",
                           Toast.LENGTH_SHORT).show();
            break;

        case R.id.contentProviderClient:
            getOps().setContentProviderAccessType
                (HobbitOps.ContentProviderAccessType.CONTENT_PROVIDER_CLIENT); 
            Toast.makeText(this,
                           "ContentProviderClient selected",
                           Toast.LENGTH_SHORT).show();
            break;
        }

        // The calls to setContentProviderAccessType() above will set
        // the new implementation type and construct a new instance of
        // that implementation.  These changes require initializing
        // the implementation WeakReference to this Activity, which
        // can be accomplished by generating a fake configuration
        // change event.  Moreover, since the HobbitOps implementation
        // was just constructed and is not being restored, we need to
        // pass in true for the "firstTimeIn" in parameter.
        getOps().onConfiguration(this, 
                                 true);
        return true;
    }

    /**
     * Inflates the Operations ("Ops") Option Menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOpsOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ops_options_menu,
                         menu);
        return true;
    }

    /**
     * Display the contents of the cursor as a ListView.
     */
    @Override
    public void displayCursor(Cursor cursor) {
    	// Display the designated columns in the cursor as a List in
        // the ListView connected to the SimpleCursorAdapter.
        mAdapter.changeCursor(cursor);
    }
}
