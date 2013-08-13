package nl.rgonline.homewizardlib.sensors;

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
 * Sensor manager.
 * @author pdegeus
 */
@Slf4j
public class SensorManager extends AbstractManager<HWSensor> {

    private final HWConnection connection;

    private Map<Integer, HWSensor> sensors;
    private volatile boolean initialized = false;

    /**
     * Constructor.
     * @param connection Connection to use.
     */
    public SensorManager(HWConnection connection) {
        this.connection = connection;
    }

    @Override
    protected void init(boolean force) throws HWException {
        if (!initialized || force) {
            JSONObject response = connection.doGet("/get-sensors");

            // "kakusensors": [
            //   {"id": 0, "name": "Voordeur", "status": null, "type": "doorbell", "favorite": "no", "timestamp": "00:00"},
            //   {"id": 1, "name": "Voordeur", "status": "no", "type": "contact", "favorite": "no", "timestamp": "10:22"},
            //   {"id": 2, "name": "Achterdeur", "status": "yes", "type": "contact", "favorite": "no", "timestamp": "10:22"},
            //   {"id": 3, "name": "Rookmelder", "status": null, "type": "smoke", "favorite": "no", "timestamp": "00:00"}
            // ]

            try {
                JSONArray jsonSensors = response.getJSONArray("kakusensors");
                int numSensors = jsonSensors.length();

                sensors = new HashMap<>();

                for (int i = 0; i < numSensors; i++) {
                    JSONObject sensorJson = jsonSensors.getJSONObject(i);

                    int id = sensorJson.getInt("id");
                    String name = sensorJson.getString("name");
                    SensorType type = SensorType.forString(sensorJson.getString("type"));
                    boolean isFavorite = BooleanUtils.toBoolean(sensorJson.getString("favorite"));

                    String status = sensorJson.getString("status");
                    boolean isOn = BooleanUtils.toBoolean(status);
                    String lastEvent = (status == null) ? null : sensorJson.getString("timestamp");

                    HWSensor sensor = new HWSensor(connection, id, name, type, lastEvent, isFavorite, isOn);
                    sensors.put(id, sensor);
                }
            } catch (JSONException e) {
                throw new HWException("Error initializing sensors", e);
            }

            initialized = true;
        }
    }

    @Override
    protected void updateStatus() throws HWException {
        JSONObject response = connection.doGet("/get-status");

        // "kakusensors": [
        //   {"id": 0, "status": null, "timestamp": "00:00"},
        //   {"id": 1, "status": "no", "timestamp": "10:22"}
        // ]

        try {
            JSONArray jsonSensors = response.getJSONArray("kakusensors");

            int numSensors = jsonSensors.length();
            for (int i = 0; i < numSensors; i++) {
                JSONObject sensorJson = jsonSensors.getJSONObject(i);

                int id = sensorJson.getInt("id");
                String status = sensorJson.getString("status");
                boolean isOn = BooleanUtils.toBoolean(status);
                String lastEvent = (status == null) ? null : sensorJson.getString("timestamp");

                HWSensor sensor = sensors.get(id);
                if (sensor == null) {
                    log.warn("Unknown sensor ID: " + id);
                } else {
                    sensor.setOn(isOn);
                    sensor.setLastEventTime(lastEvent);
                    sensor.updated();
                }
            }
        } catch (JSONException e) {
            throw new HWException("Error reloading sensor statuses", e);
        }
    }

    @Override
    protected Map<Integer, HWSensor> getEntityMap() {
        return sensors;
    }

    @Override
    protected int getStatusUpdateInterval() {
        return HWConfig.SENSOR_UPDATE_INTERVAL.getValue();
    }

}
