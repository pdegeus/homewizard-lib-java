package nl.rgonline.homewizardlib.cameras;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.AbstractManager;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Camera manager.
 * @author pdegeus
 */
@Slf4j
public class CameraManager extends AbstractManager<HWCamera> {

    private final HWConnection connection;

    private Map<Integer, HWCamera> cameras;
    private volatile boolean initialized = false;

    /**
     * Constructor.
     * @param connection Connection to use.
     */
    public CameraManager(HWConnection connection) {
        this.connection = connection;
    }

    @Override
    public void init(boolean force) throws HWException {
        if (!initialized || force) {
            JSONObject response = connection.doGet("/get-sensors");

            // "cameras": [
            //   {"id": 0, "name": "Testcam", "username": "a", "password": "a", "ip": "192.168.88.244", "port": "80", "presets":[]}
            // ]

            try {
                JSONArray jsonCams = response.getJSONArray("cameras");
                int numCams = jsonCams.length();

                cameras = new HashMap<>();

                for (int i = 0; i < numCams; i++) {
                    JSONObject camJson = jsonCams.getJSONObject(i);

                    int id = camJson.getInt("id");
                    String name = camJson.getString("name");
                    String user = camJson.getString("username");
                    String pass = camJson.getString("password");
                    String host = camJson.getString("ip");
                    int port = JsonUtil.readInteger(camJson, "port");

                    HWCamera camera = new HWCamera(connection, id, name, user, pass, host, port);
                    cameras.put(id, camera);
                }
            } catch (JSONException e) {
                throw new HWException("Error initializing cameras", e);
            }

            initialized = true;
        }
    }

    @Override
    protected void updateStatus() throws HWException {
        //Nothing to do
    }

    @Override
    protected Map<Integer, HWCamera> getEntityMap() {
        return cameras;
    }

    @Override
    protected int getStatusUpdateInterval() {
        return -1;
    }

}
