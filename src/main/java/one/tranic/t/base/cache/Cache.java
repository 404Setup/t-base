package one.tranic.t.base.cache;

/**
 * Represents a generic cache interface that provides access
 * to the underlying {@link CacheService} implementation.
 * <p>
 * Classes implementing this interface are responsible for
 * defining specific cache behaviors while encapsulating their
 * implementation details through the provided service.
 */
public interface Cache extends AutoCloseable {
    /**
     * Retrieves the underlying CacheService instance associated with the current Cache implementation.
     * This method provides access to the internal caching service used for managing cache operations.
     *
     * @return the CacheService instance backing the current Cache implementation.
     */
    CacheService getService();
}
