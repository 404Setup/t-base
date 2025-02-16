package one.tranic.t.base.updater;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import one.tranic.t.proxy.RequestWithProxyParser;
import one.tranic.t.thread.T2hread;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * usage:
 * <pre>
 *     var fetcher = new VersionFetcher(currentVersion, pluginName, apiAddress);
 *     fetcher.start(); // don't use run.
 * </pre>
 */
public abstract class VersionFetcher implements AutoCloseable, Runnable {
    private static final long CACHE_EXPIRY_MINUTES = 1440;
    private static final int SLEEP_HOURS = 2;

    private static final String UPDATE_MESSAGE_TEMPLATE = "<aqua>[{plugin}]</aqua> <gold>The plugin has an update available, from </gold>"
            + "<aqua>{oldVersion}</aqua> <gold>to</gold> <aqua>{latestVersion}</aqua> <gold>, download address: </gold>"
            + "<aqua>{resourceURL}</aqua>";
    final URL updateCheckURL;
    final String currentVersion;
    private final String pluginName;
    private final String noUpdateTemplate;
    private String resourceURL;
    private Thread updateThread;
    private String latestVersion;
    private Date lastUpdateTime = new Date();

    /**
     * Constructs an instance of the {@code VersionFetcher} class.
     *
     * @param currentVersion the current version of the plugin or resource
     * @param pluginName     the name of the plugin or resource
     * @param api            the API URL for checking updates
     */
    public VersionFetcher(String currentVersion, String pluginName, String api) {
        this(currentVersion, pluginName, api, null);
    }

    /**
     * Constructs an instance of the VersionFetcher class.
     *
     * @param currentVersion the current version of the plugin.
     * @param pluginName     the name of the plugin for which version updates are being fetched.
     * @param api            the URL of the API endpoint to check for updates.
     * @param resourceURL    the URL of the plugin's resource page. If null, defaults to "<a href="https://spigotmc.org/resources/">SpigotMC</a>".
     */
    public VersionFetcher(String currentVersion, String pluginName, String api, String resourceURL) {
        this.currentVersion = currentVersion;
        this.pluginName = pluginName;
        try {
            this.updateCheckURL = new URL(api);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.resourceURL = Objects.requireNonNullElse(resourceURL, "https://spigotmc.org/resources/");
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
    @Override
    @SuppressWarnings("all")
    public void run() {
        try {
            this.hasUpdate();
            for (; ; ) {
                TimeUnit.HOURS.sleep(SLEEP_HOURS);
                this.hasUpdate();
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    public void start() {
        close();
        updateThread = T2hread.newThread(this, "FetchVersion-Thread", true);
        updateThread.start();
    }

    /**
     * Closes the resources associated with the FetchVersion instance by stopping the
     * update thread if it is alive.
     * <p>
     * This method interrupts the update thread, preventing any ongoing
     * processes from running, and subsequently sets the update thread to null.
     * It ensures that redundant threads are not left running, which helps in
     * releasing resources and avoiding potential memory leaks.
     */
    @Override
    public void close() {
        if (updateThread != null && updateThread.isAlive())
            updateThread.interrupt();
        updateThread = null;
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
    public @NotNull Component getUpdateMessage() {
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

    /**
     * Updates the resource URL for the current instance.
     *
     * @param resourceURL the new URL of the resource to be associated with this instance
     */
    public void updateResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }

    /**
     * Determines whether there is an update available for the current version
     * of the associated plugin or resource. This method checks the latest
     * known version against the current version and may fetch a new version
     * if necessary.
     *
     * @return true if an update is available (i.e., the latest version differs
     * from the current version); false otherwise
     */
    @SuppressWarnings("all")
    public boolean hasUpdate() {
        if (latestVersion != null && !latestVersion.isBlank() && !isExpired()) {
            return !latestVersion.equals(currentVersion);
        }
        String fetchedVersion = fetchLatestVersion();
        if (fetchedVersion == null || fetchedVersion.isEmpty()) {
            return false;
        }
        latestVersion = fetchedVersion;
        lastUpdateTime = new Date();
        return !latestVersion.equals(currentVersion);
    }

    /**
     * Fetches the latest version of the associated plugin or resource from the update server.
     * <p>
     * This method establishes a connection to the specified update server URL, reads the response,
     * and retrieves the latest version information as a string. If an error occurs during the process,
     * the stack trace is printed, and the method returns null.
     *
     * @return the latest version as a string, or null if an error occurs or the version cannot be retrieved
     */
    @SuppressWarnings("all")
    public String fetchLatestVersion() {
        HttpURLConnection connection = null;
        try {
            connection = RequestWithProxyParser.openConnection(updateCheckURL);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return reader.readLine();
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
