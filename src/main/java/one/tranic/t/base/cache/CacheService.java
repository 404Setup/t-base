package one.tranic.t.base.cache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Optional;

public interface CacheService extends AutoCloseable {
    <T> @NotNull Optional<T> get(@NotNull String key, @NotNull Class<T> type);

    @NotNull String get(@NotNull String key);

    void put(@NotNull String key, @NotNull Object value, @Range(from = 0, to = Long.MAX_VALUE) long ttl);

    void invalidate(@NotNull String key);

    void invalidateAll();
}
