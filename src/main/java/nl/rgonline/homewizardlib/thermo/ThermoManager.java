package nl.rgonline.homewizardlib.thermo;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.AbstractManager;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.config.HWConfig;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.util.JsonUtil;

import org.apache.commons.lang.BooleanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Switch manager.
 * @author pdegeus
 */
@Slf4j
public class ThermoManager extends AbstractManager<HWThermometer> {

    private final HWConnection connection;

    private Map<Integer, HWThermometer> thermos;
    private volatile boolean initialized = false;

    /**
     * Constructor.
     * @param connection Connection to use.
     */
    public ThermoManager(HWConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void init(boolean force) throws HWException {
        if (!initialized || force) {
            JSONObject response = connection.doGet("/get-sensors");

            // "thermometers": [
            //   { "id": 0, "hu": null, "name": "Binnen", "te": null, "favorite": "no", "channel": 2 },
            //   { "id": 1, "hu": 72,   "name": "Buiten", "te": 15.5, "favorite": "no", "channel": 1,
            //     "te+t": "12:53", "te+": 21.4, "te-": 13, "te-t": "06:39",
            //     "hu+t": "07:24", "hu+": 78,   "hu-": 60, "hu-t": "13:11"
            //   }
            // ]

            try {
                JSONArray jsonThermos = response.getJSONArray("thermometers");
                int numThermos = jsonThermos.length();

                thermos = new HashMap<>();

                for (int i = 0; i < numThermos; i++) {
                    JSONObject thermoJson = jsonThermos.getJSONObject(i);

                    int id = thermoJson.getInt("id");
                    String name = thermoJson.getString("name");
                    boolean isFavorite = BooleanUtils.toBoolean(thermoJson.getString("favorite"));
                    int channel = thermoJson.getInt("channel");

                    Integer humidity = JsonUtil.readInteger(thermoJson, "hu");
                    Double temperature = JsonUtil.readDouble(thermoJson, "te");

                    HWThermometer thermo = new HWThermometer(connection, id, name, isFavorite, channel, humidity, temperature);
                    thermos.put(id, thermo);
                }
            } catch (JSONException e) {
                throw new HWException("Error initializing thermometers", e);
            }

            initialized = true;
        }
    }

    @Override
    protected void updateStatus() throws HWException {
        JSONObject response = connection.doGet("/get-status");

        // "thermometers": [
        //   { "id": 0, "te": null, "hu": null },
        //   { "id": 1, "te": 15.5, "hu": 72 }
        // ]

        try {
            JSONArray jsonThermos = response.getJSONArray("thermometers");

            int numThermos = jsonThermos.length();
            for (int i = 0; i < numThermos; i++) {
                JSONObject thermoJson = jsonThermos.getJSONObject(i);

                int id = thermoJson.getInt("id");
                Integer humidity = JsonUtil.readInteger(thermoJson, "hu");
                Double temperature = JsonUtil.readDouble(thermoJson, "te");

                HWThermometer curThermo = thermos.get(id);
                if (curThermo == null) {
                    log.warn("Unknown thermometer ID: " + id);
                } else {
                    curThermo.setHumidity(humidity);
                    curThermo.setTemperature(temperature);
                    curThermo.updated();
                }
            }
        } catch (JSONException e) {
            throw new HWException("Error reloading thermometer data", e);
        }
    }

    @Override
    public Map<Integer, HWThermometer> getEntityMap() {
        return thermos;
    }

    @Override
    protected int getStatusUpdateInterval() {
        return HWConfig.THERMO_UPDATE_INTERVAL.getValue();
    }
}
