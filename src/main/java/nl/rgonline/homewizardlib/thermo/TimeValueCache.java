package nl.rgonline.homewizardlib.thermo;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cache for a list of {@link TimeValue TimeValues}.
 * @param <T> Type of the TimeValue values.
 * @author pdegeus
 */
@NoArgsConstructor
public class TimeValueCache<T extends Number> {

    @Getter @Setter
    private Long lastUpdate = null;

    @Getter
    private List<TimeValue<T>> data;

    /**
     * Constructor.
     * @param data Initial data.
     */
    public TimeValueCache(List<TimeValue<T>> data) {
        setData(data);
    }

    /**
     * Sets the cache data and updates the {@code lastUpdate} timestamp.
     * @param data Data to store.
     */
    public void setData(List<TimeValue<T>> data) {
        this.data = data;
        lastUpdate = System.currentTimeMillis();
    }

}
