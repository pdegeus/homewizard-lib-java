package nl.rgonline.homewizardlib.timers;

import lombok.Getter;

/**
 * Timer subject enum.
 * @author pdegeus
 */
public enum TimerSubject {

    /** Subject is a switch */
    SWITCH ("switch"),
    /** Subject is a scene */
    SCENE ("scene");

    @Getter
    private final String apiString;

    /**
     * Constructor.
     * @param apiString API string.
     */
    TimerSubject(String apiString) {
        this.apiString = apiString;
    }

    /**
     * Get the TimerSubject for the given API string.
     * @param apiString API type string.
     * @return TimerSubject for the given string.
     */
    public static TimerSubject forString(String apiString) {
        for (TimerSubject type : values()) {
            if (type.apiString.equals(apiString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown API string: " + apiString);
    }

}
