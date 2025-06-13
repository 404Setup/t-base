package one.tranic.t.base.loader;

import java.nio.file.Path;

public interface TPLoader {
    Path getGamePath();

    Path getPluginPath();

    boolean isClient();
}
