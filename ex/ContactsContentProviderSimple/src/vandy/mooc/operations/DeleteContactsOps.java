package vandy.mooc.operations;

import java.util.Iterator;

import vandy.mooc.utils.GenericAsyncTask;
import vandy.mooc.utils.GenericAsyncTaskOps;
import vandy.mooc.utils.Utils;
import android.provider.ContactsContract;

/**
 * Delete all designated contacts in a background thread.
 */
public class DeleteContactsOps
       implements GenericAsyncTaskOps<Iterator<String>, Void, Integer> {
    /**
     * Store a reference to the ContactsOps object.
     */
    private ContactsOps mOps;

    /**
     * The GenericAsyncTask used to insert contacts into the
     * ContactContentProvider.
     */
    private GenericAsyncTask<Iterator<String>, Void, Integer, DeleteContactsOps> mAsyncTask;

    /**
     * Constructor initializes the fields.
     */
    @SuppressWarnings("unchecked")
    public DeleteContactsOps(ContactsOps ops,
                             Iterator<String> contactsIter) {
        // Store the ContactOps.
        mOps = ops;

        // Create and execute a GenericAsyncTask to delete the
        // contacts off the UI Thread.
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute(contactsIter);
    }

    /**
     * Run in a background Thread to avoid blocking the UI Thread.
     */
    @Override
    public Integer doInBackground(Iterator<String> contactsIter) {
        // Delete all the contacts named by the iterator.
        return deleteAllContacts(contactsIter);
    }

    /**
     * Run in the UI Thread and displays a toast indicating how
     * many contacts were deleted.
     */
    @Override
    public void onPostExecute(Integer totalContactsDeleted,
                              Iterator<String> contactsIter) {
        Utils.showToast(mOps.getActivity(),
                        totalContactsDeleted 
                        + " contact(s) deleted");
    }


    /**
     * Synchronously delete all contacts designated by the Iterator.
     */
    private int deleteAllContacts(Iterator<String> contactsIter) {
        int totalContactsDeleted = 0;

        // Delete all the contacts named by the iterator.
        while (contactsIter.hasNext())
            totalContactsDeleted += deleteContact(contactsIter.next());

        return totalContactsDeleted;
    }

    /**
     * Delete the contact with the designated @a name.
     */
    private int deleteContact(String name) {
        return mOps.getActivity()
            .getApplicationContext()
            .getContentResolver()
            .delete(ContactsContract.RawContacts.CONTENT_URI,
                    ContactsContract.Contacts.DISPLAY_NAME + "=?",
                    new String[] { name });
    }
}
