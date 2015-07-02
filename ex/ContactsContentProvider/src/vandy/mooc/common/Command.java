package vandy.mooc.common;


/**
 * Base interface that defines a command a la the Command pattern.
 */
public interface Command<T> {
    /**
     * Execute the command with the @a param.
     */
    void execute(T param);
}
