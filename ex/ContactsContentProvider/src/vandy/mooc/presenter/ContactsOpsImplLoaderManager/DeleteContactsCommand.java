package vandy.mooc.presenter.ContactsOpsImplLoaderManager;

import java.util.Iterator;

import vandy.mooc.common.Command;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.content.ContentResolver;
import android.provider.ContactsContract;

/**
 * Delete all designated contacts in a background thread.
 */
public class DeleteContactsCommand
       implements GenericAsyncTaskOps<Iterator<String>, Void, Integer>,
                  Command<Iterator<String>> {
    /**
     * Store a reference to the ContactsOpsImpl object.
     */
    private ContactsOpsImpl mOps;

    /**
     * Store a reference to the Application context's ContentResolver.
     */
    private ContentResolver mContentResolver;

    /**
     * Constructor initializes the fields.
     */
    public DeleteContactsCommand(ContactsOpsImpl ops) {
        // Store the ContactOps and the ContentResolver from the
        // Application context.
        mOps = ops;
        mContentResolver =
            ops.getApplicationContext().getContentResolver();
    }

    /**
     * Run the command.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute (Iterator<String> contactsIter) {
        // Create a GenericAsyncTask to delete the contacts off the UI
        // Thread.
        final GenericAsyncTask<Iterator<String>,
                               Void,
                               Integer,
                               DeleteContactsCommand> asyncTask =
            new GenericAsyncTask<>(this);

        // Execute the GenericAsyncTask.
        asyncTask.execute(contactsIter);
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
        Utils.showToast(mOps.getActivityContext(),
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
        return mContentResolver.delete(ContactsContract.RawContacts.CONTENT_URI,
                                       ContactsContract.Contacts.DISPLAY_NAME
                                       + "=?",
                                       new String[] { name });
    }
}
