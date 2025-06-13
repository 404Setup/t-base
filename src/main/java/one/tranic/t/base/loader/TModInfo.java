package one.tranic.t.base.loader;

import java.nio.file.Path;

@SuppressWarnings("unused")
public interface TModInfo {
    Path modPath();

    String description();

    String version();

    String[] authors();

    String website();

    String modId();

    String modName();
}
