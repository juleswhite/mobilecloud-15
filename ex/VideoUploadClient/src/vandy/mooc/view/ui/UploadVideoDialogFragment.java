package vandy.mooc.view.ui;

import vandy.mooc.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * UploadVideoDialog Fragment shows user a Dialog that lists various
 * means of uploading a Video.
 */
public class UploadVideoDialogFragment extends DialogFragment {
    /**
     * The various means of uploading a Video.
     */
    public enum OperationType {
        /**
         * Position of Video Gallery Option in List.
         */
        VIDEO_GALLERY,

        /**
         * Position of Record Video Option in List.
         */
        RECORD_VIDEO
    };

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
        public void onVideoSelected(OperationType which);
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
            throw new ClassCastException
                (activity.toString()
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
        // Builder for creating a new Dialog.
        AlertDialog.Builder builder = 
            new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_upload_video)
               .setItems(listItems,
                         //Set OnClick listener for the Dialog.
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog,
                                                 int which) {
                                 UploadVideoDialogFragment.OperationType type =
                                     UploadVideoDialogFragment.OperationType
                                                              .values()[which];
                                 // Select the means of uploading a video.
                                 mListener.onVideoSelected(type);
                             }
                         });

        // Use the Builder pattern to create the Dialog.
        return builder.create();
    }
}
