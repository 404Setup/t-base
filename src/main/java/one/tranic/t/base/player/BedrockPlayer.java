package one.tranic.t.base.player;

import one.tranic.t.base.parse.uuid.UUIDParser;
import one.tranic.t.utils.Reflect;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.cumulus.form.Form;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.player.FloodgatePlayerImpl;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.connection.GeyserConnection;
import org.geysermc.geyser.session.GeyserSession;

import java.util.UUID;

public class BedrockPlayer {
    private static final boolean geyser = Reflect.hasClass("org.geysermc.geyser.api.GeyserApi");
    private static final boolean floodgate = Reflect.hasClass("org.geysermc.floodgate.api.FloodgateApi");

    /**
     * Determines if a player, identified by their UUID, is a Bedrock player.
     * <p>
     * This method uses either the Floodgate or Geyser API to make the determination,
     * depending on their availability, and falls back to checking if the player is
     * a Floodgate player.
     *
     * @param uuid the unique identifier of the player
     * @return true if the player is a Bedrock player, false otherwise
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        if (floodgate)
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        if (geyser) return GeyserApi.api().isBedrockPlayer(uuid);
        return isFloodgatePlayer(uuid);
    }

    /**
     * Determines whether the provided UUID string corresponds to a Floodgate player.
     * This method checks if the UUID string, with dashes removed, starts with a specific prefix
     * associated with Floodgate players.
     *
     * @param uuid the UUID string to be checked, expected in the standard UUID format with dashes
     * @return true if the UUID belongs to a Floodgate player, false otherwise
     */
    public static boolean isFloodgatePlayer(String uuid) {
        final String FLOODGATE_UUID_PREFIX = "0000000000000000";

        String str = UUIDParser.removeDashes(uuid);
        return str.startsWith(FLOODGATE_UUID_PREFIX);
    }

    /**
     * Determines whether the player associated with the given UUID is a Floodgate player.
     * This method converts the UUID to a string and checks if it matches the Floodgate criteria.
     *
     * @param uuid the unique identifier (UUID) of the player to be checked
     * @return true if the player is identified as a Floodgate player; false otherwise
     */
    public static boolean isFloodgatePlayer(UUID uuid) {
        return isFloodgatePlayer(uuid.toString());
    }

    /**
     * Gets the platform of the player associated with the given UUID.
     * <p>
     * This method determines the player's platform using either the Floodgate or Geyser API,
     * based on their availability. If neither API recognizes the UUID, it returns "Java Edition".
     *
     * @param uuid the UUID of the player whose platform is being determined
     * @return the platform of the player as a string, or "Java Edition" if the platform cannot be determined
     */
    public static @NonNull String getPlatform(UUID uuid) {
        if (floodgate) {
            FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(uuid);
            if (player != null) return player.getDeviceOs().toString();
        } else if (geyser) {
            @Nullable GeyserConnection player = GeyserApi.api().connectionByUuid(uuid);
            if (player != null) return player.platform().toString();
        }
        return "Java Edition";
    }

    /**
     * Retrieves the subscribe ID for a player identified by their UUID.
     * This method uses the Floodgate API to check if the given player is a Bedrock player
     * and returns the corresponding subscribe ID. If the player is not a Bedrock player,
     * or the Floodgate API is unavailable, it returns -1.
     *
     * @param uuid the unique identifier (UUID) of the player whose subscribe ID is to be retrieved
     * @return the subscribe ID of the player if available; otherwise, -1
     */
    public static int getSubscribeId(UUID uuid) {
        if (floodgate) {
            FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(uuid);
            if (player != null) return ((FloodgatePlayerImpl) player).toBedrockData().getSubscribeId();
        }
        return -1;
    }

    /**
     * Retrieves the device ID of a player identified by their UUID.
     * This method uses the Geyser API to fetch the device ID if available.
     * If the player is not associated with Geyser or the API does not provide data, it returns null.
     *
     * @param uuid the unique identifier (UUID) of the player whose device ID is to be retrieved
     * @return the device ID of the player if available, or null if the player is not associated with Geyser or the data is unavailable
     */
    public static @Nullable String getDeviceId(UUID uuid) {
        if (geyser) {
            @Nullable GeyserConnection player = GeyserApi.api().connectionByUuid(uuid);
            if (player != null)
                return ((GeyserSession) player).getClientData().getDeviceId();
        }
        return null;
    }

    /**
     * Retrieves the device model of a player identified by their UUID.
     * This method uses the Geyser API to obtain the device model if the player is a Bedrock player.
     * If the Geyser API is unavailable or the player is not recognized, the method will return null.
     *
     * @param uuid the unique identifier of the player whose device model is being retrieved
     * @return the device model as a string if available, or {@code null} if the player is not recognized or the device model cannot be determined
     */
    public static @Nullable String getDeviceModel(UUID uuid) {
        if (geyser) {
            @Nullable GeyserConnection player = GeyserApi.api().connectionByUuid(uuid);
            if (player != null) return ((GeyserSession) player).getClientData().getDeviceModel();
        }
        return null;
    }

