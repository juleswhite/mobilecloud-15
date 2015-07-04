package vandy.mooc.presenter.ContactsOpsImplAsync;

import java.util.Iterator;

import vandy.mooc.common.AsyncProviderCommandAdapter;
import vandy.mooc.common.MutableInt;
import vandy.mooc.presenter.ContactsOpsImpl;

/**
 * Holds the arguments passed to an AsyncCommand.
 */
public class CommandArgs {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG =
        CommandArgs.class.getSimpleName();

    /**
     * Store a reference to the ContactsOps object.
     */
    final private ContactsOpsImpl mOps;

    /**
     * Iterator containing the contacts to delete.
     */
    private Iterator<String> mContactsIter;

    /**
     * Keeps track of the number of contacts deleted.
     */
    final private MutableInt mCounter;

    /**
     * Store a reference to the AsyncProviderCommandAdapter.
     */
    final AsyncProviderCommandAdapter<CommandArgs> mAdapter;

    /**
     * Constructor initializes the fields.
     */ 
    public CommandArgs(ContactsOpsImpl ops) {
        mCounter = new MutableInt(0);
        mOps = ops;
        mAdapter =
            ((ContactsOpsImplAsync) ops).getAdapter();
    }

    /**
     * Set the counter.
     */
    public void setCounter(int value) {
        mCounter.setValue(value);
    }

    /**
     * Get the counter.
     */
    public MutableInt getCounter() {
        return mCounter;
    }

    /**
     * Set the iterator.
     */
    public void setIterator(Iterator<String> contactsIter) {
        mContactsIter = contactsIter;
    }

    /**
     * Get the iterator.
     */
    public Iterator<String> getIterator() {
        return mContactsIter;
    }

    /**
     * Get the ContactsOpsImpl.
     */
    public ContactsOpsImpl getOps() {
        return mOps;
    }

    /**
     * Get a reference to the AsyncProviderCommandAdapter.
     */
    public AsyncProviderCommandAdapter<CommandArgs> getAdapter() {
        return mAdapter;
    }
}

