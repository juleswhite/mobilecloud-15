package vandy.mooc.utils;

import java.util.Iterator;

/**
 * Base interface implemented by the ContactsOpsSimple and
 * ContactsOpsImplLoaderManager ContactsCommand subclasses.
 */
public interface ContactsCommand {
    /**
     * Execute the command on the @a contactsIter.
     */
    void execute(Iterator<String> contactsIter);
}
