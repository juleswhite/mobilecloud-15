package vandy.mooc.presenter.ContactsOpsImplAsync;

import vandy.mooc.common.AsyncProviderCommand;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

/**
 * Defines a command that asynchronously inserts all the contacts
 * listed in the Iterator parameter into the Contacts ContentProvider.
 * This class plays the role of the Concrete Command in the Command
 * pattern.
 */
public class InsertContactsCommand
       extends ContactsCommandBase {
    /**
     * Constructor forwards to the superclass to initialize its
     * fields.
     */
    public InsertContactsCommand(ContactsOpsImpl ops) {
        super(new InsertAsyncCommand(ops));
    }

    /**
     * Define an AsyncCommand that inserts contacts into the Contacts
     * ContentProvider.
     */
    private static class InsertAsyncCommand
            extends AsyncProviderCommand<CommandArgs> {
        /**
         * Token that indicates the type of operation.
         */
        final static int INSERT_RAW_CONTACT = 1;
        final static int INSERT_RAW_CONTACT_DATA = 2;

        /**
         * Constructor initializes the fields.
         */
        public InsertAsyncCommand(ContactsOpsImpl ops) {
            super(new CommandArgs(ops));
        }

        /**
         * Insert all the contacts in the Contacts Provider
         * asynchronously.
         */
        @Override
        public void execute() {
            getArgs().setCounter(0);
            executeImpl();
        }

        /**
         * Each contact requires two (asynchronous) insertions into
         * the Contacts Provider.  The first insert puts the
         * RawContact into the Contacts Provider and the second insert
         * puts the data associated with the RawContact into the
         * Contacts Provider.
         */
        public void executeImpl() {
            if (getArgs().getIterator().hasNext()) {
                // If there are any contacts left to insert, make a
                // ContentValues object containing the RawContact
                // portion of the contact and initiate an asynchronous
                // insert on the Contacts ContentProvider.
                final ContentValues values = makeRawContact(1);
                getArgs().getAdapter()
                         .startInsert(this,
                                      INSERT_RAW_CONTACT,
                                      RawContacts.CONTENT_URI,
                                      values);
            } else
                // Otherwise, print a toast with summary info.
                Utils.showToast(getArgs().getOps().getActivityContext(),
                                getArgs().getCounter().getValue()
                                +" contact(s) inserted");
        }

        /**
         * This method is called back by Android after the item has
         * been inserted into the Contacts Provider to perform the
         * completion task(s).
         */
        @Override
        public void onCompletion(int token,
                                 Uri uri) {
            if (token == INSERT_RAW_CONTACT) {
                // If the token is INSERT_RAW_CONTACT then
                // make a ContentValues object containing
                // the data associated with RawContact.
                final ContentValues values =
                    makeRawContactData
                        (getArgs().getIterator().next(),
                         uri);

                // Initiate an asynchronous insert on the Contacts
                // Provider.
                getArgs().getAdapter()
                         .startInsert(this,
                                      INSERT_RAW_CONTACT_DATA,
                                      Data.CONTENT_URI,
                                      values);
            } else if (token == INSERT_RAW_CONTACT_DATA) {
                // Increment the insertion count.
                getArgs().getCounter().increment();

                // Calls executeImpl() to trigger insertion of the next
                // contact (if any) in the Iterator.
                executeImpl();
            }
        }

        /**
         * Factory method that creates a ContentValues containing the
         * RawContact associated with the account type/name.
         */
        private ContentValues makeRawContact(int starred) {
            ContentValues values = new ContentValues();
            values.put(RawContacts.ACCOUNT_TYPE,
                       getArgs().getOps().getAccountType());
            values.put(RawContacts.ACCOUNT_NAME,
                       getArgs().getOps().getAccountName());
            values.put(Contacts.STARRED,
                       starred);
            return values;
        }
        
        /**
         * Factory method that creates a ContentValues containing the data
         * associated with a RawContact.
         */
        private ContentValues makeRawContactData(String displayName,
                                                 Uri rawContactUri) {
            ContentValues values = new ContentValues();
            values.put(Data.RAW_CONTACT_ID,
                       ContentUris.parseId(rawContactUri));
            values.put(Data.MIMETYPE,
                       StructuredName.CONTENT_ITEM_TYPE);
            values.put(StructuredName.DISPLAY_NAME,
                       displayName);
            return values;
        }
    }
}

