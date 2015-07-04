package vandy.mooc.presenter.ContactsOpsImplSimple;

import java.util.Iterator;

import vandy.mooc.common.Command;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.provider.ContactsContract;

/**
 * Modify some designated contacts in the UI thread.
 */
public class ModifyContactsCommand
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
    public ModifyContactsCommand(ContactsOpsImpl ops) {
        // Store the ContactOps, Iterator, and the ContentResolver
        // from the Application context.
        mOps = ops;
        mContentResolver =
            ops.getApplicationContext().getContentResolver();
    }

    /**
     * Run the command.
     */
    @Override
    public void execute (Iterator<String> contactsIter) {
        // Modify all the contacts named by the iterator.
        int totalContactsModified = modifyAllContacts(contactsIter);

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
