package vandy.mooc.model;

import java.util.ArrayList;
import java.util.List;

import vandy.mooc.model.AcronymData.AcronymExpansion;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is a Plain Old Java Object (POJO) used for data
 * transport within the Acronym app. This POJO implements the
 * Parcelable interface to enable IPC between the AcronymActivity and
 * the AcronymServiceSync and AcronymServiceAsync. It represents the
 * response Json obtained from the Acronym API, e.g., a call to
 * http://www.nactem.ac.uk/software/acromine/dictionary.py?sf=BBC
 * might return the following Json data:
 * 
 * [{"sf": "BBC", "lfs": [{"lf": "British Broadcasting Corporation", "freq": 8,
 * "since": 1986, "vars": [{"lf": "British Broadcasting
 * Corporation", "freq": 8, "since": 1986}]}, {"lf": "backbone
 * cyclic", "freq": 5, "since": 1999, "vars": [{"lf": "backbone
 * cyclic", "freq": 5, "since": 1999}]}, {"lf": "bilateral breast
 * cancer", "freq": 5, "since": 2000, "vars": [{"lf": "bilateral breast
 * cancer", "freq": 4, "since": 2000}, {"lf": "Bilateral breast
 * cancer", "freq": 1, "since": 2006}]}, {"lf": "bone bisphosphonate
 * clearance", "freq": 3, "since": 1992, "vars": [{"lf": "bone bisphosphonate
 * clearance", "freq": 3, "since": 1992}]}, {"lf": "bovine brain capillary",
 * "freq": 3, "since": 1989, "vars": [{"lf": "bovine brain capillary", "freq":
 * 2, "since": 1989}, {"lf": "bovine brain capillaries", "freq": 1, "since":
 * 2000}]}]}]
 * 
 * Parcelable defines an interface for marshaling/de-marshaling
 * https://en.wikipedia.org/wiki/Marshalling_(computer_science) to/from a format
 * that Android uses to allow data transport between processes on a device.
 * Discussion of the details of Parcelable is outside the scope of this
 * assignment, but you can read more at
 * https://developer.android.com/reference/android/os/Parcelable.html.
 */
public class ParcelableAcronymExpansion
       implements Parcelable {
    /*
     * These data members are the local variables that will store the
     * AcronymExpansion's state
     */

    /**
     * The long form of the acronym (spelled out version).
     */
    private String lf;

    /**
     * The relative frequency of usage in print, of this meaning of the acronym.
     */
    private int freq;

    /**
     * The year the acronym was added to this database of acronyms, or was
     * originally termed.
     */
    private int since;

    /**
     * Returns an ArrayList of ParcelableAcronymExpansions based on
     * the "long forms" stored in the @a acronymData parameter.
     */
    public static ArrayList<ParcelableAcronymExpansion> getExpansions
                                           (List<AcronymExpansion> results) {
        // Create an ArrayList that's sized properly.
        final ArrayList<ParcelableAcronymExpansion> longForms = 
            new ArrayList<>(results.size());
                    
        // Create a ParcelableAcronymExpansion for each
        // AcronymExpansion and add it to the longForms ArrayList.
        for (AcronymExpansion acronymExpansion : results)
            longForms.add
                (new ParcelableAcronymExpansion(acronymExpansion));

        return longForms;
    }

    /**
     * Private constructor provided for the CREATOR interface, which
     * is used to de-marshal a ParcelableAcronymExpansion from the
     * Parcel of data.
     */
    private ParcelableAcronymExpansion(Parcel in) {
        lf = in.readString();
        freq = in.readInt();
        since = in.readInt();
    }

    /**
     * Constructor that initialises a ParcelableAcronymExpansion
     * object from its parameters.
     */
    public ParcelableAcronymExpansion(AcronymExpansion ae) {
        super();
        this.lf = ae.getLf();
        this.freq = ae.getFreq();
        this.since = ae.getSince();
    }

    /*
     * Getters and setters to access ParcelableAcronymExpansion.
     */

    public String getLf() {
        return lf;
    }

    public void setLf(String lf) {
        this.lf = lf;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getSince() {
        return since;
    }

    public void setSince(int since) {
        this.since = since;
    }

    /**
     * The toString() custom implementation.
     */
    @Override
	public String toString() {
        return "AcronymExpansion [lf=" 
            + lf 
            + ", freq=" 
            + freq 
            + ", since=" 
            + since
            + "]";
    }

    /*
     * Parcelable related methods.
     */

    /**
     * A bitmask indicating the set of special object types marshaled by the
     * Parcelable.
     */
    @Override
	public int describeContents() {
        return 0;
    }

    /**
     * Marshal this ParcelableAcronymExpansion to the target Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest,
                              int flags) {
        dest.writeString(lf);
        dest.writeInt(freq);
        dest.writeInt(since);
    }

    /**
     * public Parcelable.Creator for ParcelableAcronymExpansion, which
     * is an interface that must be implemented and provided as a
     * public CREATOR field that generates instances of your
     * Parcelable class from a Parcel.
     */
    public static final Parcelable.Creator<ParcelableAcronymExpansion> CREATOR =
        new Parcelable.Creator<ParcelableAcronymExpansion>() {
        public ParcelableAcronymExpansion createFromParcel(Parcel in) {
            return new ParcelableAcronymExpansion(in);
        }

        public ParcelableAcronymExpansion[] newArray(int size) {
            return new ParcelableAcronymExpansion[size];
        }
    };
}
