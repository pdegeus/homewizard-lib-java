package nl.rgonline.homewizardlib.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Hue bulb color representation.
 * @author pdegeus
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HueColor {

    private int hue;
    private int saturation;
    private int brightness;

    /**
     * Constructor based on {@link JSONObject} containing color data from HomeWizard API.
     * @param jsonObject JSON object.
     * @throws JSONException On any JSON read error.
     */
    public HueColor(JSONObject jsonObject) throws JSONException {
        hue = jsonObject.getInt("hue");
        saturation = jsonObject.getInt("sat");
        brightness = jsonObject.getInt("bri");
    }

}
