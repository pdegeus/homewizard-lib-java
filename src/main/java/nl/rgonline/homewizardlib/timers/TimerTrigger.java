package nl.rgonline.homewizardlib.timers;

import lombok.Getter;

/**
 * Timer trigger types enum.
 * @author pdegeus
 */
public enum TimerTrigger {

    /** Trigger relative to sunrise */
    SUNRISE ("sunrise"),
    /** Trigger relative to Sunset */
    SUNSET ("sunset"),
    /** Trigger at a fixed time */
    TIME ("time");

    @Getter
    private final String apiString;

    /**
     * Constructor.
     * @param apiString API string.
     */
    TimerTrigger(String apiString) {
        this.apiString = apiString;
    }

    /**
     * Get the TimerTrigger for the given API string.
     * @param apiString API type string.
     * @return TimerTrigger for the given string.
     */
    public static TimerTrigger forString(String apiString) {
        for (TimerTrigger type : values()) {
            if (type.apiString.equals(apiString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown API string: " + apiString);
    }

}