    /**
     * Retrieves the input mode of a player identified by their UUID.
     * This method uses the Geyser or Floodgate API (if available) to determine the player's input mode.
     * If neither API provides information about the player's input mode, the method returns null.
     *
     * @param uuid the unique identifier of the player whose input mode is to be retrieved
     * @return the input mode of the player as a string if available; otherwise, null
     */
    public static @Nullable String getInputMode(UUID uuid) {
        if (geyser) {
            @Nullable GeyserConnection player = GeyserApi.api().connectionByUuid(uuid);
            if (player != null) return player.inputMode().name();
        }
        if (floodgate) {
            FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(uuid);
            if (player != null) return player.getInputMode().name();
        }
        return null;
    }

    /**
     * Retrieves the client random ID of a player identified by their UUID.
     * This method uses the Geyser API to fetch the client random ID for Bedrock players if available.
     * If the player is not found or the Geyser API is not accessible, it returns -1.
     *
     * @param uuid the unique identifier of the player whose client random ID is being retrieved
     * @return the client random ID of the player if available; otherwise, -1
     */
    public static long getClientRandomId(UUID uuid) {
        if (geyser) {
            @Nullable GeyserConnection player = GeyserApi.api().connectionByUuid(uuid);
            if (player != null) return ((GeyserSession) player).getClientData().getClientRandomId();
        }
        return -1;
    }

    /**
     * Retrieves the Bedrock player object associated with the given UUID.
     * This method uses the Floodgate or Geyser API to determine the player,
     * depending on their availability. If neither API is available or
     * the UUID does not correspond to a Bedrock player, it returns null.
     *
     * @param uuid the unique identifier of the player
     * @return the Bedrock player object if available; otherwise, null
     */
    public static @Nullable Object getBedrockPlayer(UUID uuid) {
        return floodgate ? FloodgateApi.getInstance().getPlayer(uuid) :
                geyser ? GeyserApi.api().connectionByUuid(uuid) : null;
    }

    /**
     * Retrieves the XUID (Xbox Unique Identifier) of a player identified by their UUID.
     * <p>
     * This method checks for the availability of the Floodgate or Geyser API and
     * uses the corresponding integration to obtain the player's XUID.
     *
     * @param uuid the unique identifier (UUID) of the player whose XUID is to be retrieved
     * @return the XUID of the player if available; otherwise, {@code null}
     */
    public static @Nullable String getXUID(UUID uuid) {
        if (floodgate) {
            FloodgatePlayer player = FloodgateApi.getInstance().getPlayer(uuid);
            if (player != null) return player.getXuid();
        } else if (geyser) {
            @Nullable GeyserConnection player = GeyserApi.api().connectionByUuid(uuid);
            if (player != null) return player.xuid();
        }
        return null;
    }

    /**
     * Sends a form to a player identified by their UUID.
     * <p>
     * This method leverages either the Floodgate or Geyser API, depending on their availability, to deliver the form.
     * <p>
     * If neither API is available, the method will return false.
     *
     * @param uuid the unique identifier of the player to whom the form should be sent
     * @param form the form object to be sent to the player
     * @return true if the form was successfully sent using Floodgate or Geyser; false otherwise
     */
    public static boolean sendForm(UUID uuid, Form form) {
        if (floodgate)
            return FloodgateApi.getInstance().sendForm(uuid, form);
        if (geyser) return GeyserApi.api().sendForm(uuid, form);
        return false;
    }

    /**
     * Retrieves the ping of a player identified by their UUID.
     * <p>
     * This method uses the Geyser API to determine the ping of a Bedrock player if available.
     * If the player is not a Bedrock player or the Geyser API is not available, it returns -1.
     *
     * @param uuid the unique identifier of the player whose ping is to be retrieved
     * @return the ping of the player, or -1 if unable to determine
     */
    public static long getPing(UUID uuid) {
        if (geyser) {
            @Nullable GeyserConnection geyserPlayer = GeyserApi.api().connectionByUuid(uuid);
            if (geyserPlayer != null) return geyserPlayer.ping();
        }
        return -1;
    }

    /**
     * Determines if the player has access to the Geyser platform compatibility integration.
     *
     * @return {@code true} if the player has access to Geyser; {@code false} otherwise.
     */
    public boolean hasGeyser() {
        return geyser;
    }

    /**
     * Checks if the player has Floodgate integration enabled.
     *
     * @return {@code true} if the player has Floodgate integration; {@code false} otherwise.
     */
    public boolean hasFloodgate() {
        return floodgate;
    }
}
