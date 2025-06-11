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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BedrockPlayer {
    private static final boolean geyser = Reflect.hasClass("org.geysermc.geyser.api.GeyserApi");
    private static final boolean floodgate = Reflect.hasClass("org.geysermc.floodgate.api.FloodgateApi");

    private final Player player;
    private final Object bedrockPlayer;

    protected BedrockPlayer(@NotNull Player player) {
        this.player = player;
        this.bedrockPlayer = getBedrockPlayer(player.getUniqueId());
    }

    /**
     * Creates a new {@link BedrockPlayer} instance from the provided {@link Player}.
     *
     * @param player the {@link Player} to convert to a {@link BedrockPlayer}; must not be null
     * @return a new {@link BedrockPlayer} instance associated with the given {@link Player}
     */
    public static BedrockPlayer of(@NotNull Player player) {
        return new BedrockPlayer(player);
    }

    /**
     * Determines whether the specified player, identified by their UUID, is a Bedrock player.
     *
     * @param uuid the universally unique identifier (UUID) of the player to check
     * @return true if the specified player is a Bedrock player, otherwise false
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        if (floodgate)
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        if (geyser) return GeyserApi.api().isBedrockPlayer(uuid);
        return isFloodgatePlayer(uuid);
    }

    /**
     * Checks if a given UUID string represents a Floodgate player by verifying
     * if it starts with the Floodgate UUID prefix.
     *
     * @param uuid the UUID string of the player, expected to contain dashes and not null
     * @return {@code true} if the UUID represents a Floodgate player, {@code false} otherwise
     */
    public static boolean isFloodgatePlayer(String uuid) {
        final String FLOODGATE_UUID_PREFIX = "0000000000000000";

        String str = UUIDParser.removeDashes(uuid);
        return str.startsWith(FLOODGATE_UUID_PREFIX);
    }

    /**
     * Determines if a player with the given UUID is a Floodgate player.
     *
     * @param uuid the universally unique identifier (UUID) of the player to check
     * @return true if the player is a Floodgate player, false otherwise
     */
    public static boolean isFloodgatePlayer(UUID uuid) {
        return isFloodgatePlayer(uuid.toString());
    }

    /**
     * Retrieves the Bedrock player associated with the given UUID.
     *
     * @param uuid the unique identifier of the player to retrieve
     * @return the Bedrock player object if found, otherwise null
     */
    private static @Nullable Object getBedrockPlayer(UUID uuid) {
        return floodgate ? FloodgateApi.getInstance().getPlayer(uuid) :
                geyser ? GeyserApi.api().connectionByUuid(uuid) : null;
    }

    /**
     * Checks if there is a Geyser instance available.
     *
     * @return true if a Geyser instance is available; false otherwise
     */
    public static boolean hasGeyser() {
        return geyser;
    }

    /**
     * Checks if the Floodgate integration is active.
     *
     * @return true if Floodgate is enabled, false otherwise
     */
    public static boolean hasFloodgate() {
        return floodgate;
    }

    /**
     * Retrieves the associated player instance.
     *
     * @return the {@link Player} instance associated with this object; never null
     */
    public @NotNull Player player() {
        return player;
    }

    /**
     * Determines if the current player is a Floodgate player.
     * <p>
     * A Floodgate player is a Bedrock player connected through the Floodgate plugin.
     *
     * @return true if the player is a Floodgate player, false otherwise
     */
    public boolean isFloodgatePlayer() {
        return bedrockPlayer != null && floodgate && bedrockPlayer instanceof FloodgatePlayer;
    }

    /**
     * Determines the platform type of the player.
     *
     * @return the platform type of the player as a non-null string. Possible values include specific platform names
     * from Floodgate or Geyser, or "Java Edition" for non-Bedrock players.
     */
    public @NonNull String platform() {
        if (bedrockPlayer != null) {
            if (floodgate) {
                return ((FloodgatePlayer) bedrockPlayer).getDeviceOs().toString();
            } else if (geyser) {
                return ((GeyserConnection) bedrockPlayer).platform().toString();
            }
        }
        return "Java Edition";
    }

    /**
     * Retrieves the subscription ID of the current Bedrock player instance if available.
     * <p>
     * The method checks whether the player is a Floodgate player and
     * returns their associated subscription ID. If the player is not a Floodgate player
     * or the Bedrock player instance is null, it returns -1.
     *
     * @return the subscription ID of the Floodgate player, or -1 if unavailable
     */
    public int subscribeId() {
        if (bedrockPlayer != null && floodgate) {
            return ((FloodgatePlayerImpl) bedrockPlayer).getSubscribeId();
        }
        return -1;
    }

    /**
     * Retrieves the device ID of the Bedrock player.
     * <p>
     * If the player is a Bedrock player and Geyser is enabled, this method fetches
     * the device ID associated with the player's session.
     *
     * @return the device ID of the player, or {@code null} if the player is not a
     * Bedrock player, Geyser is not enabled, or if the device ID is unavailable
     */
    public @Nullable String deviceId() {
        if (bedrockPlayer != null && geyser) {
            return ((GeyserSession) bedrockPlayer).getClientData().getDeviceId();
        }
        return null;
    }

    /**
     * Retrieves the device model associated with the Bedrock player.
     * <p>
     * If the player is a Bedrock player and the Geyser integration is available, this method
     * fetches the device model from the client's data.
     *
     * @return the device model of the Bedrock player as a string, or {@code null} if unavailable
     */
    public @Nullable String deviceModel() {
        if (bedrockPlayer != null && geyser) {
            return ((GeyserSession) bedrockPlayer).getClientData().getDeviceModel();
        }
        return null;
    }

    /**
     * Determines the input mode used by the Bedrock player, if applicable.
     * <p>
     * The input mode can vary based on the Bedrock player's connection and settings,
     * such as Floodgate or Geyser integration.
     *
     * @return a string representation of the input mode if the Bedrock player is connected via Floodgate
     * or Geyser; otherwise, returns null if the input mode cannot be determined or is not applicable.
     */
    public @Nullable String inputMode() {
        if (bedrockPlayer != null) {
            if (floodgate) {
                return ((FloodgatePlayer) bedrockPlayer).getInputMode().name();
            }
            if (geyser) {
                return ((GeyserConnection) bedrockPlayer).inputMode().name();
            }
        }
        return null;
    }

    /**
     * Retrieves the client ID of the Bedrock player.
     * <p>
     * This method checks if the player instance is associated with Bedrock through Geyser.
     * If so, it fetches the client random ID from the Geyser session.
     * If the player is not a Bedrock player or the Geyser instance is not available,
     * it returns a default value of -1.
     *
     * @return the client ID as a long if the player is a Geyser Bedrock player; -1 otherwise
     */
    public long clientID() {
        if (bedrockPlayer != null && geyser) {
            return ((GeyserSession) bedrockPlayer).getClientData().getClientRandomId();
        }
        return -1;
    }

    /**
     * Retrieves the XUID (Xbox User ID) of the Bedrock player.
     * <p>
     * The method checks if the player is associated with Floodgate or Geyser
     * and retrieves the XUID accordingly.
     *
     * @return the XUID of the Bedrock player as a string, or null if unavailable
     */
    public @Nullable String xuid() {
        if (bedrockPlayer != null) {
            if (floodgate) {
                return ((FloodgatePlayer) bedrockPlayer).getXuid();
            }
            if (geyser) {
                return ((GeyserConnection) bedrockPlayer).xuid();
            }
        }
        return null;
    }

    /**
     * Sends a form to a Bedrock player if applicable.
     * <p>
     * The form will be sent through Floodgate or Geyser, depending on the player's platform.
     *
     * @param form the form to be sent to the player; must not be null
     * @return true if the form was successfully sent; false otherwise
     */
    @SuppressWarnings("all")
    public boolean form(@NotNull Form form) {
        if (bedrockPlayer != null && form != null) {
            if (floodgate) {
                return ((FloodgatePlayer) bedrockPlayer).sendForm(form);
            }
            if (geyser) {
                return ((GeyserConnection) bedrockPlayer).sendForm(form);
            }
        }
        return false;
    }

    /**
     * Retrieves the ping value of a Bedrock player if applicable.
     * <p>
     * This method checks if the associated player is a Bedrock player and if
     * the Geyser integration is available.
     *
     * @return the ping value in milliseconds for a Bedrock player, or -1 if
     * the player is not a Bedrock player or Geyser is not available.
     */
    public long ping() {
        if (bedrockPlayer != null && geyser) {
            return ((GeyserConnection) bedrockPlayer).ping();
        }
        return -1;
    }
}
