package nl.rgonline.homewizardlib;

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
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
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

    private HttpClient httpClient;
    private String connectionString;

    /**
     * Constructor.
     */
    HWConnection() {
        String host = HWConfig.HOST.getValue();
        int port = HWConfig.PORT.getValue();
        String password = HWConfig.PASSWORD.getValue();

		this.connectionString = String.format("http://%s:%d/%s", host, port, password);
        this.httpClient = new DefaultHttpClient();
	}

    /**
     * Perform a GET request to the HomeWizard and return the result.
     * @param urlParts One or more URL parts.
     * @return Parsed response.
     * @throws HWException On any IO or JSON error.
     */
    public JSONObject doGet(Object... urlParts) throws HWException {
        return doGetResp(true, urlParts);
    }

    /**
     * Perform a GET request to the HomeWizard and return the result.
     * @param returnResponse True to extract the 'response' object, false to return all data.
     * @param urlParts One or more URL parts.
     * @return Parsed response.
     * @throws HWException On any IO or JSON error.
     */
    public JSONObject doGetResp(boolean returnResponse, Object... urlParts) throws HWException {
        StringBuilder sb = new StringBuilder(connectionString);
        for (Object urlPart : urlParts) {
            sb.append(urlPart);
        }

        log.debug("Performing GET request: {}", sb);
        return performRequest(new HttpGet(sb.toString()), returnResponse);
    }

    /**
     * Perform a POST request to the HomeWizard and return the result.
     * @param urlParts One or more URL parts.
     * @return Parsed response.
     * @throws HWException On any IO or JSON error.
     */
    public JSONObject doPost(Object... urlParts) throws HWException {
        return doPostResp(true, urlParts);
    }

    /**
     * Perform a POST request to the HomeWizard and return the result.
     * @param returnResponse True to extract the 'response' object, false to return all data.
     * @param urlParts One or more URL parts.
     * @return Parsed response.
     * @throws HWException On any IO or JSON error.
     */
    public JSONObject doPostResp(boolean returnResponse, Object... urlParts) throws HWException {
        StringBuilder sb = new StringBuilder(connectionString);
        for (Object urlPart : urlParts) {
            sb.append(urlPart);
        }

        log.debug("Performing POST request: {}", sb);
        return performRequest(new HttpPost(sb.toString()), returnResponse);
    }
	
	/**
	 * Does a blocking call to the given URL.
     * @param request HTTP request to execute.
	 * @param returnResponse True to extract the 'response' object, false to return all data.
     * @return Returned data.
	 * @throws HWException On any IO error.
	 */
	private JSONObject performRequest(HttpRequestBase request, boolean returnResponse) throws HWException {

		//Get data
        String response;
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
		    response = httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            throw new HWException("HomeWizard IO error", e);
        }

        //Parse
        return parse(response.trim(), returnResponse);
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

            if (returnResponse) {
                retVal = retVal.getJSONObject("response");
            }
        } catch (JSONException e) {
            throw new HWException("Error parsing JSON", e);
        }

        return retVal;
    }

}