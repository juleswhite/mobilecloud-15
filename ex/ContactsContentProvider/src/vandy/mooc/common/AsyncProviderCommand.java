package vandy.mooc.common;

import android.database.Cursor;
import android.net.Uri;


/**
 * This base class can be extended to interact with the Content
 * Providers.  It plays the role of the Abstract Command in the
 * Command pattern, so subclasses must override the abstract execute()
 * and onCompletion() methods.  
 */
public abstract class AsyncProviderCommand<ArgType> {
    /**
     * Store the arguments needed to run an AsyncCommand.
     */
    private final ArgType mArgs;

    /**
     * Constructor initializes the field.
     */
    public AsyncProviderCommand(ArgType args) {
        mArgs = args;
    }

    /**
     * Can be overridden by subclasses to implement command-specific
     * logic for initiating an asynchronous operation on a Content
     * Provider.
     */
    abstract public void execute();

    /**
     * Can be overridden by subclasses to implement command-specific
     * completion logic when an asynchronous operation on a Content
     * Provider completes.
     */
    public void onCompletion(int token, int result) {
    }

    public void onCompletion(int token, Uri uri) {
    }

    public void onCompletion(int token, Cursor cursor) {
    }

    /**
     * Get the args.
     */
    public ArgType getArgs() {
        return mArgs;
    }
}
