package one.tranic.t.base.updater;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import one.tranic.t.base.TBase;
import one.tranic.t.base.parse.proxy.RequestWithProxyParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FetchVersion {
    private static final long CACHE_EXPIRY_MINUTES = 1440;
    private static final String UPDATE_MESSAGE_TEMPLATE = "<aqua>[{plugin}]</aqua> <gold>The plugin has an update available, from </gold>"
            + "<aqua>{oldVersion}</aqua> <gold>to</gold> <aqua>{latestVersion}</aqua> <gold>, download address: </gold>"
            + "<aqua>{resourceURL}</aqua>";

    private final String pluginName;
    private final URL updateCheckURL;
    private final String currentVersion;
    private final String resourceURL;
    private final String noUpdateTemplate;
    private Future<?> future;
    private String latestVersion;
    private Date lastUpdateTime = new Date();


    /**
     * Constructs a {@code FetchVersion} instance for version checking and updating purposes.
     *
     * @param currentVersion the current version of the associated plugin or application.
     * @param pluginName     the name of the plugin or application being checked for updates.
     * @param resourceId     the unique identifier for the resource being checked, typically corresponding to its ID on the update server.
     */
    public FetchVersion(String currentVersion, String pluginName, String resourceId) {
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
    public FetchVersion(String currentVersion, String pluginName, String resourceId, String resourceURL) {
        this.currentVersion = currentVersion;
        this.pluginName = pluginName;
        try {
            this.updateCheckURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.resourceURL = Objects.requireNonNullElseGet(resourceURL, () -> "https://spigotmc.org/resources/" + resourceId);
        this.noUpdateTemplate = "<aqua>[" + pluginName + "]</aqua> <green> Already the latest version (or because of cache)</green>";
    }

    /**
     * Initiates an asynchronous version check process by scheduling a task through an executor.
     * <p>
     * This method first invokes the {@code stop} method to terminate any previously scheduled task.
     * It then submits a new task to be executed asynchronously. The task sleeps for a defined period
     * (2 hours) and subsequently calls the {@code hasUpdate} method to determine if an update is available.
     * <p>
     * Tasks are managed using the static executor from the {@code TBase} class.
     * Any exceptions during the sleep period of the task are ignored.
     */
    public void run() {
        stop();
        future = TBase.executor.submit(() -> {
            try {
                TimeUnit.HOURS.sleep(2);
            } catch (Exception ignored) {
            }
            this.hasUpdate();
        });
    }

    /**
     * Stops any ongoing task associated with this instance by canceling the scheduled
     * future, if it exists and has not already been canceled. If the cancellation is successful,
     * it interrupts the task if it is currently running.
     * <p>
     * After canceling the future, the reference to it is set to {@code null} to release resources.
     */
    public void stop() {
        if (future != null && !future.isCancelled())
            future.cancel(true);
        future = null;
    }

    /**
     * Generates and returns an update notification message in a formatted style.
     * <p>
     * The message is created based on the placeholders in the update message template,
     * replacing them with the current plugin's name, version, the latest version available,
     * and the corresponding resource URL.
     *
     * @return a formatted {@code Component} representing the update message.
     */
    public Component getUpdateMessage() {
        String parsedMessage = UPDATE_MESSAGE_TEMPLATE
                .replace("{plugin}", pluginName)
                .replace("{oldVersion}", currentVersion)
                .replace("{latestVersion}", getLatestVersion())
                .replace("{resourceURL}", getResourceURL());
        return MiniMessage.miniMessage().deserialize(parsedMessage);
    }

    /**
     * Builds and returns a message component indicating that no update is available.
     * <p>
     * The message content is derived from the pre-defined <code>noUpdateTemplate</code>.
     *
     * @return a {@link Component} representing the "no update available" message.
     */
    public Component getNoUpdateMessage() {
        return MiniMessage.miniMessage().deserialize(noUpdateTemplate);
    }

    /**
     * Determines whether the cached data has expired based on the time elapsed
     * since the last update compared to the predefined cache expiry threshold.
     *
     * @return true if the cached data has expired; false otherwise
     */
    public boolean isExpired() {
        long diffInMinutes = (new Date().getTime() - lastUpdateTime.getTime()) / (1000 * 60);
        return diffInMinutes > CACHE_EXPIRY_MINUTES;
    }

    /**
     * Retrieves the latest version of the plugin or resource.
     * <p>
     * This method returns the most recent version that has been checked or retrieved,
     * which can be compared against the current version to determine if an update is available.
     *
     * @return a string representing the latest version, or null if no version has been retrieved yet.
     */
    public String getLatestVersion() {
        return latestVersion;
    }

    /**
     * Retrieves the URL of the resource associated with this object.
     *
     * @return the resource URL as a String.
     */
    public String getResourceURL() {
        return resourceURL;
    }

    public boolean hasUpdate() {
        if (latestVersion != null && !latestVersion.isBlank() && !isExpired()) {
            return !latestVersion.equals(currentVersion);
        }
        HttpURLConnection connection = null;
        try {
            connection = RequestWithProxyParser.openConnection(updateCheckURL);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                latestVersion = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        if (latestVersion == null || latestVersion.isEmpty()) {
            return false;
        }
        lastUpdateTime = new Date();
        return !latestVersion.equals(currentVersion);
    }
}
