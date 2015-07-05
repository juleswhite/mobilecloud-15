package vandy.mooc.view.ui;

import vandy.mooc.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * UploadVideoDialog Fragment shows user a Dialog that has a list of
 * option to get Video from.
 */
public class UploadVideoDialogFragment extends DialogFragment {
    /**
     * Position of Video Gallery Option in List.
     */
    public static final int VIDEO_GALLERY = 0;

    /**
     * Position of Record Video Option in List.
     */
    public static final int RECORD_VIDEO = 1;

    /**
     * Array to hold List items.
     */
    private String[] listItems = { 
        "Video Gallery",
        "Record a Video" 
    };

    /**
     * Callback that will send the result to Activity that implements
     * it, when the Option is selected.
     */
    private OnVideoSelectedListener mListener;

    /**
     * Container Activity must implement this interface
     */
    public interface OnVideoSelectedListener {
        public void onVideoSelected(int which);
    }

    /**
     * Hook method called when a fragment is first attached to its
     * activity. onCreate(Bundle) will be called after this.
     * 
     * @param activity
     */
    @Override
        public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener =
                (OnVideoSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                                         + " must implement OnVideoSelectedListener");
        }
    }

    /**
     * This method will be called after onCreate(Bundle) and before
     * onCreateView(LayoutInflater, ViewGroup, Bundle).  The default
     * implementation simply instantiates and returns a Dialog class.
     */
    @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = 
            new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_upload_video)
               .setItems(listItems,
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog,
                                                 int which) {
                                 mListener.onVideoSelected(which);
                             }
                         });
        return builder.create();
    }
}
