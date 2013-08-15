package nl.rgonline.homewizardlib.timers;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * HomeWizard day-of-the-week enum.
 * @author pdegeus
 */
public enum Day {

    /** Sunday (0) */
    SUNDAY (0),
    /** Monday (1) */
    MONDAY (1),
    /** Tuesday (2) */
    TUESDAY (2),
    /** Wednesday (3) */
    WEDNESDAY (3),
    /** Thursday (4) */
    THURSDAY (4),
    /** Friday (5) */
    FRIDAY (5),
    /** Saturday (6) */
    SATURDAY (6);

    @Getter
    private final int apiNumber;

    /**
     * Constructor.
     * @param apiNumber API day number.
     */
    Day(int apiNumber) {
        this.apiNumber = apiNumber;
    }

    /**
     * Get the Day for the given API number.
     * @param apiNumber API day number.
     * @return Day for the given number.
     */
    public static Day forNumber(int apiNumber) {
        for (Day type : values()) {
            if (type.apiNumber == apiNumber) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown API number: " + apiNumber);
    }

    /**
     * Creates a {@link Set} of Days from the given JSONArray received from the HW API.
     * @param jsonArray API days array, containing day numbers.
     * @return Set of Days.
     * @throws JSONException On any JSON parse error.
     */
    public static Set<Day> readArray(JSONArray jsonArray) throws JSONException {
        Set<Day> days = new HashSet<>();
        for (int i=0; i < jsonArray.length(); i++) {
            int dayNum = jsonArray.getInt(i);
            if (dayNum != 7) { // 7 = execute once, don't add to days
                days.add(forNumber(dayNum));
            }
        }
        return days;
    }

}
