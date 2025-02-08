package one.tranic.t.base.parse.proxy;

import one.tranic.t.base.TBase;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.Future;

/**
 * A utility class for handling HTTP connections and streams with support for proxy configurations
 * and asynchronous operations.
 * <p>
 * This class provides methods to open URL connections and input streams synchronously or asynchronously,
 * applying proxy settings retrieved automatically from the environment or system configuration.
 */
public class RequestWithProxyParser {
    private static Proxy proxy = ProxyConfigReader.getProxy();

    /**
     * Sets a custom proxy for the RequestWithProxyParser class.
     *
     * @param proxy the Proxy instance to be set as the custom proxy. This proxy
     *              will be used for subsequent network requests within this class.
     */
    public static void setCustomProxy(Proxy proxy) {
        RequestWithProxyParser.proxy = proxy;
    }

    /**
     * Refreshes the proxy configuration used by the application.
     * <p>
     * This method retrieves the current proxy settings by invoking the `ProxyConfigReader.getProxy()` method
     * and updates the static `proxy` field with the newly retrieved proxy configuration.
     * It is useful in scenarios where the proxy settings might have changed dynamically and need to be reloaded.
     */
    public static void refreshProxy() {
        proxy = ProxyConfigReader.getProxy();
    }

    /**
     * Opens a connection to the specified URL asynchronously and returns a Future with the result.
     * <p>
     * The connection is established using proxy settings if applicable.
     *
     * @param url the URL to which the connection should be opened
     * @return a Future containing the established HttpURLConnection, or a RuntimeException if an error occurs
     * @throws IOException if an IO error occurs while attempting to open the connection
     */
    public static Future<HttpURLConnection> openConnectionAsync(URL url) throws IOException {
        return TBase.executor.submit(() -> {
            try {
                return openConnection(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Establishes an asynchronous connection to the specified URL.
     *
     * @param url the URL as a string to which the connection should be opened.
     * @return a Future representing the pending result of the HttpURLConnection.
     * @throws IOException if an error occurs while forming or opening the connection.
     */
    public static Future<HttpURLConnection> openConnectionAsync(String url) throws IOException {
        return openConnectionAsync(new URL(url));
    }

    /**
     * Opens a connection to the specified URL string with proxy settings applied.
     *
     * @param url the URL string to which a connection is to be opened
     * @return a HttpURLConnection object pointing to the specified resource
     * @throws IOException if an I/O error occurs while opening the connection
     */
    public static HttpURLConnection openConnection(String url) throws IOException {
        return openConnection(new URL(url));
    }

    /**
     * Opens a connection to the specified URL using the proxy configuration retrieved
     * from the system or environment settings.
     *
     * @param url the URL to which the connection needs to be established.
     * @return an instance of {@link HttpURLConnection} representing the connection to the specified URL.
     * @throws IOException if an I/O exception occurs while opening the connection.
     */
    public static HttpURLConnection openConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection(proxy);
    }

    /**
     * Opens a stream for reading from the specified {@code URL}, using a proxy configuration
     * retrieved via {@code ProxyConfigReader.getProxy()}.
     *
     * @param url the {@code URL} from which the stream should be opened
     * @return an {@code InputStream} to read from the given {@code URL}
     * @throws IOException if an I/O error occurs while opening the connection or stream
     */
    public static InputStream openStream(URL url) throws IOException {
        return url.openConnection(proxy).getInputStream();
    }

    /**
     * Opens an input stream to the resource referenced by the specified URL string.
     *
     * @param url the URL string pointing to the resource to be accessed
     * @return an InputStream to read data from the specified resource
     * @throws IOException if an I/O exception occurs while trying to open the stream
     */
    public static InputStream openStream(String url) throws IOException {
        return openStream(new URL(url));
    }

    /**
     * Opens a stream to a given URL asynchronously using an executor service.
     * <p>
     * The returned {@link Future} allows retrieving the resulting {@link InputStream}
     * when the operation completes. If an I/O error occurs during the stream opening,
     * the exception will be encapsulated in a {@link RuntimeException}.
     *
     * @param url the URL to open a stream to; must not be null
     * @return a {@link Future} representing the pending result of the operation, which
     * will yield an {@link InputStream} upon successful completion
     * @throws IOException if an error occurs while initializing the URL stream
     */
    public static Future<InputStream> openStreamAsync(URL url) throws IOException {
        return TBase.executor.submit(() -> {
            try {
                return openStream(url);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Asynchronously opens an input stream to the specified URL string.
     * This method submits a task to an executor for opening the stream
     * and returns a Future representing the pending result.
     *
     * @param url the string representation of the URL to open a stream from
     * @return a Future containing the InputStream associated with the specified URL
     * @throws IOException if an I/O error occurs while creating the URL or accessing its stream
     */
    public static Future<InputStream> openStreamAsync(String url) throws IOException {
        return openStreamAsync(new URL(url));
    }
}
