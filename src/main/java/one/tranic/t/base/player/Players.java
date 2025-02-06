package one.tranic.t.base.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public class Players {
    private static Function<String, Player<?>> getPlayerWithStringMethod;
    private static Function<UUID, Player<?>> getPlayerWithUUIDMethod;

    public static @Nullable Player<?> getPlayer(@NotNull String name) {
        return getPlayerWithStringMethod.apply(name);
    }

    public static @Nullable Player<?> getPlayer(@NotNull UUID uuid) {
        return getPlayerWithUUIDMethod.apply(uuid);
    }
}
