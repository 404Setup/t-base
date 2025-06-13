package one.tranic.t.base.loader;

import java.nio.file.Path;

public interface TPLoader {
    Path getGamePath();

    Path getModPath();

    Path getConfigPath();

    String getLauncherBrand();

    boolean isModLoaded(String modId);

    boolean isClient();

    void execute(Runnable runnable);

    /**
     * Gets the game instance based on the current platform:
     * <p>
     * - For ModLoader: Returns game instance (client or server)
     * <p>
     * - For Proxy: Returns proxy instance
     * <p>
     * - For Plugin Server: Returns Bukkit instance (temporary)
     *
     * @return The game instance object specific to the current platform
     */
    Object getGameInstance();

    TModInfo getMod(String modId);

    TModInfo[] getMods();
}
