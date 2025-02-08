package one.tranic.t.base.parse.json;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

public class JsonParser {
    private static final Gson gson = new Gson();

    public static Gson gson() {
        return gson;
    }

    /**
     * Sends an HTTP request using the provided connection, reads the response stream, and parses it
     * into an object of the specified type.
     *
     * @param <T>        the type of the object to return after parsing
     * @param connection the {@link HttpURLConnection} instance used to send the request
     * @param typeOfT    the {@link Type} of the object to parse the response into
     * @return the parsed object of the specified type
     * @throws IOException if an error occurs while reading the response or closing resources
     */
    public static <T> T requestAndParse(HttpURLConnection connection, Type typeOfT) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            return gson.fromJson(reader, typeOfT);
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Sends a request using the given HttpURLConnection and parses the response into the specified class type.
     *
     * @param <T>        the type of the object to be returned
     * @param connection the HttpURLConnection instance used to perform the request
     * @param classOfT   the class of the object to parse the response into
     * @return an instance of the specified class type containing the parsed response
     * @throws IOException if an I/O error occurs while making the request or reading the response
     */
    @SuppressWarnings("all")
    public static <T> T requestAndParse(HttpURLConnection connection, Class<T> classOfT) throws IOException {
        return requestAndParse(connection, (Type) classOfT);
    }
}
