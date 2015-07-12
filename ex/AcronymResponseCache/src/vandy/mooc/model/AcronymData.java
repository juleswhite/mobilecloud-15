package vandy.mooc.model;

import java.util.List;

/**
 * This "Plain Ol' Java Object" (POJO) class represents data of
 * interest downloaded in Json from the AcronymWebServiceProxy.  We
 * don't care about all the data, just the fields defined in this
 * class.
 */
public class AcronymData {
    /**
     * Various fields corresponding to data downloaded in Json from
     * the Acronym WebService.
     */
    private String sf;
    private List<AcronymExpansion> lfs;

    /**
     * No-op constructor
     */
    public AcronymData() {
    }

    /**
     * Constructor that initializes all the fields of interest.
     */
    public AcronymData(String sf,
                       List<AcronymExpansion> lfs) {
        super();
        this.sf = sf;
        this.lfs = lfs;
    }

    /**
     * @return the acronym
     */
    public String getSf() {
        return sf;
    }

    /**
     * @param acronym
     */
    public void setSf(String sf) {
        this.sf = sf;
    }

    /**
     * @return the List of AcronymData associated with that acronym
     */
    public List<AcronymExpansion> getLfs() {
        return lfs;
    }

    /**
     * @param the
     *            List of AcronymData associated with that acronym
     */
    public void setLfs(List<AcronymExpansion> lfs) {
        this.lfs = lfs;
    }
    
    /**
     *Inner class that contains data for each Acronym Expansion.
     *
     */
    public static class AcronymExpansion {
        /*
         * These data members are the local variables that will store
         * the AcronymExpansion's state.
         */

        /**
         * The long form of the acronym (spelled out version).
         */
        private String lf;

        /**
         * The relative frequency of usage in print, of this meaning
         * of the acronym.
         */
        private int freq;

        /**
         * The year the acronym was added to this database of
         * acronyms, or was originally termed.
         */
        private int since;

        /**
         * Constructor that initialises an AcronymExpansion object
         * from its parameters.
         */
        public AcronymExpansion(String lf,
                                int freq,
                                int since) {
            this.lf = lf;
            this.freq = freq;
            this.since = since;
        }

        /*
         * Getters and setters to access AcronymExpansion.
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
    }
}
