package vandy.mooc.presenter.ContactsOpsImplAsync;

import java.util.Iterator;

import vandy.mooc.common.AsyncProviderCommand;
import vandy.mooc.common.Command;

/**
 * This superclass factors out common code and fields used by the
 * other *ContactCommand classes that handle insertions, deletions,
 * modifications, and queries.
 */
public class ContactsCommandBase
       implements Command<Iterator<String>> {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        ContactsCommandBase.class.getSimpleName();

    /**
     * The AsyncProviderCommand to execute.
     */
    protected final AsyncProviderCommand<CommandArgs> mAsyncProviderCommand;

    /**
     * Constructor initializes the field.
     */
    @SuppressWarnings("unchecked")
    protected ContactsCommandBase(AsyncProviderCommand<CommandArgs> asyncCommand) {
        // Set the AsyncProviderCommand to execute.
        mAsyncProviderCommand = asyncCommand;
    }

    /**
     * Asynchronously execute the AsyncProviderCommand on all contacts
     * in the @a contactsIter parameter.
     */
    @Override
    public void execute(Iterator<String> contactsIter) {
        // Set the iterator containing the contacts to operate upon in
        // the first AsyncProviderCommand in the array.
        mAsyncProviderCommand.getArgs()
            .setIterator(contactsIter);

        // Execute the AsyncProviderCommand on the Contacts Provider.
        mAsyncProviderCommand.execute();
    }
}
