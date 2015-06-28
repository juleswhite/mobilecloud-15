package vandy.mooc.operations.ContactsOpsImplAsync;

import java.util.Iterator;

import vandy.mooc.common.AsyncCommand;
import vandy.mooc.common.MutableInt;
import vandy.mooc.operations.ContactsOpsImpl;
import android.provider.ContactsContract;

/**
 * Defines a command that asynchronously deletes from the
 * ContentResolver all the contacts listed in the Iterator parameter
 * passed to the constructor.  This class plays the role of the
 * Concrete Command in the Command pattern.
 */
public class DeleteAsyncCommand extends AsyncCommand {
    /**
     * Store a reference to the ContactsOps object.
     */
    final private ContactsOpsImpl mOps;

    /**
     * Iterator containing the contacts to delete.
     */
    final private Iterator<String> mContactsIter;

    /**
     * Keeps track of the number of contacts deleted.
     */
    final private MutableInt mCounter;

    /**
     * Constructor stores the ContentResolver and Iterator that
     * contains all the contacts to delete.
     */
    public DeleteAsyncCommand (ContactsOpsImpl ops,
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
     * Execute the command to asynchronously delete all the contacts
     * in the Iterator passed to the constructor.
     */
    public void execute() {
        // If there are any contacts left to delete, initiate an
        // asynchronous deletion on the Contacts Provider.  
        if (mContactsIter.hasNext()) {
            this.startDelete(0, 
                             mCounter,
                             ContactsContract.RawContacts.CONTENT_URI,
                             ContactsContract.Contacts.DISPLAY_NAME + "=?",
                             new String[] {mContactsIter.next()});
        } else {
            // Otherwise, execute the next AsyncCommand (if any) in
            // the Iterator.
            super.executeNext();
        }
    }

    /**
     * This method is called back by Android after the item has been
     * deleted from the Contacts Provider to perform the completion
     * task.
     */
    @Override
    public void onDeleteComplete(int token,
                                 Object counter,
                                 int result) {
        // Increment the count of deletions.
        ((MutableInt) counter).add(result);

        // Call the execute() method to trigger the deletion of next
        // contact (if any) in the Iterator.
        this.execute();
    }
}
                
