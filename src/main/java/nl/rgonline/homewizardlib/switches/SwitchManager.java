package nl.rgonline.homewizardlib.switches;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.AbstractManager;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.config.HWConfig;
import nl.rgonline.homewizardlib.exceptions.HWException;

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
        this.connection = connection;
    }

    @Override
    protected void init(boolean force) throws HWException {
        if (!initialized || force) {
            JSONObject response = connection.doGet("/get-sensors");

            try {
                JSONArray switchesJSON = response.getJSONArray("switches");
                int numSwitches = switchesJSON.length();

                switches = new HashMap<>();

                for (int i = 0; i < numSwitches; i++) {
                    JSONObject switchJSON = switchesJSON.getJSONObject(i);

                    int id = switchJSON.getInt("id");
                    String name = switchJSON.getString("name");
                    boolean isOn = switchJSON.getString("status").equals("on");

                    if (switchJSON.getString("dimmer").equals("no")) {
                        HWSwitch theSwitch = new HWSwitch(connection, id, name, isOn);
                        switches.put(id, theSwitch);
                    } else {
                        int dimlevel = switchJSON.getInt("dimlevel");
                        HWDimmer dimmer = new HWDimmer(connection, id, name, isOn, dimlevel);
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
        //   {"id": 0, "status": "off"},
        //   {"id": 1, "status": "off"}
        // ]

        try {
            JSONArray jsonSwitches = response.getJSONArray("switches");

            int numSwitches = jsonSwitches.length();
            for (int i = 0; i < numSwitches; i++) {
                JSONObject switchJson = jsonSwitches.getJSONObject(i);

                int id = switchJson.getInt("id");
                boolean isOn = switchJson.getString("status").equals("on");

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

    @Override
    protected int getStatusUpdateInterval() {
        return HWConfig.SWITCH_UPDATE_INTERVAL.getValue();
    }
}
