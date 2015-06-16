package vandy.mooc.retrofitWeather;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;


/**
 * This class is a Plain Old Java Object (POJO) used for data transport within
 * the WeatherService app. It represents the response Json obtained from the
 * Open Weather Map API, e.g., a call to
 * http://api.openweathermap.org/data/2.5/weather?q=Nashville,TN might return
 * the following Json data:
 * 
 * { "coord":{ "lon":-86.78, "lat":36.17 }, "sys":{ "message":0.0138,
 * "country":"United States of America", "sunrise":1431427373,
 * "sunset":1431477841 }, "weather":[ { "id":802, "main":"Clouds",
 * "description":"scattered clouds", "icon":"03d" } ], "base":"stations",
 * "main":{ "temp":289.847, "temp_min":289.847, "temp_max":289.847,
 * "pressure":1010.71, "sea_level":1035.76, "grnd_level":1010.71, "humidity":76
 * }, "wind":{ "speed":2.42, "deg":310.002 }, "clouds":{ "all":36 },
 * "dt":1431435983, "id":4644585, "name":"Nashville", "cod":200 }
 *
 * The meaning of these Json fields is documented at
 * http://openweathermap.org/weather-data#current.
 * 
 * The Retrofit library handles automatic conversion from this Json data to this
 * object. The annotations enable this functionality.
 *
 */
public class WeatherData {
    /*
     * These data members are the local variables that will store the
     * WeatherData's state
     */
    @SerializedName("name")
        private String mName;
    @SerializedName("dt")
        private long mDate;
    @SerializedName("cod")
        private long mCod;
    @SerializedName("weather")
        private List<Weather> mWeathers = new ArrayList<Weather>();
    @SerializedName("sys")
        private Sys mSys;
    @SerializedName("main")
        private Main mMain;
    @SerializedName("wind")
        private Wind mWind;

    /*
     * End Inner classes
     */

    /**
     * Constructor
     * 
     * @param name
     * @param speed
     * @param deg
     * @param temp
     * @param humidity
     * @param sunrise
     * @param sunset
     */
    public WeatherData(String name,
                       double speed,
                       double deg,
                       double temp,
                       long humidity,
                       long sunrise,
                       long sunset,
                       String country,
                       double pressure,
                       long date,
                       long cod,
                       List<Weather> weathers) {
	mName = name;
	mDate = date;
	mCod = cod;
	mSys = new Sys(sunrise, sunset, country);
	mMain = new Main(temp, humidity, pressure);
	mWind = new Wind(speed, deg);
	mWeathers = weathers;
    }

    /**
     * Constructor
     */
    public WeatherData(String name,
                       long date,
                       long cod,
                       Sys sys,
                       Main main,
                       Wind wind,
                       List<Weather> weathers) {
	mName = name;
	mDate = date;
	mCod = cod;
	mSys = sys;
	mMain = main;
	mWind = wind;
	mWeathers = weathers;
    }

    /*
     * Access methods for data members
     */

    /**
     * Access method for the System info
     * 
     * @param data
     */
    public Sys getSys() {
	return mSys;
    }

    /**
     * Access method for the Main info
     * 
     * @param data
     */
    public Main getMain() {
	return mMain;
    }

    /**
     * Access method for the Wind info
     * 
     * @param data
     */
    public Wind getWind() {
	return mWind;
    }

    /**
     * Access method for location's name
     * 
     * @param data
     */
    public String getName() {
	return mName;
    }

    /**
     * Access method for the data's date
     * 
     * @param data
     */
    public long getDate() {
	return mDate;
    }

    /**
     * Access method for the cod data
     * 
     * @param data
     */
    public long getCod() {
	return mCod;
    }

    /**
     * Access method for the Weather objects
     * 
     * @param data
     */
    public List<Weather> getWeathers() {
	return mWeathers;
    }

    /**
     * Inner class representing a description of a current weather
     * condition.
     */
    public static class Weather {
	@SerializedName("id")
            private long mId;
	@SerializedName("main")
            private String mMain;
	@SerializedName("description")
            private String mDescription;
	@SerializedName("icon")
            private String mIcon;

	public Weather(long id, String main, String description, String icon) {
	    mId = id;
	    mMain = main;
	    mDescription = description;
	    mIcon = icon;
	}

	/*
	 * Access methods for data members.
	 */

	public long getId() {
	    return mId;
	}

	public String getMain() {
	    return mMain;
	}

	public String getDescription() {
	    return mDescription;
	}

	public String getIcon() {
	    return mIcon;
	}

    }

    /**
     * Inner class representing system data.
     */
    public static class Sys {
	@SerializedName("sunrise")
            private long mSunrise;
	@SerializedName("sunset")
            private long mSunset;
	@SerializedName("country")
            private String mCountry;

	public Sys(long sunrise, long sunset, String country) {
	    mSunrise = sunrise;
	    mSunset = sunset;
	    mCountry = country;
	}

	/*
	 * Access methods for data members
	 */

	public long getSunrise() {
	    return mSunrise;
	}

	public long getSunset() {
	    return mSunset;
	}

	public String getCountry() {
	    return mCountry;
	}
    }

    /**
     * Inner class representing the core weather data
     */
    public static class Main {
	@SerializedName("temp")
            private double mTemp;
	@SerializedName("humidity")
            private long mHumidity;
	@SerializedName("pressure")
            private double mPressure;

	public Main(double temp, long humidity, double pressure) {
	    mTemp = temp;
	    mHumidity = humidity;
	    mPressure = pressure;
	}

	/*
	 * Access methods for data members
	 */

	public double getPressure() {
	    return mPressure;
	}

	public double getTemp() {
	    return mTemp;
	}

	public long getHumidity() {
	    return mHumidity;
	}
    }

    /**
     * Inner class representing wind data
     */
    public static class Wind {
	@SerializedName("speed")
            private double mSpeed;
	@SerializedName("deg")
            private double mDeg;

	public Wind(double speed, double deg) {
	    mSpeed = speed;
	    mDeg = deg;
	}

	/*
	 * Access methods for data members
	 */

	public double getSpeed() {
	    return mSpeed;
	}

	public double getDeg() {
	    return mDeg;
	}
    }
}
