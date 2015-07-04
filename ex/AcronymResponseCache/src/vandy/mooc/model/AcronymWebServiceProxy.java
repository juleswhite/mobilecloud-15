package vandy.mooc.model;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Retrofit Service to send requests to Acronym web service and
 * convert the Json response to POJO class.
 */
public interface AcronymWebServiceProxy {
    /**
     * URL to the Acronym web service.
     */
    public static final String ENDPOINT =
        "http://www.nactem.ac.uk/software/acromine";

    /**
     * Query Parameter.
     */
    public static final String SHORT_FORM_QUERY_PARAMETER =
        "sf";

    /**
     * Get List of LongForm associated with acronym from Acronym Web
     * service.  It caches the data and expires the data after a 10
     * second timeout.
     * 
     * @param shortForm
     * @return List of JsonAcronym
     */
    @Headers("Cache-Control: public, max-stale=10")
    @GET("/dictionary.py")
    public List<AcronymData> getAcronymResults
        (@Query(SHORT_FORM_QUERY_PARAMETER) String shortForm);

    /**
     * Get List of LongForm associated with acronym from Acronym Web
     * service.  Asynchronous execution requires the last parameter of
     * the method be a Callback.
     * 
     * 
     * @param shortForm
     * @param callback
     * @return List of JsonAcronym
     */
    @Headers("Cache-Control: public, max-stale=10")
    @GET("/dictionary.py")
    public void getAcronymResults
        (@Query(SHORT_FORM_QUERY_PARAMETER) String shortForm,
         Callback<List<AcronymData>> callback);    
}
