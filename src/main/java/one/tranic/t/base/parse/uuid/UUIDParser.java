package one.tranic.t.base.parse.uuid;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDParser {
    private static final Pattern UUID_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");


    public static @Nullable UUID format(@NotNull String uuidString) {
        if (uuidString == null || uuidString.isBlank()) return null;

        Matcher matcher = UUID_PATTERN.matcher(uuidString);
        if (matcher.matches()) {
            String formattedUUID = String.format("%s-%s-%s-%s-%s",
                    matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
            return UUID.fromString(formattedUUID);
        }
        return null;

    }
}
