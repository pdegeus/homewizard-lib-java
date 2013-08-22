package nl.rgonline.homewizardlib.scenes;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.AbstractManager;
import nl.rgonline.homewizardlib.HWConnection;
import nl.rgonline.homewizardlib.exceptions.HWException;

import org.apache.commons.lang.BooleanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Scene manager.
 * @author pdegeus
 */
@Slf4j
public class SceneManager extends AbstractManager<HWScene> {

    private final HWConnection connection;

    private Map<Integer, HWScene> scenes;
    private volatile boolean initialized = false;

    /**
     * Constructor.
     * @param connection Connection to use.
     */
    public SceneManager(HWConnection connection) {
        super(-1);
        this.connection = connection;
    }

    @Override
    public void init(boolean forceReload) throws HWException {
        if (!initialized || forceReload) {
            JSONObject response = connection.doGetResp(false, "/gplist");

            // "response": [
            //   {"id": 0, "name": "Alle lampen", "favorite": "no"},
            //   {"id": 1, "name": "TestScene", "favorite": "no", "camera": {"id": 0, "preset": -1}}
            // ]

            try {
                JSONArray jsonScenes = response.getJSONArray("response");
                int numScenes = jsonScenes.length();

                scenes = new HashMap<>();

                for (int i = 0; i < numScenes; i++) {
                    JSONObject sceneJson = jsonScenes.getJSONObject(i);

                    int id = sceneJson.getInt("id");
                    String name = sceneJson.getString("name");
                    boolean isFavorite = BooleanUtils.toBoolean(sceneJson.getString("favorite"));

                    HWScene scene = new HWScene(connection, id, name, isFavorite);
                    scenes.put(id, scene);
                }
            } catch (JSONException e) {
                throw new HWException("Error initializing scenes", e);
            }

            initialized = true;
        }
    }

    @Override
    protected void updateStatus() throws HWException {
        //Updates are performed per scene
    }

    @Override
    protected Map<Integer, HWScene> getEntityMap() {
        return scenes;
    }

}
