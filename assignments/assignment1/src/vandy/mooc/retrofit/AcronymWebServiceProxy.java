package vandy.mooc.retrofit;

import java.util.List;

import retrofit.http.GET;
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
     * service.
     * 
     * @param shortForm
     * @return List of JsonAcronym
     */
    @GET("/dictionary.py")
    public List<AcronymData> getAcronymResults
        (@Query(SHORT_FORM_QUERY_PARAMETER) String shortForm);
}
