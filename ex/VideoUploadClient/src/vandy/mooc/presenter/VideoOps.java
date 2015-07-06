package vandy.mooc.presenter;

import java.lang.ref.WeakReference;
import java.util.List;

import vandy.mooc.R;
import vandy.mooc.common.ConfigurableOps;
import vandy.mooc.common.GenericAsyncTask;
import vandy.mooc.common.GenericAsyncTaskOps;
import vandy.mooc.common.Utils;
import vandy.mooc.model.provider.Video;
import vandy.mooc.model.provider.VideoController;
import vandy.mooc.model.services.UploadVideoService;
import vandy.mooc.view.VideoListActivity;
import vandy.mooc.view.ui.VideoAdapter;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;

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
     * The ListView that contains a list of Videos available from
     * Server.
     */
    private ListView mVideosList;
    
    /**
     * The Adapter that is needed by ListView to show the list of
     * Videos.
     */
    private VideoAdapter mAdapter;
    
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
        
        // Get reference to the ListView for displaying the results
        // entered.
        mVideosList =
            (ListView) mActivity.get().findViewById(R.id.videoList);

        if (firstTimeIn) {
            // Get the Application Context.
            mApplicationContext =
                activity.getApplicationContext();
            
            // Create VideoController that will mediate the
            // communication between Server and Android Storage.
            mVideoController =
                new VideoController(mApplicationContext);
            
            // Create a local instance of our custom Adapter for our
            // ListView.
            mAdapter = new VideoAdapter(mApplicationContext);
            
            // Get the VideoList from Server. 
            getVideoList();
        }
        
       // Set the adapter to the ListView.
       mVideosList.setAdapter(mAdapter);
    }

    /**
     * Display the Videos in ListView
     * 
     * @param videos
     */
    public void displayVideoList(List<Video> videos) {
        if (videos != null) {
            // Update the adapter with the List of Videos.
            mAdapter.setVideos(videos);
            Utils.showToast(mActivity.get(),
                            "Video meta-data loaded from Video Service");
        } else {
            Utils.showToast(mActivity.get(),
                           "Please connect to the Video Service");
            
            mActivity.get().finish();
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
    public void uploadVideo(Long videoId, Uri videoUri){
        mApplicationContext.startService
            (UploadVideoService.makeIntent 
                 (mApplicationContext,
                  videoId,
                  videoUri));
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
        displayVideoList(videos);
    }
}
