package nl.rgonline.homewizardlib.thermo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Generic timestamp/value data container, used to store temperature and humidity data.
 * @param <T> Type of the value.
 * @author pdegeus
 */
@Data
@AllArgsConstructor
public class TimeValue<T extends Number> {

    private Date timestamp;

    private T value;
    private T maxValue;

    /**
     * @return Single <i>or</i> minimum value.
     * @see #isSingleValue()
     * @see #isMinMaxValue()
     */
    public T getValue() {
        return value;
    }

    /**
     * @return True if this container contains a single value.
     */
    public boolean isSingleValue() {
        return !isMinMaxValue();
    }

    /**
     * @return True if this container contains a minimum and maximum value.
     */
    public boolean isMinMaxValue() {
        return maxValue != null;
    }

}
