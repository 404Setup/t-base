package one.tranic.t.util;

public class Sys {
    /**
     * Retrieves the current JVM version as an integer.
     * <p>
     * The version is determined by parsing the system property "java.version".
     * If parsing fails, the feature version from {@link Runtime#version()} is used as a fallback.
     *
     * @return the current JVM version as an integer.
     */
    public static int getCurrentJVMVersion() {
        if (cachedJvmVersion != 0) {
            return cachedJvmVersion;
        }

        try { // https://stackoverflow.com/a/2591122
            String version = System.getProperty("java.version");
            if (version.startsWith("1.")) {
                version = version.substring(2, 3);
            } else {
                int dot = version.indexOf(".");
                if (dot != -1) {
                    version = version.substring(0, dot);
                }
            }
            version = version.split("-")[0];
            return Integer.parseInt(version);
        } catch (Exception e) {
            return Runtime.version().feature();
        }
    }

    // Since Java 1.1, but this library has not supported it either
    @SuppressWarnings("unused")
    public static int getCurrentJVMClassVersion() {
        return 44 + cachedJvmVersion;
    }

    /**
     * Checks whether a class with the specified fully qualified name exists in the classpath.
     *
     * @param className the fully qualified name of the class to check.
     * @return {@code true} if the class exists; {@code false} otherwise.
     */
    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static final int cachedJvmVersion = getCurrentJVMVersion();


}
