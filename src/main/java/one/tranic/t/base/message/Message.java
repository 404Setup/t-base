package one.tranic.t.base.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import one.tranic.t.util.Collections;
import one.tranic.t.util.Platform;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Message {
    private static final Map<Object, String> messages = Collections.newHashMap();
    private static final List<Locale> supportedLocales = Collections.newArrayList();
    private static MessageImpl messageImpl;

    public static MessageImpl getMessageImpl() {
        return messageImpl;
    }

    public static void setMessageImpl(MessageImpl messageImpl) {
        Message.messageImpl = messageImpl;
    }

    /**
     * Retrieves the map of messages associated with their respective keys.
     *
     * @return a map where the keys are of type T and the values are message strings
     */
    public static Map<Object, String> getMessages() {
        return messages;
    }

    /**
     * Retrieves a list of supported locales.
     *
     * @return a list of {@link Locale} objects representing the supported locales
     */
    public static List<Locale> getSupportedLocales() {
        return supportedLocales;
    }

    /**
     * Sets a message for the given key.
     *
     * @param key     the key associated with the message; must not be null
     * @param message the message to be associated with the key; must not be null
     */
    public static void setMessage(Object key, String message) {
        messages.put(key, message);
    }

    /**
     * Retrieves the message associated with the provided key from the internal messages map.
     *
     * @param key the key whose associated message is to be retrieved; must not be null
     * @return the message string associated with the specified key, or null if no message is found
     */
    public static String getMessage(Object key) {
        return messages.get(key);
    }

    public static void loadAllMessage(@NotNull Object object) {
        Validate.notNull(object, "Object cannot be null");
        messageImpl.loadAllMessage(object);
    }

    public static void reloadMessages() {
        messageImpl.reloadMessages();
    }

    /**
     * Converts a {@link Component} into its string representation using the legacy section
     * serialization format.
     *
     * @param component the {@link Component} to be serialized; must not be null
     * @return the serialized string representation of the given {@link Component}
     */
    public static String toString(@NotNull Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    /**
     * Converts a legacy text string into an array of BaseComponent objects.
     * If the current platform is not BungeeCord, the method returns null.
     *
     * @param message the legacy text string to be converted; must not be null
     * @return an array of BaseComponent objects representing the converted text,
     * or null if the platform is not BungeeCord
     */
    public static net.md_5.bungee.api.chat.BaseComponent[] toBaseComponent(@NotNull String message) {
        Validate.notNull(message, "Message cannot be null");
        if (Platform.get() != Platform.BungeeCord) return null;
        return net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message);
    }

    /**
     * Converts a {@link Component} to an array of {@link net.md_5.bungee.api.chat.BaseComponent}.
     * <p>
     * This method leverages the {@code toString(Component)} method to serialize the input
     * {@link Component} into a legacy string format and then transforms it into BungeeCord's
     * {@link Component} array representation.
     *
     * @param component The {@link Component} to be converted. Must not be null.
     * @return An array of {@link net.md_5.bungee.api.chat.BaseComponent} representing the provided
     * {@link Component}, or null if a non-BungeeCord platform is detected.
     */
    public static net.md_5.bungee.api.chat.BaseComponent[] toBaseComponent(@NotNull Component component) {
        Validate.notNull(component, "Component cannot be null");
        if (Platform.get() != Platform.BungeeCord) return null;
        return toBaseComponent(toString(component));
    }

    /**
     * Registers a new locale to the list of supported locales.
     *
     * @param locale the locale to be registered; must not be null
     * @return true if the locale was successfully added to the list of supported locales,
     * false if the locale is already present in the list
     */
    public boolean registerLocale(Locale locale) {
        return supportedLocales.add(locale);
    }

    /**
     * Checks if the specified locale is supported.
     *
     * @param locale the {@link Locale} to be checked; must not be null
     * @return {@code true} if the specified locale is supported, {@code false} otherwise
     */
    public boolean isSupportedLocale(Locale locale) {
        return supportedLocales.contains(locale);
    }

    /**
     * Clears all the messages stored in the internal messages map.
     * <p>
     * This method removes all key-value pairs from the map used to store
     * messages, effectively resetting it to an empty state. It is typically
     * used when a fresh start or reloading of messages is required.
     */
    public void resetMessages() {
        messages.clear();
    }

    /**
     * Clears all currently supported locales from the list of registered locales.
     * This method effectively resets the state of supported locales by removing all
     * previously added entries from the internal list.
     * <p>
     * It is useful in scenarios where a complete reconfiguration or reset of
     * supported locales is required.
     */
    public void resetSupportedLocales() {
        supportedLocales.clear();
    }
}
