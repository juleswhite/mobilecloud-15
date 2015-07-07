package vandy.mooc.model.mediator.webdata;

/**
 * This "Plain Ol' Java Object" (POJO) class represents meta-data of
 * interest downloaded in Json from the Video Service via the
 * VideoServiceProxy.
 */
public class VideoStatus {
    /**
     * Various fields corresponding to data downloaded in Json from
     * the Video WebService.
     */
    public enum VideoState {
        READY, 
        PROCESSING
    }

    /**
     * State of the Video.
     */
    private VideoState state;

    /**
     * Constructor that initializes all the fields of interest.
     */
    public VideoStatus(VideoState state) {
        super();
        this.state = state;
    }
    
    /*
     * Getters and setters to access VideoStatus.
     */
    
    public VideoState getState() {
        return state;
    }

    public void setState(VideoState state) {
        this.state = state;
    }
}
