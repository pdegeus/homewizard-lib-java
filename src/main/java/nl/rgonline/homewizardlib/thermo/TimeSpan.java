package nl.rgonline.homewizardlib.thermo;

import lombok.Getter;

/**
 * HomeWizard time span enum.
 * @author pdegeus
 */
public enum TimeSpan {

    /** Timespan of one day (24h) */
    DAY ("day"),
    /** One week */
    WEEK ("week"),
    /** One month */
    MONTH ("month"),
    /** One year */
    YEAR ("year");

    @Getter
    private final String apiString;

    /**
     * Constructor.
     * @param apiString API string.
     */
    TimeSpan(String apiString) {
        this.apiString = apiString;
    }

}
