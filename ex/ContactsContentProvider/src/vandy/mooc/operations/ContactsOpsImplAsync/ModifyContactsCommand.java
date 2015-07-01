package vandy.mooc.operations.ContactsOpsImplAsync;

import java.util.Iterator;

import vandy.mooc.common.AsyncCommand;
import vandy.mooc.common.Command;
import vandy.mooc.operations.ContactsOpsImpl;
import android.content.ContentValues;
import android.provider.ContactsContract;

/**
 * Defines a command that asynchronously deletes from the
 * ContentResolver all the contacts listed in the Iterator parameter
 * passed to the constructor.  This class plays the role of the
 * Concrete Command in the Command pattern.
 */
public class ModifyContactsCommand
       implements Command<Iterator<String>> {
    /**
     * Store a reference to the ContactsOps object.
     */
    final private ContactsOpsImpl mOps;

    /**
     * Constructor stores the ContentResolver and Iterator that
     * contains all the contacts to delete.
     */
    public ModifyContactsCommand(ContactsOpsImpl ops) {
        // Store the ContactOps.
        mOps = ops;
    }

    /**
     * Execute the command to asynchronously modify all the contacts
     * in the @a contactsIter.
     */
    @Override
    public void execute(Iterator<String> contactsIter) {
        final AsyncCommandArgs asyncCommandArgs =
            new AsyncCommandArgs(contactsIter);

        // Start executing AsyncCommands to modify the contacts from
        // the Contacts Provider and display the number of contacts
        // modified.
        AsyncCommand.executeAsyncCommands
            (new AsyncCommand[] {
                new AsyncCommand(mOps.getActivity().getContentResolver()) {
                    @Override
                    public void execute() {
                        // If there are any contacts left to modify,
                        // initiate an asynchronous update on the
                        // Contacts Provider.
                        if (asyncCommandArgs.mContactsIter.hasNext()) {
                            final String originalName = 
                                asyncCommandArgs.mContactsIter.next();
                            final String updatedName = 
                                asyncCommandArgs.mContactsIter.next();
                            final ContentValues cvs = new ContentValues();
                            cvs.put(ContactsContract.Contacts.DISPLAY_NAME,
                                    updatedName);

                            this.startUpdate(0,
                                             asyncCommandArgs,
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
                                                 Object asyncCommandArgs,
                                                 int result) {
                        // Increment the count of modifications.
                        ((AsyncCommandArgs) asyncCommandArgs).mCounter.add(result);

                        // Call the execute() method to trigger the update of next
                        // contact (if any) in the Iterator.
                        this.execute();
                    }
                },
                // Print a toast after all the contacts are deleted.
                mOps.makeToastAsyncCommand(" contact(s) modified",
                                           asyncCommandArgs.mCounter)
            });
    }

}
