package vandy.mooc.operations.ContactsOpsImplAsync;

import java.util.Iterator;

import vandy.mooc.common.MutableInt;

public class AsyncCommandArgs {
    public AsyncCommandArgs(Iterator<String> contactsIter) {
        mCounter = new MutableInt(0);
        mContactsIter = contactsIter;
    }

    /**
     * Iterator containing the contacts to delete.
     */
    final public Iterator<String> mContactsIter;

    /**
     * Keeps track of the number of contacts deleted.
     */
    final public MutableInt mCounter;
}

