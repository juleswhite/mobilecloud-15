package vandy.mooc.operations;

import java.util.Iterator;

/**
 * Base interface implemented by all ContactsCommand subclasses.
 */
public interface ContactsCommand {
    // Execute the command on the @a contactsIter.
    void execute(Iterator<String> contactsIter);
}
