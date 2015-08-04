package vandy.mooc.view;

import vandy.mooc.R;
import vandy.mooc.common.GenericActivity;
import vandy.mooc.common.Utils;
import vandy.mooc.model.services.UploadVideoService;
import vandy.mooc.presenter.VideoOps;
import vandy.mooc.view.ui.FloatingActionButton;
import vandy.mooc.view.ui.VideoAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

/**
 * This Activity can be used upload a selected video to a Video
 * Service and also displays a list of videos available at the Video
 * Service. The user can record a video or get a video from gallery
 * and upload it. It extends GenericActivity that provides a framework
 * for automatically handling runtime configuration changes of an
 * VideosPresenter object, which plays the role of the "Presenter" in
 * the MVP pattern. The VideosPresenter.View interface is used to
 * minimize dependencies between the View and Presenter layers.
 */
public class VideoListActivity 
       extends GenericActivity<VideoOps.View, VideoOps>
       implements VideoOps.View {
    
    /**
     * The Request Code needed in Implicit Intent to get Video from
     * Gallery.
     */
    private final int REQUEST_GET_VIDEO = 1;
    
    /**
     * The Broadcast Receiver that registers itself to receive the
     * result from UploadVideoService when a video upload completes.
     */
    private UploadResultReceiver mUploadResultReceiver;

    /**
     * The Floating Action Button that will show a Dialog Fragment to
     * upload Video when user clicks on it.
     */
    private FloatingActionButton mUploadVideoButton;
    
    /**
     * The ListView that contains a list of Videos available from
     * the Video Service.
     */
    private ListView mVideosList;

  
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
        setContentView(R.layout.video_list_activity);

        // Receiver for the notification.
        mUploadResultReceiver =
            new UploadResultReceiver();
        
        // Get reference to the ListView for displaying the results
        // entered.
        mVideosList =
            (ListView) findViewById(R.id.videoList);

        // Get reference to the Floating Action Button.
        mUploadVideoButton =
                    (FloatingActionButton) findViewById(R.id.fabButton);
        
        // Displays a chooser dialog that gives options to
        // upload video from either by Gallery or by
        // VideoRecorder.
        mUploadVideoButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                displayChooserDialog();
            }
        });

        // Invoke the special onCreate() method in GenericActivity,
        // passing in the VideoOps class to instantiate/manage and
        // "this" to provide VideoOps with the VideoOps.View instance.
        super.onCreate(savedInstanceState,
                       VideoOps.class,
                       this);
    }
    
    
    /**
     * Displays a chooser dialog that gives options
     * to upload video from either by Gallery or by 
     * VideoRecorder.
     */
    private void displayChooserDialog() {
        // Create an intent that will start an Activity to
        // get Video from Gallery.
        final Intent videoGalleryIntent =
            new Intent(Intent.ACTION_GET_CONTENT)
                  .setType("video/*")
                  .putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        
        // Create an intent that will start an Activity to
        // Record the Video.
        final Intent recordVideoIntent =
            new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // Intent that wraps the given target Intent and
        // shows a chooser dialog to show the Apps that 
        // can handle the target Intent.
        final Intent chooserIntent =
            Intent.createChooser(videoGalleryIntent, "Upload Video via");

        // Add RecordVideo Intent, so that App that can
        // record video is added to the chooser dialog.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                               new Intent[] { recordVideoIntent });
        
        // Starts an Activity to get the Video either by Gallery
        // or by VideoRecorder.
        startActivityForResult(chooserIntent, REQUEST_GET_VIDEO);

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
        if (resultCode == Activity.RESULT_OK
            && requestCode == REQUEST_GET_VIDEO) {
            Utils.showToast(this, "Uploading video");

            // Upload the Video.
            getOps().uploadVideo(data.getData());
        } else
            Utils.showToast(this, "Could not get video to upload");
    }

    /**
     *  Hook method that is called when user resumes activity
     *  from paused state, onPause(). 
     */
    @Override
    protected void onResume() {
        // Call up to the superclass.
        super.onResume();

        // Register BroadcastReceiver that receives result from
        // UploadVideoService when a video upload completes.
        registerReceiver();
    }
    
    /**
     * Register a BroadcastReceiver that receives a result from the
     * UploadVideoService when a video upload completes.
     */
    private void registerReceiver() {
        
        // Create an Intent filter that handles Intents from the
        // UploadVideoService.
        IntentFilter intentFilter =
            new IntentFilter(UploadVideoService.ACTION_UPLOAD_SERVICE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
               .registerReceiver(mUploadResultReceiver,
                                 intentFilter);
    }

    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads.  onDestroy() may not always be
     * called-when system kills hosting process.
     */
    @Override
    protected void onPause() {
        // Call onPause() in superclass.
        super.onPause();
        
        // Unregister BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
          .unregisterReceiver(mUploadResultReceiver);
    }

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from UploadVideoService.
     */
    private class UploadResultReceiver 
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
     * Sets the Adapter that contains List of Videos to the ListView.
     */
    @Override
    public void setAdapter(VideoAdapter videoAdapter) {
        mVideosList.setAdapter(videoAdapter);
    }

    /**
     * Finishes this Activity.
     */
    @Override
    public void finish() {
        super.finish();
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
