package one.tranic.t.base.player;

import one.tranic.t.utils.Collections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Players {
    // Ignore the "never allocated" warning here, it will be set by the common package via reflection.
    private static Function<String, Player<?>> getPlayerWithStringMethod;
    private static Function<UUID, Player<?>> getPlayerWithUUIDMethod;
    private static Supplier<List<Player<?>>> getOnlinePlayersMethod;
    private static Supplier<List<?>> getPlatformOnlinePlayersMethod;
    private static Supplier<List<String>> getOnlinePlayersNameMethod;

    /**
     * Retrieves a player instance based on their player name.
     *
     * @param name the name of the player to search for; must not be null
     * @return the {@code Player} instance corresponding to the given name, or {@code null} if no player is found
     */
    public static @Nullable Player<?> getPlayer(@NotNull String name) {
        return getPlayerWithStringMethod.apply(name);
    }

    /**
     * Retrieves a player using their unique identifier (UUID).
     *
     * @param uuid the UUID of the player to be retrieved; must not be null
     * @return a {@code Player<?>} instance if a player with the given UUID exists;
     * otherwise, {@code null}
     */
    public static @Nullable Player<?> getPlayer(@NotNull UUID uuid) {
        return getPlayerWithUUIDMethod.apply(uuid);
    }

    /**
     * Retrieves a list of all online players currently connected to the server.
     *
     * @return a {@code List} of {@code Player<?>} representing each online player;
     * the list is guaranteed to be non-null.
     */
    public static @NotNull List<Player<?>> getOnlinePlayers() {
        return getOnlinePlayersMethod.get();
    }

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
     * @param player the {@code Player<?>} whose connected host is used for comparison;
     *               must not be null
     * @return a {@code List<Player<?>>} containing all players who are connected
     * to the same host as the specified player, excluding the player itself;
     * the list is guaranteed to be non-null but may be empty if no other
     * players share the same connected host
     */
    public static @NotNull List<Player<?>> getAltPlayers(@NotNull Player<?> player) {
        final List<Player<?>> end = Collections.newArrayList();
        final List<Player<?>> players = getOnlinePlayers();
        for (Player<?> p : players) {
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
     * @return a {@code List<?>} representing the platform-specific online players;
     * the list is guaranteed to be non-null.
     */
    public static @NotNull List<?> getPlatformOnlinePlayers() {
        return getPlatformOnlinePlayersMethod.get();
    }

    /**
     * Retrieves a list of the names of all online players currently connected to the server.
     *
     * @return a {@code List} of {@code String} containing the names of online players;
     * the list is guaranteed to be non-null.
     */
    public static @NotNull List<String> getOnlinePlayersName() {
        return getOnlinePlayersNameMethod.get();
    }
}
