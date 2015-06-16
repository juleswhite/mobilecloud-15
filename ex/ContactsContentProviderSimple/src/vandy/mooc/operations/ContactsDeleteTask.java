package vandy.mooc.operations;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import vandy.mooc.activities.ContactsActivity;
import vandy.mooc.utils.Utils;
import android.os.AsyncTask;
import android.provider.ContactsContract;

/**
 * Delete all designated contacts in a background thread.
 */
public class ContactsDeleteTask 
       extends AsyncTask<Iterator<String>, Void, Integer> {
    /**
     * Store a reference to the ContactsOps object.
     */
    private ContactsOps mOps;

    /**
     * Constructor initializes the field.
     */
    public ContactsDeleteTask(ContactsOps ops) {
        mOps = ops;
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
     * Run in a background Thread to avoid blocking the UI Thread.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Integer doInBackground(Iterator<String>... contactsIter) {
        // Delete all the contacts named by the iterator.
        return deleteAllContacts(contactsIter[0]);
    }

    /**
     * Run in the UI Thread and displays a toast indicating how
     * many contacts were deleted.
     */
    @Override
    public void onPostExecute(Integer totalContactsDeleted) {
        Utils.showToast(mOps.getActivity(),
                        totalContactsDeleted 
                        + " contact(s) deleted");
    }
}
