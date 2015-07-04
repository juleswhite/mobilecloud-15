package vandy.mooc.presenter.ContactsOpsImplLoaderManager;

import java.util.Iterator;

import vandy.mooc.common.Command;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.ContactsContract;

/**
 * Modify some designated contacts in a background task.
 */
public class ModifyContactsCommand
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
    public ModifyContactsCommand(ContactsOpsImpl ops) {
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
                               ModifyContactsCommand> asyncTask =
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
        // Modify all the contacts named by the iterator.
        return modifyAllContacts(contactsIter[0]);
    }

    /**
     * Run in the UI Thread and displays a toast indicating how
     * many contacts were deleted.
     */
    @Override
    public void onPostExecute(Integer totalContactsModified) {
        Utils.showToast(mOps.getActivityContext(),
                        totalContactsModified
                        + " contact(s) modified");
    }

    /**
     * Synchronously modify all contacts designated by the Iterator.
     */
    private int modifyAllContacts(Iterator<String> contactsIter) {
        int totalContactsModified = 0;

        // Modify all the contacts named by the iterator.
        while (contactsIter.hasNext())
            totalContactsModified += modifyContact(contactsIter.next(),
                                                   contactsIter.next());

        return totalContactsModified;
    }

    /**
     * Modify the contact with the designated @a name.
     */
    private int modifyContact(String originalName,
                              String updatedName) {
        final ContentValues cvs = new ContentValues();
        cvs.put(ContactsContract.Contacts.DISPLAY_NAME,
                updatedName);
        return mContentResolver.update(ContactsContract.RawContacts.CONTENT_URI,
                                       cvs,
                                       ContactsContract.Contacts.DISPLAY_NAME
                                       + "=?",
                                       new String[] { originalName });
    }
}
