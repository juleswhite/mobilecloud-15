package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.model.services.UploadVideoService;
import vandy.mooc.presenter.VideoOps;
import vandy.mooc.utils.VideoGalleryUtils;
import vandy.mooc.view.ui.FloatingActionButton;
import vandy.mooc.view.ui.UploadVideoDialogFragment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * This Activity can be used upload a selected video to a Video
 * Service and also displays a list of videos available at the Video
 * Service.  The user can record a video or get a video from gallery
 * and upload it.  This Activity extends GenericActivity, which
 * provides a framework that automatically handles runtime
 * configuration changes.  It implements OnVideoSelectedListener that
 * will handle callbacks from the UploadVideoDialog Fragment.
 */
public class VideoListActivity 
       extends GenericActivity<VideoOps> 
       implements UploadVideoDialogFragment.OnVideoSelectedListener {
    /**
     * The Request Code needed in Implicit Intent start Video
     * Recording Activity.
     */
    private final int REQUEST_VIDEO_CAPTURE = 0;

    /**
     * The Request Code needed in Implicit Intent to get Video from
     * Gallery.
     */
    private final int REQUEST_GET_VIDEO = 1;

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from UploadVideoService when a video upload completes.
     */
    private UploadResultReceiver mUploadResultReceiver;

    /**
     * The Floating Action Button that will show a Dialog Fragment to
     * upload Video when user clicks on it.
     */
    private FloatingActionButton mUploadVideoButton;
    
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., storing Views.
     * 
     * @param Bundle
     *            object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the default layout.
        setContentView(R.layout.activity_video_list);

        // Register BroadcastReceiver that receives result from
        // UploadVideoService when a video upload completes.
        registerReceiver();

        // Show the Floating Action Button.
        createPlusFabButton();

        // Call up to the special onCreate() method in
        // GenericActivity, passing in the VideoOps class to
        // instantiate and manage.
        super.onCreate(savedInstanceState,
                       VideoOps.class);
    }

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from UploadVideoService. 
     */
    public class UploadResultReceiver 
           extends BroadcastReceiver {
        /**
         * Hook method that's dispatched when the UploadService has
         * uploaded the Video.
         */
        @Override
        public void onReceive(Context context,
                              Intent intent) {
            // Starts an AsyncTask to get fresh Video list from the
            // Video Service.
            getOps().getVideoList();
        }
    }

    /**
     * Register a BroadcastReceiver that receives a result from the
     * UploadVideoService when a video upload completes.
     */
    private void registerReceiver() {
        // Receiver for the notification.
        mUploadResultReceiver = new UploadResultReceiver();

        // Create an Intent filter that handles Intents from the
        // UploadVideoService.
        IntentFilter intentFilter =
            new IntentFilter(UploadVideoService.ACTION_UPLOAD_SERVICE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.
        registerReceiver(mUploadResultReceiver,
                         intentFilter);
    }

    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads. onDestroy() may not always be called-when
     * system kills hosting process
     */
    @Override
    protected void onDestroy() {
        // Call destroy in superclass.
        super.onDestroy();

        // Unregister BroadcastReceiver.
        unregisterReceiver(mUploadResultReceiver);
    }

    /**
     * The user selected option to get Video from UploadVideoDialog
     * Fragment.  Based on what the user selects either record a Video
     * or get a Video from the Video Gallery.
     */
    @Override
    public void onVideoSelected(int which) {
        if (which == UploadVideoDialogFragment.VIDEO_GALLERY) 
            // Get the Video from Video Gallery.
            getVideoFromGallery();
        else if (which == UploadVideoDialogFragment.RECORD_VIDEO) 
            // Record a video.
            selectRecordVideo();
    }

    /**
     * Start an Activity by implicit Intent to get the Video from
     * Android Video Gallery.
     */
    private void getVideoFromGallery() {
        // Create an intent that will start an Activity to
        // get Video from Gallery.
        final Intent intent = 
            new Intent(Intent.ACTION_GET_CONTENT)
            .setType("video/*")
            .putExtra(Intent.EXTRA_LOCAL_ONLY,
                      true);

        // Verify that the intent will resolve to an Activity.
        if (intent.resolveActivity(getPackageManager()) != null) 
            startActivityForResult(intent,
                                   REQUEST_GET_VIDEO);
    }

    /**
     * Start an Activity by Implicit Intent to Record the Video.
     */
    private void selectRecordVideo() {
        // Create an intent that will start an Activity to
        // get Record Video.
        final Intent intent =
            new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        
        // Verify that the intent will resolve to an Activity.
        if (intent.resolveActivity(getPackageManager()) != null) 
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }

    /**
     * Hook method called when an activity you launched exits, giving
     * you the requestCode you started it with, the resultCode it
     * returned, and any additional data from it.
     * 
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {
        // Check if the Result is Ok and upload the Video to server.
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of recorded video.
            Uri videoUri = data.getData();
            Long videoId = null;

            if (requestCode == REQUEST_VIDEO_CAPTURE) 
                // Get the Video Id of the recorded video from Uri.
                videoId = 
                    ContentUris.parseId(videoUri);
            else if (requestCode == REQUEST_GET_VIDEO) 
                // Get the Video Id of the Gallery video from Uri.
                videoId = 
                    VideoGalleryUtils.getVideoId(getApplicationContext(),
                                                 videoUri);

            Utils.showToast(this,
                            "Uploading video");
            
            // Upload the Video.
            getOps().uploadVideo(videoId,
                                 videoUri);
        } else 
            Utils.showToast(this,
                            "Could not get video to upload");
    }

    /**
     * Show the Floating Action Button that will show a Dialog
     * Fragment to upload Video when user clicks on it.
     */
    @SuppressWarnings("deprecation")
    private void createPlusFabButton() {
        final DisplayMetrics metrics =
            getResources().getDisplayMetrics();
        final int position =
            (metrics.widthPixels / 4) + 5;

        mUploadVideoButton =
            new FloatingActionButton
            .Builder(this)
            .withDrawable(getResources()
                          .getDrawable(R.drawable.ic_video))
            .withButtonColor(getResources()
                             .getColor(R.color.theme_primary))
            .withGravity(Gravity.BOTTOM | Gravity.END)
            .withMargins(0, 
                         0,
                         position,
                         0)
            .create();

        // Show the UploadVideoDialog Fragment when user clicks the
        // Button.
        mUploadVideoButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new UploadVideoDialogFragment().show(getFragmentManager(),
                                                         "uploadVideo");
                }
            });
    }


    /**
     * Hook method called to initialize the contents of the Activity's
     * standard options menu.
     * 
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it
        // is present.
        getMenuInflater().inflate(R.menu.video_list,
                                  menu);
        return true;
    }

    /**
     * Hook method called whenever an item in your options menu is
     * selected
     * 
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
