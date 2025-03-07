package one.tranic.t.base.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import one.tranic.t.utils.Collections;
import one.tranic.t.utils.Platform;

import java.util.List;
import java.util.Objects;

/**
 * The {@code MessageExample} enum defines a set of predefined message keys
 * used for retrieving and formatting text-based messages.
 * <p>
 * It is only used as your own MessageKey reference.
 * Please do not use it directly in your own project. You should copy it and add your own keys.
 * <p>
 * Each enum constant is associated with a unique key, which serves as the
 * identifier for a specific message.
 */
public enum MessageExample {
    NAME("name"),
    DEFAULT_KICK("kick.default"), KICK_MESSAGE("kick.message");
    private final String key;

    MessageExample(String key) {
        this.key = key;
    }

    /**
     * Retrieves the key associated with this {@code MessageExample} instance.
     *
     * @return the key as a {@code String} that uniquely identifies this message
     */
    public String getKey() {
        return key;
    }

    /**
     * Formats a message using the provided arguments and resolves placeholders.
     * <p>
     * If no arguments are provided, it simply retrieves and returns the message
     * associated with the current object key.
     *
     * @param args the arguments used to populate placeholders in the message.
     *             This can be an array of {@link MessageFormat} or {@link MessageFormatString} objects.
     * @return a {@link Component} representing the formatted message with resolved placeholders.
     * @throws IllegalArgumentException if the arguments are not of type MessageFormat[]
     *                                  or MessageFormatString[].
     */
    public Component format(Object... args) {
        if (args.length == 0)
            return MiniMessage.miniMessage().deserialize(
                    Objects.requireNonNull(Message.getMessage(this))
            );

        List<TagResolver> list = Collections.newArrayList();
        if (args instanceof MessageFormat[] argsArray) {
            for (MessageFormat arg : argsArray) list.add(Placeholder.component(arg.key(), arg.value()));
        } else if (args instanceof MessageFormatString[] argsArray) {
            for (MessageFormatString arg : argsArray)
                list.add(Placeholder.component(arg.key(), Component.text(arg.value())));
        } else {
            throw new IllegalArgumentException("Invalid argument type");
        }

        return MiniMessage.miniMessage().deserialize(
                Objects.requireNonNull(Message.getMessage(this)),
                list.toArray(new TagResolver[0])
        );
    }

    /**
     * Retrieves the message string associated with this enum constant.
     * <p>
     * The message is fetched using the key corresponding to the enum constant
     * from the internal messages map, and is guaranteed to be non-null.
     *
     * @return the non-null message string associated with the enum constant
     * @throws NullPointerException if no message is found for the enum constant
     */
    public String getValue() {
        return Objects.requireNonNull(Message.getMessage(this));
    }

    /**
     * Formats a string representation of a message using specified arguments.
     * <p>
     * The method utilizes the legacy section serialization format to convert
     * the formatted {@link Component} returned by the {@code format} method
     * into a string.
     *
     * @param args the arguments to be used for formatting the message. The arguments
     *             can be of various types, including {@link MessageFormat} or
     *             {@link MessageFormatString} arrays.
     * @return the serialized string representation of the formatted message
     */
    public String formatString(Object... args) {
        return LegacyComponentSerializer.legacySection().serialize(format(args));
    }

    /**
     * Converts the given arguments into an array of BungeeCord BaseComponents
     * using legacy text formatting, but only if the current platform is BungeeCord.
     *
     * @param args the arguments to format into a legacy text string and convert to BungeeCord BaseComponents
     * @return an array of BungeeCord BaseComponents representing the formatted text,
     * or null if the current platform is not BungeeCord
     */
    public net.md_5.bungee.api.chat.BaseComponent[] formatBungee(Object... args) {
        if (Platform.get() != Platform.BungeeCord) return null;
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(formatString(args));
    }
}
