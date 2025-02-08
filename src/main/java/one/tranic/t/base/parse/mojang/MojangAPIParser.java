package one.tranic.t.base.parse.mojang;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import one.tranic.t.base.parse.mojang.schemas.ProfileLookup;
import one.tranic.t.base.parse.proxy.RequestWithProxyParser;
import one.tranic.t.base.parse.uuid.UUIDParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * MojangAPIParser is a utility class for interacting with the Mojang API to perform profile lookups.
 * It provides methods to retrieve Minecraft profile information by username, UUID, or a batch of usernames.
 * Connections are established using the RequestWithProxyParser class to support proxy configurations.
 */
public class MojangAPIParser {
    private static final Gson gson = new Gson();
    private static final String BASE_URL = "https://api.minecraftservices.com/minecraft/profile/lookup";

    /**
     * Looks up a Minecraft profile by its username using the Mojang API.
     *
     * @param name the username of the Minecraft profile to be looked up
     * @return a {@code ProfileLookup} object containing the profile's ID and username
     * @throws IOException if an I/O error occurs while connecting to the Mojang API or parsing the response
     */
    public static ProfileLookup lookupProfile(String name) throws IOException {
        String url = BASE_URL + "/name/" + name;
        HttpURLConnection connection = RequestWithProxyParser.openConnection(url);
        return requestAndParse(connection, ProfileLookup.class);
    }

    /**
     * Retrieves the Minecraft profile associated with the given UUID by performing a lookup request to the Mojang API.
     *
     * @param uuid the unique identifier of the Minecraft account to look up, must not be null
     * @return a {@link ProfileLookup} record containing the profile information, including the UUID and username
     * @throws IOException if an I/O error occurs while making the request or parsing the response
     */
    public static ProfileLookup lookupProfile(UUID uuid) throws IOException {
        String url = BASE_URL + "/" + UUIDParser.removeDashes(uuid);
        HttpURLConnection connection = RequestWithProxyParser.openConnection(url);
        return requestAndParse(connection, ProfileLookup.class);
    }

    /**
     * Retrieves a list of Minecraft profiles by performing a bulk lookup using an array of usernames.
     *
     * @param names an array of usernames to be looked up; each name represents a Minecraft user's profile
     * @return a list of {@code ProfileLookup} records containing the id and name of the queried profiles
     * @throws IOException if an I/O error occurs during the HTTP request or response handling
     */
    public static List<ProfileLookup> lookupProfile(String... names) throws IOException {
        String url = BASE_URL + "/bulk/byname";
        HttpURLConnection connection = RequestWithProxyParser.openConnection(url);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        String jsonBody = gson.toJson(names);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        Type listType = new TypeToken<List<ProfileLookup>>() {
        }.getType();
        return requestAndParse(connection, listType);
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
    private static <T> T requestAndParse(HttpURLConnection connection, Type typeOfT) throws IOException {
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
    private static <T> T requestAndParse(HttpURLConnection connection, Class<T> classOfT) throws IOException {
        return requestAndParse(connection, (Type) classOfT);
    }
}