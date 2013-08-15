package nl.rgonline.homewizardlib.sensors;

import lombok.Getter;

/**
 * HomeWizard sensor types enum.
 * @author pdegeus
 */
public enum SensorType {

    /** Contact sensor */
    CONTACT ("contact"),
    /** Smoke detector */
    SMOKE ("smoke"),
    /** Doorbell */
    DOORBELL ("doorbell"),
    /** Motion detector */
    MOTION ("motion");

    @Getter
    private final String apiString;

    /**
     * Constructor.
     * @param apiString API type string.
     */
    SensorType(String apiString) {
        this.apiString = apiString;
    }

    /**
     * Get the SensorType for the given API string.
     * @param apiString API type string.
     * @return SensorType for the given string.
     */
    public static SensorType forString(String apiString) {
        for (SensorType type : values()) {
            if (type.apiString.equals(apiString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown API string: " + apiString);
    }

}
