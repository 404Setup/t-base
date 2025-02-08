package one.tranic.t.base.parse.mojang;

import com.google.gson.reflect.TypeToken;
import one.tranic.t.base.parse.json.JsonParser;
import one.tranic.t.base.parse.mojang.schemas.ProfileLookup;
import one.tranic.t.base.parse.proxy.RequestWithProxyParser;
import one.tranic.t.base.parse.uuid.UUIDParser;

import java.io.IOException;
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
        return JsonParser.requestAndParse(connection, ProfileLookup.class);
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
        return JsonParser.requestAndParse(connection, ProfileLookup.class);
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
        String jsonBody = JsonParser.gson().toJson(names);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        Type listType = new TypeToken<List<ProfileLookup>>() {
        }.getType();
        return JsonParser.requestAndParse(connection, listType);
    }
}