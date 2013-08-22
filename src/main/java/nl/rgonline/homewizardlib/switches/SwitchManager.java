package nl.rgonline.homewizardlib.switches;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.AbstractManager;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.config.HWConfig;
import nl.rgonline.homewizardlib.exceptions.HWException;

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
            JSONObject response = connection.doGetResp(false, "/swlist");

            // "response": [
            //   { "id": 0, "status": "on",  "name": "Woonkamer 1", "dimmer": "no", "favorite": "yes", "type": "switch" },
            //   { "id": 1, "status": "off", "name": "Woonkamer 2", "dimmer": "no", "favorite": "no",  "type": "switch" }
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

                    if (switchJson.getString("dimmer").equals("no")) {
                        HWSwitch theSwitch = new HWSwitch(connection, id, name, isFavorite, isOn);
                        switches.put(id, theSwitch);
                    } else {
                        int dimlevel = switchJson.getInt("dimlevel");
                        HWDimmer dimmer = new HWDimmer(connection, id, name, isFavorite, isOn, dimlevel);
                        switches.put(id, dimmer);
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
        JSONObject response = connection.doGet("/get-status");

        // "switches": [
        //   {"id": 0, "status": "on" },
        //   {"id": 1, "status": "off"}
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
