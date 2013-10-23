package nl.rgonline.homewizardlib.connection;

import java.io.IOException;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import nl.rgonline.homewizardlib.config.HWConfig;
import nl.rgonline.homewizardlib.exceptions.HWException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class will be used for setting up a connection to the HomeWizard and to retrieve data from it.
 * @author Ruud Greven
 * @author pdegeus
 */
@Slf4j
@ToString
public final class HWConnection {

    private static final PoolingClientConnectionManager CONNECTION_MANAGER;

    static {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

        CONNECTION_MANAGER = new PoolingClientConnectionManager(schemeRegistry);
        CONNECTION_MANAGER.setMaxTotal(HWConfig.MAX_TOTAL_CONNECTIONS.getValue());
        CONNECTION_MANAGER.setDefaultMaxPerRoute(HWConfig.MAX_ROUTE_CONNECTIONS.getValue());
    }

    private HttpClient httpClient;
    private ResponseCache cache;
    private String connectionString;

    /**
     * Constructor.
     * @param host Host to connect to.
     * @param port Port to connect to.
     * @param password Password to use.
     */
    public HWConnection(String host, int port, String password) {
		this.connectionString = String.format("http://%s:%d/%s", host, port, password);
        this.httpClient = new DefaultHttpClient(CONNECTION_MANAGER);
        this.cache = new ResponseCache();

        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, HWConfig.CONNECT_TIMEOUT.getValue());
        HttpConnectionParams.setSoTimeout(params, HWConfig.READ_TIMEOUT.getValue());
    }

    /**
     * Perform a simple GET request to the HomeWizard without caching.
     * @param urlParts String parts to construct URL from.
     * @throws HWException On any IO or JSON error.
     */
    public void request(Object... urlParts) throws HWException {
        request(new Request(urlParts));
    }

    /**
     * Perform a request to the HomeWizard and return the result.
     * @param request Request to perform.
     * @return Parsed response.
     * @throws HWException On any IO or JSON error.
     */
    public JSONObject request(Request request) throws HWException {
        StringBuilder sb = new StringBuilder(connectionString);
        for (Object urlPart : request.getUrlParts()) {
            sb.append(urlPart);
        }

        String url = sb.toString();
        long maxAge = request.getMaxAge();

        //Get from cache if applicable
        String response;
        if (request.isCacheable() && cache.hasResponse(url, maxAge)) {
            log.debug("Using cached response for {} {}", request.getMethod(), url);
            response = cache.get(url);
        } else {
            log.debug("Performing {} request: {}", request.getMethod(), url);

            HttpRequestBase req;
            switch (request.getMethod()) {
                case GET:
                    req = new HttpGet(url);
                    break;
                case POST:
                    req = new HttpPost(url);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown method: " + request.getMethod());
            }

            //Get data
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {
                response = httpClient.execute(req, responseHandler).trim();
            } catch (IOException e) {
                throw new HWException("HomeWizard IO error", e);
            }

            //Add to cache
            if (request.isCacheable()) {
                cache.add(url, response);
            }
        }

        //Parse
        return parse(response, request.isReturnResponse());
    }

    /**
     * Reads the server response and returns the parsed JSON data.
     * @param jsonString The response from the server.
     * @param returnResponse True to extract the 'response' object, false to return all data.
     * @return A JSONObject representing the response.
     * @throws HWException On any IO or JSON error.
     */
    private JSONObject parse(String jsonString, boolean returnResponse) throws HWException {
        JSONObject retVal;
        try {
            retVal = new JSONObject(jsonString);

            //Check status
            if (!retVal.getString("status").equals("ok")) {
                throw new HWException("HomeWizard returns not an OK status: " + jsonString);
            }

            if (returnResponse && retVal.has("response")) {
                retVal = retVal.getJSONObject("response");
            }
        } catch (JSONException e) {
            throw new HWException("Error parsing JSON:\n" + jsonString, e);
        }

        return retVal;
    }

}
