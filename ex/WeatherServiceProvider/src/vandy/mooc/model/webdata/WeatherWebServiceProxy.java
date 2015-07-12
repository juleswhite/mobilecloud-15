package vandy.mooc.model.webdata;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Interface defining methods used by RetroFit to access current
 * weather data from the Weather Service web service.
 */
public interface WeatherWebServiceProxy {
    /**
     * URL to the Web Search web service to use with the Retrofit
     * service.
     */
    final String sWeather_Service_URL_Retro =
        "http://api.openweathermap.org/data/2.5";

    /**
     * Method used to query the Weather Service web service for the
     * current weather at a city @a location.  The annotations enable
     * Retrofit to convert the @a location parameter into an HTTP
     * request, which would look something like this:
     * http://api.openweathermap.org/data/2.5/weather?q=location
     * 
     * @param location
     * @return WeatherData
     */
    @GET("/weather")
    WeatherData getWeatherData(@Query("q") String location);

    /**
     * Method used to query the Weather Service web service for the
     * current weather at a city @a location.  The annotations enable
     * Retrofit to convert the @a location parameter into an HTTP
     * request, which would look something like this:
     * http://api.openweathermap.org/data/2.5/weather?q=location
     * 
     * @param location
     * @param callback
     * @return WeatherData
     */
    @GET("/weather")
    public void getWeatherData
        (@Query("q") String location,
         Callback<WeatherData> callback);    
}

