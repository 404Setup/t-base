package one.tranic.t.base;

import one.tranic.t.base.command.Operator;
import one.tranic.t.base.command.source.SystemCommandSource;
import one.tranic.t.thread.T2hread;
import org.jetbrains.annotations.Nullable;

import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class TBase {
    public final static ExecutorService executor = T2hread.getExecutor();
    public final static TInterface INSTANCE;
    private final static Operator operator = new Operator("Console", UUID.fromString("05b11eee-24db-4a21-ba9d-e12e8df9a92f"));
    private static final String packageName;
    //public final static List<String> EMPTY_LIST = Collections.newUnmodifiableList();
    private static SystemCommandSource<?, ?> CONSOLE_SOURCE;

    static {
        packageName = getRootPath();
        INSTANCE = load();
    }

    @SuppressWarnings("all")
    private static TInterface load() {
        var loader = ServiceLoader.load(TInterface.class);
        var i = loader.iterator();
        for (; i.hasNext(); ) {
            var service = i.next();
            var platforms = service.getSupportedPlatforms();
            for (var platform : platforms) {
                if (platform.is() && !platform.isMixinServer()) {
                    return service;
                }
            }
        }
        throw new NullPointerException("Failed to load service for " + TInterface.class.getName());
    }

    /**
     * Retrieves the root path of the current package structure.
     * This method utilizes the {@code getCurrentRootPackage} method to determine the
     * uppermost level of the package hierarchy for the {@code TBase} class.
     *
     * @return the root package path as a {@code String}, representing the highest-level
     * parent package in the hierarchy of the {@code TBase} class.
     */
    public static String getRootPath() {
        if (packageName != null) return packageName;
        return getCurrentRootPackage(TBase.class);
    }

    /**
     * Determines the root package for the given class by traversing up one level from the current package name.
     * If the class is already in the root package, it returns the current package name as is.
     *
     * @param clazz the class whose root package is to be determined
     * @return the root package name as a string
     */
    public static String getCurrentRootPackage(Class<?> clazz) {
        String currentPackage = clazz.getPackageName();
        int lastDotIndex = currentPackage.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return currentPackage.substring(0, lastDotIndex);
        }
        return currentPackage; // Return as is, if no parent exists
    }

    public static Operator console() {
        return operator;
    }

    /**
     * Executes the given {@code Runnable} asynchronously using a pre-configured executor.
     *
     * @param runnable the {@code Runnable} task to be executed asynchronously
     * @return a {@code CompletableFuture<Void>} that completes once the specified task has been executed
     */
    public static CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executor);
    }

    /**
     * Executes the given supplier asynchronously using a pre-configured executor.
     * <p>
     * This method is intended for running tasks that return a result in an asynchronous manner.
     *
     * @param <T>      the type of the result produced by the supplier
     * @param supplier the {@code Supplier} task to be executed asynchronously
     * @return a {@code CompletableFuture<T>} that completes with the result of the supplier execution
     */
    public static <T> CompletableFuture<T> runAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executor);
    }

    public static void close() {
        executor.shutdownNow();
        INSTANCE.disable();
    }
}
