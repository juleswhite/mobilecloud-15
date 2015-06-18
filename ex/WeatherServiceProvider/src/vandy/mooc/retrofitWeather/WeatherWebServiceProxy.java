package vandy.mooc.retrofitWeather;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Interface defining methods used by RetroFit to access current
 * weather data from the Weather Service web service.
 */
public interface WeatherWebServiceProxy {
    /**
     * Method used to query the Weather Service web service for the
     * current weather at a city @a location.  The annotations enable
     * Retrofit to convert the @a location parameter into an HTTP
     * request, which would look something like this:
     * http://api.openweathermap.org/data/2.5/weather?q=location
     */
    @GET("/weather")
    WeatherData getWeatherData(@Query("q") String location);
}

