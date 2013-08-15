package nl.rgonline.homewizardlib;

import lombok.Getter;

/**
 * Timer actions enum.
 * @author pdegeus
 */
public enum HWAction {

    /** Do nothing */
    NONE ("", -1),
    /** Turn on */
    ON ("on", 1),
    /** Turn off */
    OFF ("off", 0);

    @Getter
    private final String apiString;

    @Getter
    private final int apiNumber;

    /**
     * Constructor.
     * @param apiString Timer action API string.
     * @param apiNumber Scene switch action API number.
     */
    HWAction(String apiString, int apiNumber) {
        this.apiString = apiString;
        this.apiNumber = apiNumber;
    }

    /**
     * Get the TimerAction for the given API string.
     * @param apiString API action string.
     * @return TimerAction for the given string.
     */
    public static HWAction forString(String apiString) {
        for (HWAction type : values()) {
            if (type.apiString.equals(apiString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown API string: " + apiString);
    }

    /**
     * Get the TimerAction for the given API number.
     * @param apiNumber API action number.
     * @return TimerAction for the given number.
     */
    public static HWAction forNumber(int apiNumber) {
        for (HWAction type : values()) {
            if (type.apiNumber == apiNumber) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown API number: " + apiNumber);
    }

}
