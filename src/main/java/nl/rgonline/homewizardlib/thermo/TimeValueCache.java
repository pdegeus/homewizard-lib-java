package nl.rgonline.homewizardlib.thermo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * FIXME: JavaDoc
 * @author pdegeus
 */
public class TimeValueCache<T extends Number> {

    @Getter @Setter
    private Long lastUpdate = null;

    @Getter
    private List<TimeValue<T>> data;

    public TimeValueCache() {
    }

    public TimeValueCache(List<TimeValue<T>> data) {
        setData(data);
    }

    public void setData(List<TimeValue<T>> data) {
        this.data = data;
        lastUpdate = System.currentTimeMillis();
    }


}
