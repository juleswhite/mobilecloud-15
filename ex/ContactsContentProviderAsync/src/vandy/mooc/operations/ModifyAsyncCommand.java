package vandy.mooc.operations;

import java.util.Iterator;

import vandy.mooc.common.AsyncCommand;
import android.content.ContentValues;
import android.provider.ContactsContract;

/**
 * @classs ModifyAsyncCommmand
 *
 * @brief Defines a command that asynchronously deletes from the
 *        ContentResolver all the contacts listed in the Iterator
 *        parameter passed to the constructor.  This class plays the
 *        role of the Concrete Command in the Command pattern.
 */
public class ModifyAsyncCommand extends AsyncCommand {
    /**
     * Store a reference to the ContactsOps object.
     */
    final private ContactsOps mOps;

    /**
     * Iterator containing the contacts to delete.
     */
    final private Iterator<String> mContactsIter;

    /**
     * Constructor stores the ContentResolver and Iterator that
     * contains all the contacts to delete.
     */
    public ModifyAsyncCommand (ContactsOps ops,
                               Iterator<String> contactsIter) {
        super(ops.getActivity().getContentResolver());
        mOps = ops;
        mContactsIter = contactsIter;
    }

    /**
     * Execute the command to asynchronously delete all the contacts
     * in the Iterator passed to the constructor.
     */
    public void execute() {
        // If there are any contacts left to modify, initiate an
        // asynchronous update on the Contacts Provider.
        if (mContactsIter.hasNext()) {
            final String originalName = mContactsIter.next();
            final String updatedName = mContactsIter.next();
            final ContentValues cvs = new ContentValues();
            cvs.put(ContactsContract.Contacts.DISPLAY_NAME,
                    updatedName);

            this.startUpdate(0,
                             null,
                             ContactsContract.RawContacts.CONTENT_URI,
                             cvs,
                             ContactsContract.Contacts.DISPLAY_NAME + "=?",
                             new String[] { originalName });
        } else {
            // Otherwise, execute the next AsyncCommand (if any) in
            // the Iterator.
            super.executeNext();
        }
    }

    /**
     * This method is called back by Android after the item has been
     * updated from the Contacts Provider to perform the completion
     * task.
     */
    @Override
    public void onUpdateComplete(int token,
                                 Object cookie,
                                 int result) {
        // Increment the count of modifications.
        mOps.mCounter += result;

        // Call the execute() method to trigger the update of next
        // contact (if any) in the Iterator.
        this.execute();
    }
}
                

