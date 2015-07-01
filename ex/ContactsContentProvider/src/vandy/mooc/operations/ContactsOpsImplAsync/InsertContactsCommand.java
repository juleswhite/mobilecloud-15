package vandy.mooc.operations.ContactsOpsImplAsync;

import java.util.Iterator;

import vandy.mooc.common.AsyncCommand;
import vandy.mooc.common.Command;
import vandy.mooc.common.MutableInt;
import vandy.mooc.operations.ContactsOpsImpl;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

/**
 * Defines a command that asynchronously inserts into the
 * ContentResolver all the contacts listed in the Iterator parameter
 * passed to the constructor.  This class plays the role of the
 * Concrete Command in the Command pattern.
 */
public class InsertContactsCommand
       implements Command<Iterator<String>> {
    /**
     * Store a reference to the ContactsOps object.
     */
    final private ContactsOpsImpl mOps;

    /**
     * Iterator containing the contacts to insert.
     */
    private Iterator<String> mContactsIter;

    /**
     * Token that indicates the type of operation.
     */
    final static int INSERT_RAW_CONTACT = 1;
    final static int INSERT_RAW_CONTACT_DATA = 2;

    /**
     * Constructor stores the ContentResolver, Iterator that contains
     * all the contacts to delete, and the account type/name.
     */
    public InsertContactsCommand(ContactsOpsImpl ops) {
         // Store the ContactOps.
        mOps = ops;
    }

    /**
     * Execute the command to asynchronously insert all the contacts
     * in the @a contactsIterator.  Each contact requires two
     * (asynchronous) insertions into the Contacts Provider.  The
     * first insert puts the RawContact into the Contacts Provider and
     * the second insert puts the data associated with the RawContact
     * into the Contacts Provider.
     */
    @Override
    public void execute(Iterator<String> contactsIter) {
        final AsyncCommandArgs asyncCommandArgs =
            new AsyncCommandArgs(contactsIter);

        // Start executing AsyncCommands to insert the contacts into
        // the Contacts Provider and display the number of contacts
        // inserted.
        AsyncCommand.executeAsyncCommands
            (new AsyncCommand[] {
                new AsyncCommand(mOps.getActivity().getContentResolver()) {
                    @Override
                    public void execute() {
                        if (asyncCommandArgs.mContactsIter.hasNext()) {
                            // If there are any contacts left to insert, make a
                            // ContentValues object containing the RawContact portion
                            // of the contact and initiate an asynchronous insert on
                            // the Contacts ContentProvider.
                            final ContentValues values = makeRawContact(1);
                            startInsert(INSERT_RAW_CONTACT,
                                        asyncCommandArgs,
                                        RawContacts.CONTENT_URI,
                                        values);
                        } else
                            // Otherwise, execute the next AsyncCommand (if any) in
                            // the Iterator.
                            super.executeNext();
                    }

                    /**
                     * This method is called back by Android after the item has been
                     * inserted into the Contacts Provider to perform the completion
                     * task(s).
                     */
                    @Override
                    public void onInsertComplete(int token,
                                                 Object asyncCommandArgs,
                                                 Uri uri) {
                        if (token == INSERT_RAW_CONTACT) {
                            // If the token is INSERT_RAW_CONTACT then
                            // make a ContentValues object containing
                            // the data associated with RawContact.
                            final ContentValues values =
                                makeRawContactData
                                (((AsyncCommandArgs) asyncCommandArgs).mContactsIter.next(),
                                 uri);

                            // Initiate an asynchronous insert on the Contacts
                            // Provider.
                            this.startInsert(INSERT_RAW_CONTACT_DATA,
                                             asyncCommandArgs,
                                             Data.CONTENT_URI,
                                             values);
                        } else if (token == INSERT_RAW_CONTACT_DATA) {
                            // Increment the insertion count.
                            ((AsyncCommandArgs) asyncCommandArgs).mCounter.increment();

                            // Calls execute() to trigger insertion of
                            // the next contact (if any) in the
                            // Iterator.
                            execute();
                        }
                    }

                    /**
                     * Factory method that creates a ContentValues containing the
                     * RawContact associated with the account type/name.
                     */
                    private ContentValues makeRawContact(int starred) {
                        ContentValues values = new ContentValues();
                        values.put(RawContacts.ACCOUNT_TYPE,
                                   mOps.getAccountType());
                        values.put(RawContacts.ACCOUNT_NAME,
                                   mOps.getAccountName());
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
                },
                // Print a toast after all the contacts are inserted.
                mOps.makeToastAsyncCommand(" contact(s) inserted",
                                           asyncCommandArgs.mCounter)
            });
    }
}

