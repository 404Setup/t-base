package one.tranic.t.base.command.source;

import net.kyori.adventure.text.Component;
import one.tranic.t.base.TBase;
import one.tranic.t.base.command.Operator;
import one.tranic.t.base.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Represents a source implementation that abstracts interactions with various platforms.
 *
 * @param <C> the underlying type representing the source (e.g., CommandSender)
 * @param <R> the player-specific type related to the platform (e.g., Player)
 */
public interface CommandSource<C, R> {
    /**
     * Retrieves the operator associated with this source implementation.
     * <p>
     * The operator typically represents the entity that executes administrative or management tasks.
     *
     * @return the operator, defaulting to the system console operator.
     */
    default Operator getOperator() {
        return TBase.console();
    }

    /**
     * Retrieves the source entity that this implementation represents.
     *
     * @return the source entity of type C
     */
    C getSource();

    /**
     * Determines if the entity associated with this source is a Bedrock player.
     *
     * @return true if the source entity is a Bedrock player, otherwise false
     */
    default boolean isBedrockPlayer() {
        return isPlayer() && asPlayer().isBedrockPlayer();
    }

    /**
     * Determines if the entity represented by this source is a player.
     *
     * @return true if the source is a player, otherwise false
     */
    boolean isPlayer();

    /**
     * Retrieves the arguments associated with the source. The arguments are typically
     * used to provide additional information or parameters related to a command or action.
     *
     * @return an array of strings representing the arguments associated with the source
     */
    String[] getArgs();

    /**
     * Retrieves the number of arguments associated with the source.
     *
     * @return the number of arguments as an integer.
     */
    int argSize();

    /**
     * Retrieves the locale associated with this source, identifying the language
     * and regional preferences set for the source.
     *
     * @return the locale representing the language and region preferences, or null
     * if the client is not connected to any server.
     * <p>
     * If the source is not a player, it returns the environment's default locale.
     */
    @Nullable Locale locale();

    /**
     * Checks if the entity associated with this source has the specified permission.
     *
     * @param permission the permission node to check for
     * @return true if the entity has the specified permission, otherwise false
     */
    boolean hasPermission(String permission);

    /**
     * Sends a message to the source.
     *
     * @param message the message to be sent
     */
    void sendMessage(String message);

    /**
     * Sends a message to the source.
     *
     * @param message the message to be sent; must not be null
     */
    void sendMessage(@NotNull Component message);

    /**
     * Attempts to retrieve the source as a {@link Player}.
     * If the source does not represent a player, this method will return {@code null}.
     *
     * @return a {@code Player<R>} instance if the source is a player, or
     * {@code null} if the source is not a player.
     */
    @Nullable Player<R> asPlayer();
}
