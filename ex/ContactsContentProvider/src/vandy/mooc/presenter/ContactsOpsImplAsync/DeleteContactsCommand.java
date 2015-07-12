package vandy.mooc.presenter.ContactsOpsImplAsync;

import vandy.mooc.common.AsyncProviderCommand;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.provider.ContactsContract;

/**
 * Defines a command that asynchronously deletes all the contacts
 * listed in the Iterator parameter from the Contacts ContentProvider.
 * This class plays the role of the Concrete Command in the Command
 * pattern.
 */
public class DeleteContactsCommand
       extends ContactsCommandBase {
    /**
     * Constructor initializes the fields.
     */
    protected DeleteContactsCommand(ContactsOpsImpl ops) {
        super(new DeleteAsyncCommand(ops));
    }

    /**
     * Define an AsyncCommand that deletes contacts from the Contacts
     * ContentProvider.
     */
    private static class DeleteAsyncCommand 
            extends AsyncProviderCommand<CommandArgs> {
        /**
         * Constructor initializes the fields.
         */
        public DeleteAsyncCommand(ContactsOpsImpl ops) {
            super(new CommandArgs(ops));
        }

        /**
         * Delete all the contacts from the Contacts Provider
         * asynchronously.
         */
        @Override
        public void execute() {
            getArgs().setCounter(0);
            executeImpl();
        }

        /**
         * Delete a contact from the Contacts Provider asynchronously.
         */
        private void executeImpl() {
            // If there are any contacts left to delete, initiate an
            // asynchronous deletion on the Contacts Provider.
            if (getArgs().getIterator().hasNext()) {
                getArgs().getAdapter()
                         .startDelete(this,
                                      0,
                                      ContactsContract.RawContacts.CONTENT_URI,
                                      ContactsContract.Contacts.DISPLAY_NAME + "=?",
                                      new String[] { getArgs().getIterator().next()});
            } else 
                // Otherwise, print a toast with summary info.
                Utils.showToast(getArgs().getOps().getActivityContext(),
                                getArgs().getCounter().getValue()
                                +" contact(s) deleted");
        }

        /**
         * This method is called back after the item has been deleted
         * from the Contacts Provider to perform the completion
         * operations.
         */
        @Override
        public void onCompletion(int token,
                                 int result) {
            // Increment the count of deletions.
            getArgs().getCounter().add(result);

            // Call the executeImpl() method to trigger the deletion
            // of next contact (if any) in the Iterator.
            executeImpl();
        }
    }
}
