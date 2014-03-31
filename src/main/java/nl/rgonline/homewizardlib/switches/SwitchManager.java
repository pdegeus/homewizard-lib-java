package nl.rgonline.homewizardlib.switches;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.AbstractManager;
import nl.rgonline.homewizardlib.config.HWConfig;
import nl.rgonline.homewizardlib.connection.HWConnection;
import nl.rgonline.homewizardlib.connection.Request;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.util.HueColor;

import org.apache.commons.lang.BooleanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Switch manager.
 * @author pdegeus
 */
@Slf4j
public class SwitchManager extends AbstractManager<HWSwitch> {

    private final HWConnection connection;

    private Map<Integer, HWSwitch> switches;
    private volatile boolean initialized = false;

    /**
     * Constructor.
     * @param connection Connection to use.
     */
    public SwitchManager(HWConnection connection) {
        super(HWConfig.SWITCH_UPDATE_INTERVAL.getValue());
        this.connection = connection;
    }

    @Override
    public void init(boolean forceReload) throws HWException {
        if (!initialized || forceReload) {
            Request request = new Request("/swlist").setReturnResponse(false);
            JSONObject response = connection.request(request);

            // "response": [
            //   { "id": 5, "name": "TestDim", "type": "dimmer", "status": "off", "dimlevel": 0, "favorite": "yes" },
            //   { "id": 6, "name": "Bijkeuken", "type": "switch", "status": "off", "favorite": "no" },
            //   { "id": 7, "name": "TV kast", "type": "hue", "status": "on", "hue_id": 0, "light_id": 3,
            //     "color": {
            //       "hue": 76, "sat": 92, "bri": 79
            //     }, "favorite": "no"
            //   }
            // ]

            try {
                JSONArray jsonSwitches = response.getJSONArray("response");
                int numSwitches = jsonSwitches.length();

                switches = new HashMap<>();

                for (int i = 0; i < numSwitches; i++) {
                    JSONObject switchJson = jsonSwitches.getJSONObject(i);

                    int id = switchJson.getInt("id");
                    String name = switchJson.getString("name");
                    boolean isFavorite = BooleanUtils.toBoolean(switchJson.getString("favorite"));
                    boolean isOn = BooleanUtils.toBoolean(switchJson.getString("status"));
                    SwitchType type = SwitchType.forString(switchJson.getString("type"));

                    switch (type) {
                        case STANDARD:
                            HWSwitch theSwitch = new HWSwitch(connection, id, name, isFavorite, isOn);
                            switches.put(id, theSwitch);
                            break;
                        case DIMMER:
                            int dimlevel = switchJson.getInt("dimlevel");
                            HWDimmer dimmer = new HWDimmer(connection, id, name, isFavorite, isOn, dimlevel);
                            switches.put(id, dimmer);
                            break;
                        case HUE_BULB:
                            HueColor color = new HueColor(switchJson.getJSONObject("color"));
                            HWHueBulb hue = new HWHueBulb(connection, id, name, isFavorite, isOn, color);
                            switches.put(id, hue);
                            break;
                    }

                }
            } catch (JSONException e) {
                throw new HWException("Error initializing switches", e);
            }

            initialized = true;
        }
    }

    @Override
    protected void updateStatus() throws HWException {
        Request request = new Request(getUpdateInterval(), "/get-status");
        JSONObject response = connection.request(request);

        // "switches": [
        //   { "id": 5, "type": "dimmer", "status": "off", "dimlevel": 0 },
        //   { "id": 6, "type": "switch", "status": "off" },
        //   { "id": 7, "type": "hue", "status": "on", "color": { "hue": 76, "sat": 92, "bri": 79 } }
        // ]

        try {
            JSONArray jsonSwitches = response.getJSONArray("switches");

            int numSwitches = jsonSwitches.length();
            for (int i = 0; i < numSwitches; i++) {
                JSONObject switchJson = jsonSwitches.getJSONObject(i);

                int id = switchJson.getInt("id");
                boolean isOn = BooleanUtils.toBoolean(switchJson.getString("status"));

                HWSwitch curSwitch = switches.get(id);
                if (curSwitch == null) {
                    log.warn("Unknown switch ID: " + id);
                } else {
                    curSwitch.setOn(isOn);
                    curSwitch.updated();
                }
            }
        } catch (JSONException e) {
            throw new HWException("Error reloading switch statuses", e);
        }
    }

    @Override
    public Map<Integer, HWSwitch> getEntityMap() {
        return switches;
    }

}
