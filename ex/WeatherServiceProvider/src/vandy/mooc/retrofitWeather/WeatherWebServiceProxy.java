package vandy.mooc.retrofitWeather;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Interface defining methods used in the RetroFit service to access current
 * weather data from the openweathermap api.
 */
public interface WeatherWebServiceProxy {
    /**
     * Method used to query the weather api for the current weather at a
     * location. The Annotations allow the java parameters to be converted
     * into a http request.
     */
    @GET("/weather")
    WeatherData getWeatherData(@Query("q") String location);
}

