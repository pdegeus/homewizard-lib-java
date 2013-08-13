package nl.rgonline.homewizardlib.sensors;

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

    private final String typeString;

    /**
     * Constructor.
     * @param typeString API type string.
     */
    SensorType(String typeString) {
        this.typeString = typeString;
    }

    /**
     * Get the SensorType for the given API string.
     * @param typeStr API type string.
     * @return SensorType for the given string.
     */
    public static SensorType forString(String typeStr) {
        for (SensorType type : SensorType.values()) {
            if (type.typeString.equals(typeStr)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type string: " + typeStr);
    }

}
