package vandy.mooc.presenter.ContactsOpsImplSimple;

import java.util.Iterator;

import vandy.mooc.common.Command;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.content.ContentResolver;
import android.provider.ContactsContract;

/**
 * Delete all designated contacts in the UI thread.
 */
public class DeleteContactsCommand
       implements Command<Iterator<String>> {
    /**
     * Store a reference to the ContactsOps object.
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
    @Override
    public void execute (Iterator<String> contactsIter) {
        int totalContactsDeleted =
            deleteAllContacts(contactsIter);
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
