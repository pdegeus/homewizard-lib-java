package nl.rgonline.homewizardlib.switches;

/**
 * Switch type enumeration.
 * @author pdegeus
 */
public enum SwitchType {

    /** Standard switch */
    STANDARD ("switch"),

    /** Standard switch with dimmer */
    DIMMER ("dimmer"),

    /** Philips Hue switch */
    HUE_BULB ("hue");

    private final String apiString;

    SwitchType(String apiString) {
        this.apiString = apiString;
    }

    /**
     * @return The API string for this type.
     */
    public String getApiString() {
        return apiString;
    }

    /**
     * Get the SwitchType for the given API string.
     * @param apiString API type string.
     * @return SwitchType for the given string.
     */
    public static SwitchType forString(String apiString) {
        for (SwitchType type : values()) {
            if (type.apiString.equals(apiString)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown API string: " + apiString);
    }

}
