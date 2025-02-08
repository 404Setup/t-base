package one.tranic.t.base.updater;

/**
 * The FetchVersion class is responsible for managing the fetching and updating of a plugin's version.
 * <p>
 * It performs periodic version checks, determines whether an update is available,
 * and provides formatted messages displaying update statuses.
 * <p>
 * This class implements both the {@code AutoCloseable} and {@code Runnable} interfaces for resource management and
 * asynchronous operations, respectively.
 */
@SuppressWarnings("unused")
public class SpigotVersionFetcher extends VersionFetcher {
    /**
     * Constructs a {@code FetchVersion} instance for version checking and updating purposes.
     *
     * @param currentVersion the current version of the associated plugin or application.
     * @param pluginName     the name of the plugin or application being checked for updates.
     * @param resourceId     the unique identifier for the resource being checked, typically corresponding to its ID on the update server.
     */
    public SpigotVersionFetcher(String currentVersion, String pluginName, String resourceId) {
        this(currentVersion, pluginName, resourceId, null);
    }

    /**
     * Constructs a new FetchVersion instance responsible for managing version fetching and updates.
     *
     * @param currentVersion the current version of the plugin as a String
     * @param pluginName     the name of the plugin the version fetch is associated with
     * @param resourceId     the resource ID of the plugin on the platform
     * @param resourceURL    the URL of the resource; if null, a default URL is generated based on the resourceId
     */
    public SpigotVersionFetcher(String currentVersion, String pluginName, String resourceId, String resourceURL) {
        super(currentVersion, pluginName, "https://api.spigotmc.org/legacy/update.php?resource=" + resourceId, resourceURL);
    }
}
