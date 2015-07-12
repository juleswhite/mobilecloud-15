package vandy.mooc.presenter.ContactsOpsImplLoaderManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vandy.mooc.common.Command;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.common.Utils;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
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
       implements GenericAsyncTaskOps<Iterator<String>, Void, Integer>,
                  Command<Iterator<String>> {
    /**
     * Store a reference to the ContactsOpsImpl object.
     */
    private ContactsOpsImpl mOps;

    /**
     * Store a reference to the Application context's ContentResolver.
     */
    private ContentResolver mContentResolver;

    /**
     * Constructor initializes the field.
     */
    public InsertContactsCommand(ContactsOpsImpl ops) {
        // Store the ContactOps and the ContentResolver from the
        // Application context.
        mOps = ops;
        mContentResolver =
            ops.getApplicationContext().getContentResolver();
    }

    /**
     * Run the command.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(Iterator<String> contactsIter) {
        // Create a GenericAsyncTask to insert the contacts off the UI
        // Thread.
        final GenericAsyncTask<Iterator<String>,
                               Void,
                               Integer,
                               InsertContactsCommand> asyncTask =
            new GenericAsyncTask<>(this);

        // Execute the GenericAsyncTask.
        asyncTask.execute(contactsIter);
    }

    /** 
     * Run in a background Thread to avoid blocking the UI Thread.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Integer doInBackground(Iterator<String>... contactsIter) {
        // Insert all the contacts designated by the Iterator.
        return insertAllContacts(contactsIter[0]);
    }

    /**
     * A count of the number of results in the query are displayed in
     * the UI Thread.
     */
    @Override
    public void onPostExecute(Integer count) {
        Utils.showToast(mOps.getActivityContext(),
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
                mContentResolver.applyBatch(ContactsContract.AUTHORITY,
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

