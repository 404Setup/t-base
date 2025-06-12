package one.tranic.t.base;

import one.tranic.t.base.command.source.SystemCommandSource;
import one.tranic.t.base.player.Player;
import one.tranic.t.utils.Collections;
import one.tranic.t.utils.minecraft.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
public interface TInterface<S, P> {
    /**
     * Enables the functionality or state associated with this instance.
     */
    void enable();

    /**
     * Disables the current instance of the interface or system.
     */
    void disable();

    /**
     * Retrieves an array of supported platforms for the implementing service.
     *
     * @return an array of {@code Platform} objects representing the platforms supported by this service.
     * The returned array is guaranteed to be non-null but may be empty if no platforms are supported.
     */
    Platform[] getSupportedPlatforms();

    /**
     * Retrieves the console command source for the system.
     * The console source represents an abstract command execution entity
     * used for operations not tied to any specific player or client.
     *
     * @return a {@code SystemCommandSource<S, P>} instance representing the console command source.
     */
    SystemCommandSource<S, P> getConsoleSource();

    /**
     * Retrieves a player instance based on their player name.
     *
     * @param name the name of the player to search for; must not be null
     * @return the {@code Player} instance corresponding to the given name, or {@code null} if no player is found
     */
    @Nullable Player<P> getPlayer(@NotNull String name);

    /**
     * Retrieves a player using their unique identifier (UUID).
     *
     * @param uuid the UUID of the player to be retrieved; must not be null
     * @return a {@code Player<P>} instance if a player with the given UUID exists;
     * otherwise, {@code null}
     */
    @Nullable Player<P> getPlayer(@NotNull UUID uuid);

    /**
     * Retrieves a list of all online players currently connected to the server.
     *
     * @return a {@code List} of {@code Player<P>} representing each online player;
     * the list is guaranteed to be non-null.
     */
    @NotNull List<Player<P>> getOnlinePlayers();

    /**
     * Retrieves a list of alternative players who are connected to the same host
     * as the specified player, excluding the player itself.
     * <p>
     * This method identifies other players currently online that share the same
     * connected host with the given player.
     * <p>
     * Players with a matching connected host are returned in the resulting list, ensuring that the specified player is
     * excluded from this list.
     *
     * @param player the {@code Player<P>} whose connected host is used for comparison;
     *               must not be null
     * @return a {@code List<Player<P>>} containing all players who are connected
     * to the same host as the specified player, excluding the player itself;
     * the list is guaranteed to be non-null but may be empty if no other
     * players share the same connected host
     */
    default @NotNull List<Player<P>> getAltPlayers(@NotNull Player<P> player) {
        final List<Player<P>> end = Collections.newArrayList();
        final List<Player<P>> players = getOnlinePlayers();
        for (Player<P> p : players) {
            if (p.getUniqueId() != player.getUniqueId() && Objects.equals(p.getConnectedHost(), player.getConnectedHost()))
                end.add(p);
        }
        return end;
    }

    /**
     * Retrieves a list of all online players currently connected to the platform.
     * <p>
     * The specific type of player instances within this list may vary based on the platform implementation.
     *
     * @return a {@code List<P>} representing the platform-specific online players;
     * the list is guaranteed to be non-null.
     */
    @NotNull List<P> getPlatformOnlinePlayers();

    /**
     * Retrieves a list of the names of all online players currently connected to the server.
     *
     * @return a {@code List} of {@code String} containing the names of online players;
     * the list is guaranteed to be non-null.
     */
    @NotNull List<String> getOnlinePlayersName();
}
