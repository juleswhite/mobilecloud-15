package vandy.mooc.activities;

import vandy.mooc.R;
import vandy.mooc.operations.HobbitOps;
import vandy.mooc.provider.CharacterContract;
import vandy.mooc.utils.GenericActivity;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * This Activity illustrates how to use the HobbitContentProvider to
 * perform various "CRUD" (i.e., insert, query, update, and delete)
 * operations using characters from Tolkien's classic book "The
 * Hobbit."
 */
public class HobbitActivity extends GenericActivity<HobbitOps> {
    /**
     * ListView displays the Hobbit character information.
     */
    private ListView mListView;

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

        // Call up to the special onCreate() method in
        // GenericActivity, passing in the HobbitOps class to
        // instantiate and manage.
        super.onCreate(savedInstanceState,
                       HobbitOps.class);
    }

    /**
     * This method is run when the user clicks the "Run" button.  It
     * performs various CRUD operations using characters from the
     * Hobbit book.
     */
    public void run(View v) {
        try {
            // Clear out the database.
            getOps().deleteByName(new String[] {
                    "Bilbo",
                    "Gandalf",
                    "Thorin", "Kili", "Fili",
                    "Balin", "Dwalin", "Oin", "Gloin",
                    "Dori", "Nori", "Ori",
                    "Bifur", "Bofur", "Bombur",
                    "Smaug",
                    "Beorn",
                    "Sauron",
                    "Necromancer"
                });

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

            // Update Beorn's race since he's a skinchanger.
            getOps().updateByName("Beorn",
                                  "Bear");
            // Insert another antagonist.
            Uri necromancerUri = 
                getOps().insert("Necromancer",
                                "Maia");

            // The Necromancer is really Sauron the Deceiver.
            getOps().updateByUri(necromancerUri,
                                 "Sauron",
                                 "Maia");

            // Delete dwarves who get killed in the Battle of Five
            // Armies.
            getOps().deleteByName(new String[] { 
                    "Thorin",
                    "Kili",
                    "Fili" 
                });

            // Delete Smaug since he gets killed by Bard the Bowman.
            getOps().deleteByRace(new String[] { "Dragon" });

            // Display the results.
            getOps().display(CharacterContract.CharacterEntry.COLUMN_RACE,
                             new String[] { 
                                 "Dwarf",
                                 "Maia",
                                 "Hobbit" 
                             });
        } catch (RemoteException e) {
            Log.d(TAG, 
                  "exception " 
                  + e);
        } finally {
            getOps().close();
        }
    }

    /**
     * Display the contents of the cursor as a ListView.
     */
    public void displayCursor(SimpleCursorAdapter adapter) {
    	// Display the designated columns in the cursor as a List in
        // the ListView.
        mListView.setAdapter(adapter);
    }
}
