package vandy.mooc.presenter.ContactsOpsImplAsync;

import vandy.mooc.common.AsyncProviderCommand;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.content.ContentValues;
import android.provider.ContactsContract;

/**
 * Defines a command that asynchronously modifies all the contacts
 * listed in the Iterator parameter in the Contacts ContentProvider.
 * This class plays the role of the Concrete Command in the Command
 * pattern.
 */
public class ModifyContactsCommand
       extends ContactsCommandBase {
    /**
     * Constructor initializes the fields.
     */
    public ModifyContactsCommand(ContactsOpsImpl ops) {
        super(new ModifyAsyncCommand(ops));
    }

    /**
     * Define an AsyncCommand that modifies contacts from the Contacts
     * ContentProvider.
     */
    private static class ModifyAsyncCommand
            extends AsyncProviderCommand<CommandArgs> {
        /**
         * Constructor initializes the fields.
         */
        public ModifyAsyncCommand(ContactsOpsImpl ops) {
            super(new CommandArgs(ops));
        }

        /**
         * Modify all the contacts in the Contacts Provider
         * asynchronously.
         */
        @Override
        public void execute() {
            getArgs().setCounter(0);
            executeImpl();
        }

        /**
         * Modify a contact in the Contacts Provider asynchronously.
         */
        public void executeImpl() {
            // If there are any contacts left to modify, initiate an
            // asynchronous update on the Contacts Provider.
            if (getArgs().getIterator().hasNext()) {
                final String originalName = 
                    getArgs().getIterator().next();
                final String updatedName = 
                    getArgs().getIterator().next();
                final ContentValues cvs = new ContentValues();
                cvs.put(ContactsContract.Contacts.DISPLAY_NAME,
                        updatedName);

                getArgs().getAdapter()
                         .startUpdate(this,
                                      0,
                                      ContactsContract.RawContacts.CONTENT_URI,
                                      cvs,
                                      ContactsContract.Contacts.DISPLAY_NAME + "=?",
                                      new String[] { originalName });
            } else 
                // Otherwise, print a toast with summary info.
                Utils.showToast(getArgs().getOps().getActivityContext(),
                                getArgs().getCounter().getValue()
                                +" contact(s) modified");
        }

        /**
         * This method is called back after the item has been updated
         * from the Contacts Provider to perform the completion
         * operation.
         */
        @Override
        public void onCompletion(int token,
                                 int result) {
            // Increment the count of modifications.
            getArgs().getCounter().add(result);

            // Call the executeImpl() method to trigger the update of
            // next contact (if any) in the Iterator.
            executeImpl();
        }
    }
}
