package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ContactsOps;
import android.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * This Activity provides a facade for an application that shows how
 * to insert, query, modify, and delete contacts via the Android
 * ContactsContentProvider.  The user can optionally select various
 * implementation techniques to perform these operations, including
 * the Android AsyncQueryHandler and LoaderManager classes.  This
 * class plays the role of the "View" in the Model-View-Presenter
 * (MVP) pattern.  It extends GenericActivity that provides a
 * framework for automatically handling runtime configuration changes
 * of a ContactsOps object, which plays the role of the "Presenter" in
 * the MVP pattern.  The ContactsOps.View interface is used to
 * minimize dependencies between the View and Presenter layers.
 */
public class ContactsActivity
       extends GenericActivity<ContactsOps.View, ContactsOps>
       implements ContactsOps.View {
    /**
     * ListView displays the Contacts List.
     */
    private ListView mListView;

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
        // Set the layout of the MainActivity.
        setContentView(R.layout.contacts_activity);

        // Invoke the special onCreate() method in GenericActivity,
        // passing in the ContactsOps class to instantiate/manage and
        // "this" to provide ContactsOps with the ContactsOps.View
        // instance.
        super.onCreate(savedInstanceState, 
                       ContactsOps.class,
                       this);

        // Initialize the List View.
        mListView = (ListView) findViewById(R.id.list);

        // Connect the ListView with the SimpleCursorAdapter.
        mListView.setAdapter(getOps().makeCursorAdapter());
    }

    /**
     * This click handler method inserts contacts into the
     * ContactsContentProvider.
     */
    public void insertContacts(View v) {
        // Insert contacts.
        getOps().insertContacts();
    }

    /**
     * This click handler method modifies contacts in the
     * ContactsContentProvider.
     */
    public void modifyContacts(View v) {
        // Modify contacts.
        getOps().modifyContacts();
    }

    /**
     * This click handler method deletes contacts from the
     * ContactsContentProvider.
     */
    public void deleteContacts(View v) {
        // Delete contacts.
        getOps().deleteContacts();
    }

    /**
     * Called by Android framework when menu option is clicked.
     * 
     * @param item
     * @return true
     */
    public boolean chooseOpsOption(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.simpleImpl:
            getOps().setContactsOpsImplType
                    (ContactsOps.ContactsOpsImplType.SIMPLE);
            Utils.showToast(this,
                            "ContactsOpsImplSimple selected");
            break;

        case R.id.asyncImpl:
            getOps().setContactsOpsImplType
                (ContactsOps.ContactsOpsImplType.ASYNC);
            Utils.showToast(this,
                            "ContactsOpsImplAsync selected");
            break;

        case R.id.loaderManagerImpl:
            getOps().setContactsOpsImplType
                (ContactsOps.ContactsOpsImplType.LOADER_MANAGER);
            Utils.showToast(this,
                            "ContactsOpsImplLoaderManager selected");
            break;
        }

        // The calls to setContactsOpsImplType() above will set the
        // new implementation type and construct a new instance of
        // that implementation.  These changes require initializing
        // the implementation WeakReference to this Activity, which
        // can be accomplished by generating a "fake" configuration
        // change event.  Moreover, since the ContactOps
        // implementation was just constructed and is not being
        // restored, we need to pass in true for the "firstTimeIn" in
        // parameter.
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
     * Return the LoaderManager.
     */
    @Override
    public LoaderManager getLoaderManager() {
        return super.getLoaderManager();
    }
}
