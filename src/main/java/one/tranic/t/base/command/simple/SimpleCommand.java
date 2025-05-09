package one.tranic.t.base.command.simple;

import net.kyori.adventure.text.Component;
import one.tranic.t.base.TBase;
import one.tranic.t.base.command.source.CommandSource;
import one.tranic.t.utils.minecraft.Platform;

/**
 * Abstract base class representing a command in a multi-platform environment.
 * <p>
 * This class provides methods to manage command properties such as name, description, usage,
 * and permissions, as well as utilities to handle platform-specific command registration and unwrapping.
 *
 * @param <C> the type of the command source, extending from {@link CommandSource}
 */
public abstract class SimpleCommand<C extends CommandSource<?, ?>> implements SimpleCommandImpl<C> {
    private String name;
    private String description;
    private String usage;
    private String permission;

    @Override
    public boolean hasPermission(C source) {
        return source.hasPermission(getPermission());
    }

    /**
     * Sends a result message to a specified source.
     *
     * @param source the command source who will receive the message
     * @param msg    the message to be sent to the source
     */
    public void sendResult(C source, Component msg) {
        sendResult(source, msg, true);
    }

    /**
     * Sends a message result to a given source, taking into account whether the source is
     * a Bedrock player, a standard player, or whether the message should also be sent to the console.
     *
     * @param source      the source to which the result should be sent; can be a player or other entity
     * @param msg         the message to be sent, represented as a {@link Component}
     * @param withConsole if true, the message will also be sent to the console
     */
    public void sendResult(C source, Component msg, boolean withConsole) {
        if (source.isPlayer()) source.sendMessage(msg);
        if (withConsole) TBase.getConsoleSource().sendMessage(msg);
    }

    /**
     * Sends a message result to a specified source.
     *
     * @param source the source (e.g., player or console) to which the result will be sent
     * @param msg    the message to be sent to the source
     */
    public void sendResult(C source, String msg) {
        sendResult(source, msg, true);
    }

    /**
     * Sends a result message to the specified source and optionally to the console.
     *
     * @param source      the source to which the result is sent; it can represent a player or another entity
     * @param msg         the message to be sent
     * @param withConsole whether the message should also be sent to the console
     */
    public void sendResult(C source, String msg, boolean withConsole) {
        if (source.isPlayer()) source.sendMessage(msg);
        if (withConsole) TBase.getConsoleSource().sendMessage(msg);
    }

    /**
     * Retrieves the name of the command.
     *
     * @return the name of the command
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the command, prefixing it with platform-specific identifiers.
     *
     * @param name the base name to set for this command
     */
    public void setName(String name) {
        if (Platform.get() == Platform.BungeeCord) {
            this.name = "b" + name;
        } else if (Platform.get() == Platform.Velocity) {
            this.name = "v" + name;
        } else {
            this.name = name;
        }
    }

    /**
     * Retrieves the description of the command.
     *
     * @return the description text for this command
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for this command.
     *
     * @param description the description to set for this command
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the usage string for the command.
     *
     * @return the usage string for the command
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Sets the usage description for this command.
     *
     * @param usage the usage description to set for this command
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Retrieves the permission required to execute this command.
     *
     * @return the permission string associated with this command
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Sets the permission for the command with platform-specific modifications.
     *
     * @param permission the base permission string to set for this command
     */
    public void setPermission(String permission) {
        if (Platform.get() == Platform.BungeeCord) {
            this.permission = permission.replaceFirst("\\.([^.]+)$", ".b$1");
        } else if (Platform.get() == Platform.Velocity) {
            this.permission = permission.replaceFirst("\\.([^.]+)$", ".v$1");
        } else {
            this.permission = permission;
        }
    }
}
