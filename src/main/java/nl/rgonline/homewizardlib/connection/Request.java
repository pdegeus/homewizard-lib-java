package nl.rgonline.homewizardlib.connection;

import lombok.Getter;

/**
 * HTTP request data object, used by {@link HWConnection}.
 * @author pdegeus
 */
public class Request {

    /**
     * Request method enum.
     */
    @SuppressWarnings("JavaDoc")
    public enum Method {
        GET, POST
    }


    @Getter
    private final Method method;

    @Getter
    private final long maxAge;

    @Getter
    private final Object[] urlParts;

    @Getter
    private boolean returnResponse = true;

    /**
     * Constructor defaulting to a GET request without caching.
     * @param urlParts String parts to construct URL from.
     */
    protected Request(Object... urlParts) {
        this(Method.GET, urlParts);
    }

    /**
     * Constructor defaulting to a GET request without caching.
     * @param urlPart First part of the URL.
     * @param urlParts String parts to construct URL from.
     */
    public Request(String urlPart, Object... urlParts) {
        this(Method.GET, combine(urlPart, urlParts));
    }

    /**
     * Constructor defaulting to a GET request.
     * @param maxAge Maximum response age in milliseconds.
     * @param urlParts String parts to construct URL from.
     */
    public Request(long maxAge, Object... urlParts) {
        this(Method.GET, maxAge, urlParts);
    }

    /**
     * Constructor defaulting to a request without caching.
     * @param method Request method.
     * @param urlParts String parts to construct URL from.
     */
    public Request(Method method, Object... urlParts) {
        this(method, 0, urlParts);
    }

    /**
     * Constructor with all options.
     * @param method Request method.
     * @param maxAge Maximum response age in milliseconds.
     * @param urlParts String parts to construct URL from.
     */
    public Request(Method method, long maxAge, Object... urlParts) {
        this.method = method;
        this.maxAge = maxAge;
        this.urlParts = urlParts;
    }

    /**
     * Set whether or not to return the 'response' object.
     * @param returnResponse True to extract the 'response' object, false to return all data.
     * @return this for chaining.
     */
    public Request setReturnResponse(boolean returnResponse) {
        this.returnResponse = returnResponse;
        return this;
    }

    /**
     * @return True if this request is cacheable.
     */
    public boolean isCacheable() {
        return maxAge > 0;
    }

    private static Object[] combine(String urlPart, Object... urlParts) {
        Object[] result = new Object[urlParts.length + 1];
        result[0] = urlPart;
        System.arraycopy(urlParts, 0, result, 1, urlParts.length);
        return result;
    }

}
