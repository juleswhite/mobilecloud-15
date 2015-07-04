package vandy.mooc.view;

import android.database.Cursor;
import vandy.mooc.common.ContextView;

/**
 * This interface defines the minimum dependency between the HobbitOps
 * class in the "Presenter" layer and the HobbitActivity in the "View"
 * layer.
 */
public interface HobbitView extends ContextView {
    /**
     * Display the contents of the cursor as a ListView.
     */
    void displayCursor(Cursor cursor);
}
