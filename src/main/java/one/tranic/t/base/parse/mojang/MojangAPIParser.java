package one.tranic.t.base.parse.mojang;

import com.google.gson.reflect.TypeToken;
import one.tranic.t.base.parse.json.JsonParser;
import one.tranic.t.base.parse.mojang.schemas.ProfileLookup;
import one.tranic.t.base.parse.uuid.UUIDParser;
import one.tranic.t.proxy.RequestWithProxyParser;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * MojangAPIParser is a utility class for interacting with the Mojang API to perform profile lookups.
 * It provides methods to retrieve Minecraft profile information by username, UUID, or a batch of usernames.
 * Connections are established using the RequestWithProxyParser class to support proxy configurations.
 */
public class MojangAPIParser {
    private static final String BASE_URL = "https://api.minecraftservices.com/minecraft/profile/lookup";
    private static final String NAME_ENDPOINT = "/name/";
    private static final String BULK_ENDPOINT = "/bulk/byname";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String POST_METHOD = "POST";

    /**
     * Retrieves the profile information for a user based on their username.
     *
     * @param name the username for which profile information is to be looked up
     * @return a {@link ProfileLookup} object containing the profile information
     * @throws IOException if an I/O error occurs during the profile lookup
     */
    public static ProfileLookup lookupProfile(String name) throws IOException {
        return lookupProfile(true, name);
    }

    /**
     * Looks up profile information for a given username from a remote server.
     *
     * @param useProxy whether to use a proxy connection for the request
     * @param name     the username to look up
     * @return the {@link ProfileLookup} containing the profile ID and name
     * @throws IOException if an I/O error occurs while performing the request or reading the response
     */
    public static ProfileLookup lookupProfile(boolean useProxy, String name) throws IOException {
        String url = BASE_URL + NAME_ENDPOINT + name;
        HttpURLConnection connection = createConnection(url, useProxy);
        return JsonParser.requestAndParse(connection, ProfileLookup.class);
    }

    /**
     * Retrieves the profile information associated with the specified UUID.
     *
     * @param uuid the {@link UUID} of the profile to lookup; must not be null
     * @return a {@link ProfileLookup} containing the profile information corresponding to the given UUID
     * @throws IOException if an I/O error occurs while making the request or processing the response
     */
    public static ProfileLookup lookupProfile(UUID uuid) throws IOException {
        return lookupProfile(true, uuid);
    }

    /**
     * Looks up a profile from the Mojang API using the specified UUID.
     *
     * @param useProxy a boolean indicating whether a proxy should be used for the connection
     * @param uuid     the UUID of the profile to lookup, must not be null
     * @return a {@link ProfileLookup} object containing the profile information
     * @throws IOException if an error occurs during the network request or while reading the response
     */
    public static ProfileLookup lookupProfile(boolean useProxy, UUID uuid) throws IOException {
        String url = BASE_URL + "/" + UUIDParser.removeDashes(uuid);
        HttpURLConnection connection = createConnection(url, useProxy);
        return JsonParser.requestAndParse(connection, ProfileLookup.class);
    }

    /**
     * Retrieves a list of profile lookups for the given player names.
     *
     * @param names the array of player names to look up profiles for; each name must be a valid string
     * @return a list of {@code ProfileLookup} records containing the ID and name of each requested profile
     * @throws IOException if an I/O error occurs during the HTTP request or response parsing
     */
    public static List<ProfileLookup> lookupProfiles(String... names) throws IOException {
        return lookupProfiles(true, names);
    }

    /**
     * Looks up profiles for the given list of names by sending an HTTP request to the Mojang API.
     *
     * @param useProxy a boolean indicating whether to use a proxy for the HTTP connection
     * @param names    a variable-length list of usernames to look up profiles for
     * @return a list of {@link ProfileLookup} representing the fetched profile information for each provided name
     * @throws IOException if an I/O error occurs while making the request or processing the response
     */
    public static List<ProfileLookup> lookupProfiles(boolean useProxy, String... names) throws IOException {
        String url = BASE_URL + BULK_ENDPOINT;
        HttpURLConnection connection = createConnection(url, useProxy);

        connection.setRequestMethod(POST_METHOD);
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        connection.setDoOutput(true);

        String jsonBody = JsonParser.toJson(names);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }

        Type listType = new TypeToken<List<ProfileLookup>>() {
        }.getType();
        return JsonParser.requestAndParse(connection, listType);
    }

    /**
     * Creates and returns an {@link HttpURLConnection} instance for the specified URL.
     * If the {@code useProxy} flag is set to {@code true}, a connection using a proxy is created.
     * Otherwise, a direct connection is instantiated.
     *
     * @param url      the URL to which the connection is to be established
     * @param useProxy a boolean flag indicating whether a proxy should be used for the connection
     * @return an instance of {@link HttpURLConnection} configured based on the provided parameters
     * @throws IOException if an I/O error occurs while creating the connection
     */
    private static HttpURLConnection createConnection(String url, boolean useProxy) throws IOException {
        return useProxy
                ? RequestWithProxyParser.openConnection(url)
                : (HttpURLConnection) new URL(url).openConnection(Proxy.NO_PROXY);
    }
}