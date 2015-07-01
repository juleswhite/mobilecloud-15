package vandy.mooc.common;

import java.util.Iterator;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

/**
 * @class AsyncCommand
 *
 * @brief This base class is extended by the various commands (e.g.,
 *        Insert, Query, and Delete) used to interact with the
 *        Contacts Provider.  It plays the role of the Abstract
 *        Command in the Command pattern, so subclasses must override
 *        the abstract execute() method.  It also contains an Iterator
 *        that's used to chain together a set of AsyncCommands that
 *        will be run (asynchronously) in a sequence.
 */
public abstract class AsyncCommand
       extends AsyncQueryHandler {
    /**
     * An Iterator that contains a chain of AsyncCommands that will be
     * run in a sequence.
     */
    private Iterator<AsyncCommand> mAsyncCommandIter;

    /**
     * Constructor is passed a ContentResolver, which is passed up to
     * the super class.
     */
    public AsyncCommand(ContentResolver contentResolver) {
        super (contentResolver);
    }

    /**
     * When there is no other processing to run for an AsyncCommand a
     * subclass of AsyncCommand should call executeNext() in its
     * execute() method so that the next AsyncCommand in the Iterator
     * can run.
     */
    protected void executeNext() {
        // Executes the next AsyncCommand in the command iterator.
        if (mAsyncCommandIter.hasNext()) 
            mAsyncCommandIter.next().execute();
    }

    /**
     * Can be overridden by subclasses to implement their
     * command-specific logic.
     */
    abstract public void execute();

    /**
     * Set the Iterator used to chain together the AsyncCommands into
     * a sequence.
     */
    public void setIterator(Iterator<AsyncCommand> asyncCommandIter) {
        mAsyncCommandIter = asyncCommandIter;
    }

    /**
     * Execute the array of asyncCommands passed as a parameter.
     */
    public static void executeAsyncCommands(AsyncCommand[] asyncCommands) {
        GenericArrayIterator<AsyncCommand> asyncCommandsIter = 
             new GenericArrayIterator<>(asyncCommands);

        // Pass the Iterator to each of the AsyncCommands passed as a
        // parameter.
        for (AsyncCommand asyncCommand : asyncCommands)
            asyncCommand.setIterator(asyncCommandsIter);

        // Start executing the first AsyncCommand in the chain of
        // AsyncCommands.
        asyncCommandsIter.next().execute();
    }
}
