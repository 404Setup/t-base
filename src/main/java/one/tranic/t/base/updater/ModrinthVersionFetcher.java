package one.tranic.t.base.updater;

import one.tranic.t.base.parse.json.JsonParser;
import one.tranic.t.base.parse.proxy.RequestWithProxyParser;
import one.tranic.t.base.updater.schemas.modrinth.Loaders;
import one.tranic.t.base.updater.schemas.modrinth.ModrinthVersionSource;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Objects;

public class ModrinthVersionFetcher extends VersionFetcher {
    private static final String MODRINTH_PROJECT_URL = "https://modrinth.com/project/";
    private final String expectedLoader;


    public ModrinthVersionFetcher(String currentVersion, String pluginName, String slug, Loaders loaders) {
        super(
                currentVersion,
                pluginName,
                "https://api.modrinth.com/v2/project/" + slug + "/version",
                MODRINTH_PROJECT_URL + slug
        );
        this.expectedLoader = loaders.toString();
    }

    @Override
    public String fetchLatestVersion() {
        HttpURLConnection connection = null;
        try {
            connection = RequestWithProxyParser.openConnection(updateCheckURL);
            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                var versions = JsonParser.gson().fromJson(reader, ModrinthVersionSource[].class);
                if (versions.length == 0) return null;
                ModrinthVersionSource latestVersion = versions[0];
                if (hasCompatibleLoader(latestVersion) && !Objects.equals(currentVersion, latestVersion.getVersionNumber())) {
                    updateResourceURL(MODRINTH_PROJECT_URL + latestVersion.getProjectId() + "/version/" + latestVersion.getVersionNumber());
                    return latestVersion.getVersionNumber();
                }
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private boolean hasCompatibleLoader(ModrinthVersionSource version) {
        for (String loader : version.getLoaders()) {
            if (Objects.equals(loader, expectedLoader)) {
                return true;
            }
        }
        return false;
    }
}
