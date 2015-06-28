package vandy.mooc.operations.ContactsOpsImplAsync;

import java.util.Iterator;

import vandy.mooc.common.AsyncCommand;
import vandy.mooc.common.MutableInt;
import vandy.mooc.operations.ContactsOpsImpl;
import android.content.ContentValues;
import android.provider.ContactsContract;

/**
 * Defines a command that asynchronously deletes from the
 * ContentResolver all the contacts listed in the Iterator parameter
 * passed to the constructor.  This class plays the role of the
 * Concrete Command in the Command pattern.
 */
public class ModifyAsyncCommand extends AsyncCommand {
    /**
     * Store a reference to the ContactsOps object.
     */
    final private ContactsOpsImpl mOps;

    /**
     * Iterator containing the contacts to delete.
     */
    final private Iterator<String> mContactsIter;

    /**
     * Keeps track of the number of contacts modified.
     */
    final private MutableInt mCounter;

    /**
     * Constructor stores the ContentResolver and Iterator that
     * contains all the contacts to delete.
     */
    public ModifyAsyncCommand (ContactsOpsImpl ops,
                               Iterator<String> contactsIter,
                               MutableInt counter) {
        // Set the ContentResolver from the Activity context.
        super(ops.getActivity().getContentResolver());

        // Store the ContactOps, iterator, and counter.
        mOps = ops;
        mContactsIter = contactsIter;
        mCounter = counter;
    }

    /**
     * Execute the command to asynchronously modify all the contacts
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
                             mCounter,
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
                                 Object counter,
                                 int result) {
        // Increment the count of modifications.
        ((MutableInt) counter).add(result);

        // Call the execute() method to trigger the update of next
        // contact (if any) in the Iterator.
        this.execute();
    }
}
                

