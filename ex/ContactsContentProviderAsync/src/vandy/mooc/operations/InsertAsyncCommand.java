package vandy.mooc.operations;

import java.util.Iterator;

import vandy.mooc.common.AsyncCommand;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

/**
 * @classs InsertAsyncCommmand
 *
 * @brief Defines a command that asynchronously inserts into the
 *        ContentResolver all the contacts listed in the Iterator
 *        parameter passed to the constructor.  This class plays the
 *        role of the Concrete Command in the Command pattern.
 *
 *        Each contact requires two (asynchronous) insertions into the
 *        Contacts Provider.  The first insert puts the RawContact
 *        into the Contacts Provider and the second insert puts the
 *        data associated with the RawContact into the Contacts
 *        Provider.
 */
public class InsertAsyncCommand extends AsyncCommand {
    /**
     * Store a reference to the ContactsOps object.
     */
    final private ContactsOps mOps;

    /**
     * Iterator containing the contacts to insert.
     */
    final private Iterator<String> mContactsIter;

    /**
     * Token that indicates the type of operation.
     */
    final static int RAW_CONTACT = 1;
    final static int RAW_CONTACT_DATA = 2;

    /**
     * Constructor stores the ContentResolver, Iterator that contains
     * all the contacts to delete, and the account type/name.
     */
    public InsertAsyncCommand (ContactsOps ops,
                               Iterator<String> contactsIter) {
        super(ops.getActivity().getContentResolver());
        mOps = ops;
        mContactsIter = contactsIter;
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

    /**
     * Execute the command to asynchronously insert all the contacts
     * in the Iterator passed to the constructor.
     */
    public void execute() {
        if (mContactsIter.hasNext()) {
            // If there are any contacts left to insert, make a
            // ContentValues object containing the RawContact portion
            // of the contact and initiate an asynchronous insert on
            // the Contacts Provider.
            ContentValues values = makeRawContact(1);
            this.startInsert(RAW_CONTACT,
                             null,
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
                                 Object cookie,
                                 Uri uri) {
        if (token == RAW_CONTACT) {
            // If the token is RAW_CONTACT then make a ContentValues
            // object containing the data associated with RawContact.
            ContentValues values =
                makeRawContactData(mContactsIter.next(),
                                   uri);

            // Initiate an asynchronous insert on the Contacts
            // Provider.
            this.startInsert(RAW_CONTACT_DATA,
                             null,
                             Data.CONTENT_URI,
                             values);
        } else if (token == RAW_CONTACT_DATA) {
            // Increment the count of insertions.
            mOps.mCounter++;

            // Calls execute() to trigger insertion of the next
            // contact (if any) in the Iterator.
            this.execute();
        }
    }
}

