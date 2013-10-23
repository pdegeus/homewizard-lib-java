package nl.rgonline.homewizardlib.cameras;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import nl.rgonline.homewizardlib.AbstractHwEntity;
import nl.rgonline.homewizardlib.connection.HWConnection;
import nl.rgonline.homewizardlib.connection.Request;
import nl.rgonline.homewizardlib.exceptions.HWException;
import nl.rgonline.homewizardlib.util.UrlUtil;

/**
 * Represents a camera in the HomeWizard system.
 * @author pdegeus
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HWCamera extends AbstractHwEntity {

    private String username;
    private String password;
    private String host;
    private int port;

    /**
     * Constructor.
     * @param connection Connection to use.
     * @param id Camera ID.
     * @param name Camera name.
     * @param username Camera username.
     * @param password Camera password.
     * @param host Camera host or IP.
     * @param port Camera port.
     */
    protected HWCamera(HWConnection connection, int id, String name, String username, String password, String host, int port) {
        super(connection, id, name, false);
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    @Override
    protected void saveInternal() throws HWException {
        // /cam/edit/<id>/<name>/<host>/<port>/<user>/<pass>
        Request request = new Request(
            "/cam/edit/", getId(), "/", UrlUtil.encode(getName()), "/", UrlUtil.encode(getHost()), "/",
            getPort(), "/", UrlUtil.encode(getUsername()), "/", UrlUtil.encode(getPassword())
        );
        getConnection().request(request);
    }

}
