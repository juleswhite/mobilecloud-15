package vandy.mooc.presenter;

import vandy.mooc.common.ConfigurableOps;
import vandy.mooc.common.ContextView;
import vandy.mooc.presenter.ContactsOpsImplAsync.ContactsOpsImplAsync;
import vandy.mooc.presenter.ContactsOpsImplLoaderManager.ContactsOpsImplLoaderManager;
import vandy.mooc.presenter.ContactsOpsImplSimple.ContactsOpsImplSimple;
import android.app.LoaderManager;
import android.widget.SimpleCursorAdapter;

/**
 * Class that defines an interface for inserting, querying, modifying,
 * and deleting contacts from the Android ContactsContentProvider.  It
 * implements ConfigurableOps so it can be managed by the
 * GenericActivity framework.  This class plays the role of the
 * "Abstraction" in the Bridge pattern.  These classes in this
 * directory also play the role of the "Presenter" in the
 * Model-View-Presenter pattern.
 */
public class ContactsOps implements ConfigurableOps<ContactsOps.View> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        ContactsOps.class.getSimpleName();

    /**
     * This interface defines the minimum interface needed by the
     * ContactsOps class in the "Presenter" layer to interact with the
     * ContactsActivity in the "View" layer.
     */
    public interface View extends ContextView {
        /**
         * Get the LoaderManager associated with the View.
         */
        LoaderManager getLoaderManager();
    }

    /**
     * Defines the type of the ContactsOpsImpl (i.e., SIMPLE, ASYNC,
     * or LOADER_MANAGER).
     */

    public enum ContactsOpsImplType {
        SIMPLE,        // Use a "simple" implementation
        ASYNC,         // Use an AsyncQueryHandler implementation
        LOADER_MANAGER // Use a LoaderManager (and AsyncTask) implementations
    }

    /**
     * Stores the type of the ContactsOpsImpl (i.e., SIMPLE, ASYNC, or
     * LOADER_MANAGER).
     */
    private ContactsOpsImplType mImplType;

    /**
     * The root of the Implementor hierarchy.
     */
    private ContactsOpsImpl mImpl;

    /**
     * This default constructor must be public for the GenericOps
     * class to work properly.
     */
    public ContactsOps() {
        setContactsOpsImplType(ContactsOpsImplType.SIMPLE);
    }

    /**
     * Sets the type of the ContactsOpsImpl (i.e., SIMPLE, ASYNC, or
     * LOADER_MANAGER).
     */
    public void setContactsOpsImplType(ContactsOpsImplType implType) {
        // Set and construct the appropriate type of ContactsOpsImpl.
        if (mImplType != implType) {
            mImplType = implType;
            switch(mImplType) {
                case SIMPLE:
                    mImpl = new ContactsOpsImplSimple();
                    break;
                case ASYNC:
                    mImpl = new ContactsOpsImplAsync();
                    break;
                case LOADER_MANAGER:
                    mImpl = new ContactsOpsImplLoaderManager();
                    break;
            }
        }
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the ContactsOps object after it's been created.
     *
     * @param instance     The currently active ContactsOps.View.
     * @param firstTimeIn  Set to "true" if this is the first time the
     *                     Ops class is initialized, else set to
     *                     "false" if called after a runtime
     *                     configuration change.
     */
    public void onConfiguration(ContactsOps.View view,
                                boolean firstTimeIn) {
        mImpl.onConfiguration(view,
                              firstTimeIn);
    }

    /**
     * Factory method that returns the SimpleCursorAdapter.
     */ 
    public SimpleCursorAdapter makeCursorAdapter() {
        return mImpl.makeCursorAdapter();
    }

    /**
     * Insert the contacts.
     */
    public void insertContacts() {
        mImpl.insertContacts();
    }

    /**
     * Modify the contacts.
     */
    public void modifyContacts() {
        mImpl.modifyContacts();
    }

    /**
     * Delete the contacts.
     */
    public void deleteContacts() {
        mImpl.deleteContacts();
    }
}
