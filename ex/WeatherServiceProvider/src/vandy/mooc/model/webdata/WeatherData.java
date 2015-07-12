package vandy.mooc.model.webdata;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

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
 * The Retrofit library handles automatic conversion from this Json
 * data to this object.  The Java annotations enable this
 * functionality.
 *
 */
public class WeatherData implements Parcelable {
    /*
     * These fields store the WeatherData's state.  We use
     * the @SerializedName annotation to make an explicit mapping
     * between the Json names and the fields in this class.  If we
     * named these fields the same as the Json names we won't need to
     * use this annotation.
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

    /**
     * Constructor that initializes the POJO.
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

	public Weather(long id,
                       String main,
                       String description,
                       String icon) {
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

	public Sys(long sunrise,
                   long sunset,
                   String country) {
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

	public Main(double temp,
                    long humidity,
                    double pressure) {
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

	public Wind(double speed,
                    double deg) {
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

    /*
     * BELOW THIS is related to Parcelable Interface.
     */

    /**
     * A bitmask indicating the set of special object types marshaled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write this instance out to byte contiguous memory.
     */
    @Override
    public void writeToParcel(Parcel dest,
                              int flags) {
        dest.writeString(mName);
        dest.writeLong(mDate);
        dest.writeLong(mCod);
        final Weather weather = mWeathers.get(0);
        dest.writeLong(weather.getId());
        dest.writeString(weather.getMain());
        dest.writeString(weather.getDescription());
        dest.writeString(weather.getIcon());
        dest.writeLong(mSys.getSunrise());
        dest.writeLong(mSys.getSunset());
        dest.writeString(mSys.getCountry());
        dest.writeDouble(mMain.getTemp());
        dest.writeLong(mMain.getHumidity());
        dest.writeDouble(mMain.getPressure());
        dest.writeDouble(mWind.getSpeed());
        dest.writeDouble(mWind.getDeg());
    }

    /**
     * Private constructor provided for the CREATOR interface, which
     * is used to de-marshal an WeatherData from the Parcel of data.
     * <p>
     * The order of reading in variables HAS TO MATCH the order in
     * writeToParcel(Parcel, int)
     *
     * @param in
     */
    private WeatherData(Parcel in) {
        mName = in.readString();
        mDate = in.readLong();
        mCod = in.readLong();

        mWeathers.add(new Weather(in.readLong(),
                                  in.readString(),
                                  in.readString(),
                                  in.readString()));

        mSys = new Sys(in.readLong(),
                       in.readLong(),
                       in.readString());

        mMain = new Main(in.readDouble(),
                         in.readLong(),
                         in.readDouble());

        mWind = new Wind(in.readDouble(),
                         in.readDouble());
    }

    /**
     * public Parcelable.Creator for WeatherData, which is an
     * interface that must be implemented and provided as a public
     * CREATOR field that generates instances of your Parcelable class
     * from a Parcel.
     */
    public static final Parcelable.Creator<WeatherData> CREATOR =
        new Parcelable.Creator<WeatherData>() {
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(in);
        }

        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };
}
