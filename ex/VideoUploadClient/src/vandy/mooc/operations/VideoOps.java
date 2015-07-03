package vandy.mooc.operations;

import java.lang.ref.WeakReference;
import java.util.List;

import vandy.mooc.activities.VideoListActivity;
import vandy.mooc.common.ConfigurableOps;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.data.VideoController;
import vandy.mooc.data.model.Video;
import vandy.mooc.services.UploadVideoService;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * This class implements all the Video-related operations.  It
 * implements ConfigurableOps so it can be created/managed by the
 * GenericActivity framework.  It implements Callback so it can serve
 * as the target of an asynchronous Retrofit RPC call.  It extends
 * GenericAsyncTaskOps so its doInBackground() method runs in a
 * background task.
 */
public class VideoOps
       implements ConfigurableOps,
                  GenericAsyncTaskOps<Void, Void, List<Video>> {
    /**
     * Debugging tag used by the Android logger.
     */
    private static final String TAG =
        VideoOps.class.getSimpleName();
    
    /**
     * Used to enable garbage collection.
     */
    private WeakReference<VideoListActivity> mActivity;
    
    /**
     *  It allows access to application-specific resources.
     */
    private Context mApplicationContext;
    
    /**
     * The GenericAsyncTask used to expand an Video in a background
     * thread via the Video web service.
     */
    private GenericAsyncTask<Void, Void, List<Video>, VideoOps> mAsyncTask;
    
    /**
     * VideoController mediates the communication between Server and
     * Android Storage.
     */
    VideoController mVideoController;
    
    /**
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public VideoOps() {
    }
    
    /**
     * Called after a runtime configuration change occurs to finish
     * the initialisation steps.
     */
    public void onConfiguration(Activity activity,
                                boolean firstTimeIn) {
        final String time =
            firstTimeIn 
            ? "first time" 
            : "second+ time";
        
        Log.d(TAG,
              "onConfiguration() called the "
              + time
              + " with activity = "
              + activity);

        // (Re)set the mActivity WeakReference.
        mActivity =
            new WeakReference<>((VideoListActivity) activity);

        if (firstTimeIn) {
            // Get the Application Context.
            mApplicationContext =
                activity.getApplicationContext();
            
            // Create VideoController that will mediate the 
            // communication between Server and Android Storage.
            mVideoController =
                new VideoController(mApplicationContext);
            
            // Get the VideoList from Server. 
            getVideoList();
        }
    }

    /**
     * Gets the VideoList from Server by executing the AsyncTask to
     * expand the acronym without blocking the caller.
     */
    public void getVideoList(){
        mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute();
    }
    
    /**
     * Start a service that Uploads the Video having given Id.
     *   
     * @param videoId
     */
    public void uploadVideo(Long videoId){
        mApplicationContext.startService
            (UploadVideoService.makeIntent 
                 (mApplicationContext,
                  videoId));
    }
    
    /**
     * Retrieve the List of Videos by help of VideoController via a
     * synchronous two-way method call, which runs in a background
     * thread to avoid blocking the UI thread.
     */
    @Override
    public List<Video> doInBackground(Void... params) {
        return mVideoController.getVideoList();
    }

    /**
     * Display the results in the UI Thread.
     */
    @Override
    public void onPostExecute(List<Video> videos) {
        mActivity.get().displayVideoList(videos);
    }
}
