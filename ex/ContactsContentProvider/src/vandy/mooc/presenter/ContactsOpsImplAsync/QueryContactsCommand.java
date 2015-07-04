package vandy.mooc.presenter.ContactsOpsImplAsync;

import vandy.mooc.common.AsyncProviderCommand;
import vandy.mooc.presenter.ContactsOpsImpl;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

/**
 * Defines a command that asynchronously queries the ContentResolver
 * for all the starred contacts and displays the results.  This class
 * plays the role of the Concrete Command in the Command pattern.
 */
public class QueryContactsCommand 
       extends ContactsCommandBase {
   /**
     * Constructor stores the ContentResolver and ListActivity.
     */
    public QueryContactsCommand(ContactsOpsImpl ops) {
        super(new QueryAsyncCommand(ops));
    }

    /**
     * Define an AsyncCommand that modifies contacts from the Contacts
     * ContentProvider.
     */
    private static class QueryAsyncCommand
            extends AsyncProviderCommand<CommandArgs> {
        /**
         * Constructor initializes the fields.
         */
        public QueryAsyncCommand(ContactsOpsImpl ops) {
            super(new CommandArgs(ops));
        }

        /**
         * Asynchronously query the contacts based on selection
         * criteria.
         */
        @Override
        public void execute() {
            // Columns to query.
            final String columnsToQuery[] = 
                new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.STARRED 
            };

            // Contacts to select.
            final String selection = 
                "((" 
                + Contacts.DISPLAY_NAME 
                + " NOTNULL) AND ("
                + Contacts.DISPLAY_NAME 
                + " != '' ) AND (" 
                + Contacts.STARRED
                + "== 1))";

            // Initiate an asynchronous query.
            getArgs().getAdapter()
                     .startQuery(this,
                                 0,
                                 ContactsContract.Contacts.CONTENT_URI, 
                                 columnsToQuery, 
                                 selection,
                                 // ContactsContract.Contacts.STARRED /* + "= 0" */,
                                 null, 
                                 ContactsContract.Contacts._ID
                                 + " ASC");        
        }

        /**
         * This method is called back after the query on the Contacts
         * Provider finishes to perform the completion operations.
         */
        @Override
        public void onCompletion(int token,
                                 Cursor cursor) {
            // Display the results if there are any.
            if (cursor != null
                && cursor.getCount() != 0) 
                getArgs().getOps().displayCursor(cursor);
        }
    }
}

                
                
