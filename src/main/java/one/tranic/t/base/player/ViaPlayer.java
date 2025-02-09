package one.tranic.t.base.player;

import com.viaversion.viaversion.api.Via;
import one.tranic.t.util.Sys;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ViaPlayer {
    private static final boolean isEnabled = Sys.hasClass("com.viaversion.viaversion.api.Via");

    /**
     * Retrieves the protocol version of a player identified by their unique identifier (UUID).
     * <p>
     * If the ViaVersion plugin is enabled, the player's protocol version is fetched using the ViaVersion API.
     * If the plugin is not enabled, this method returns -1.
     *
     * @param uuid the universally unique identifier (UUID) of the player; must not be null
     * @return the protocol version of the player as an integer if ViaVersion is enabled;
     * otherwise, -1
     * @throws IllegalArgumentException if the provided UUID is null
     */
    public static int getVersion(@NotNull UUID uuid) {
        if (!isEnabled) return -1;
        Validate.notNull(uuid, "UUID cannot be empty");
        return Via.getAPI().getPlayerVersion(uuid);
    }

    /**
     * Retrieves the protocol version of the specified player.
     * <br>
     * This method utilizes the ViaVersion API to determine the protocol version of the player
     * if the ViaVersion plugin is enabled. If the plugin is not enabled, it defaults to returning -1.
     *
     * @param player the player object for which the protocol version is to be retrieved; must not be null
     * @return the protocol version of the player as an integer if ViaVersion is enabled;
     * -1 if ViaVersion is not enabled
     * @throws IllegalArgumentException if the player object is null
     */
    public static int getVersion(@NotNull Object player) {
        if (!isEnabled) return -1;
        Validate.notNull(player, "Player cannot be empty");
        return Via.getAPI().getPlayerVersion(player);
    }

    /**
     * Determines if the ViaVersion plugin is enabled.
     *
     * @return {@code true} if the ViaVersion plugin is active and available, {@code false} otherwise.
     */
    public static boolean isEnabled() {
        return isEnabled;
    }
}
