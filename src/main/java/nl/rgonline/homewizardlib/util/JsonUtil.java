package nl.rgonline.homewizardlib.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON read/write utility.
 * @author pdegeus
 */
public final class JsonUtil {

    /**
     * Read an Integer JSON value.
     * @param json JSON object to read from.
     * @param field Field to read from.
     * @return Integer value, or null.
     */
    public static Integer readInteger(JSONObject json, String field) {
        Integer result = null;
        try {
            String value = json.getString(field);
            if (value != null) {
                result = Integer.parseInt(value);
            }
        } catch (JSONException | NumberFormatException ignored) { }
        return result;
    }

    /**
     * Read an Double JSON value.
     * @param json JSON object to read from.
     * @param field Field to read from.
     * @return Double value, or null.
     */
    public static Double readDouble(JSONObject json, String field) {
        Double result = null;
        try {
            String value = json.getString(field);
            if (value != null) {
                result = Double.parseDouble(value);
            }
        } catch (JSONException | NumberFormatException ignored) { }
        return result;
    }

    /**
     * Read an optional String JSON value.
     * @param json JSON object to read from.
     * @param field Field to read from.
     * @param defaultValue Value to return when field value is missing.
     * @return Field value, or {@code defaultValue}.
     */
    public static String readString(JSONObject json, String field, String defaultValue) {
        try {
            return json.getString(field);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    private JsonUtil() {
    }
}
