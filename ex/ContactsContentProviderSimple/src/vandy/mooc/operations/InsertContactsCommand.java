package vandy.mooc.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vandy.mooc.utils.GenericAsyncTask;
import vandy.mooc.utils.GenericAsyncTaskOps;
import vandy.mooc.utils.Utils;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

/**
 * Insert all designated contacts in a background thread.
 */
public class InsertContactsCommand
       implements GenericAsyncTaskOps<Iterator<String>, Void, Integer> {
    /**
     * Store a reference to the ContactsOps object.
     */
    private ContactsOps mOps;

    /**
     * Iterator containing contacts to delete.
     */
    private Iterator<String> mContactsIter;
    
    /**
     * Store a reference to the Application context. 
     */
    private Context mApplicationContext;

    /**
     * The GenericAsyncTask used to insert contacts into the
     * ContactContentProvider.
     */
    private GenericAsyncTask<Iterator<String>, Void, Integer, InsertContactsCommand> mAsyncTask;

    /**
     * Constructor initializes the field.
     */
    public InsertContactsCommand(ContactsOps ops,
                                 Iterator<String> contactsIter) {
        // Store the ContactOps, Iterator, and Application context.
        mOps = ops;
        mContactsIter = contactsIter;
        mApplicationContext =
            ops.getActivity().getApplicationContext();

        // Create a GenericAsyncTask to insert the contacts off the UI
        // Thread.
        mAsyncTask = new GenericAsyncTask<>(this);
    }

    /**
     * Run the command.
     */
    @SuppressWarnings("unchecked")
    public void run() {
        // Execute the GenericAsyncTask.
        mAsyncTask.execute(mContactsIter);
    }

    /** 
     * Run in a background Thread to avoid blocking the UI Thread.
     */
    @Override
    public Integer doInBackground(Iterator<String> contactsIter) {
        // Insert all the contacts designated by the Iterator.
        return insertAllContacts(contactsIter);
    }

    /**
     * A count of the number of results in the query are displayed in
     * the UI Thread.
     */
    @Override
    public void onPostExecute(Integer count,
                              Iterator<String> contactsIter) {
        Utils.showToast(mOps.getActivity(),
                        count
                        + " contact(s) inserted");
    }

    /**
     * Synchronously insert all contacts designated by the Iterator.
     */
    private Integer insertAllContacts(Iterator<String> contactsIter) {
        // Set up a batch operation on ContactsContentProvider.
        final ArrayList<ContentProviderOperation> batchOperation =
            new ArrayList<ContentProviderOperation>();

        // Add each contact in the Iterator into the Contacts
        // ContentProvider.
        while (contactsIter.hasNext())
            addContact(contactsIter.next(),
                       batchOperation);

        try {
            // Apply all the batched operations synchronously.
            ContentProviderResult[] results =
                mApplicationContext
                .getContentResolver()
                .applyBatch(ContactsContract.AUTHORITY,
                            batchOperation);
            // Divide by 2 since each insert required two operations.
            return results.length / 2; 
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Synchronously insert a contact with the designated @name into
     * the ContactsContentProvider.  This code is explained at
     * http://developer.android.com/reference/android/provider/ContactsContract.RawContacts.html.
     */
    private void addContact(String name,
                            List<ContentProviderOperation> cpops) {
        final int position = cpops.size();

        // First part of operation.
        cpops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                  .withValue(RawContacts.ACCOUNT_TYPE,
                             mOps.getAccountType())
                  .withValue(RawContacts.ACCOUNT_NAME,
                             mOps.getAccountName())
                  .withValue(Contacts.STARRED,
                             1)
                  .build());

        // Second part of operation.
        cpops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                  .withValueBackReference(Data.RAW_CONTACT_ID,
                                          position)
                  .withValue(Data.MIMETYPE,
                             StructuredName.CONTENT_ITEM_TYPE)
                  .withValue(StructuredName.DISPLAY_NAME,
                             name)
                  .build());
    }
}

