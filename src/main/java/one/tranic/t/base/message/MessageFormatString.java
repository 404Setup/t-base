package one.tranic.t.base.message;

/**
 * Record representing a message with a key and a string value.
 * <p>
 * The {@code MessageFormatString} record is used to associate a unique key with a
 * textual value. This class provides a simple representation of a message or text
 * that can be utilized in various contexts, such as localization or configuration.
 *
 * @param key   the unique identifier for the message
 * @param value the string value representing the message content
 */
public record MessageFormatString(String key, String value) {
}