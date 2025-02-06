package one.tranic.t.base;

import one.tranic.t.base.command.Operator;
import one.tranic.t.base.command.source.CommandSource;
import one.tranic.t.util.Threads;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class TBase {
    public final static ExecutorService executor = Threads.getExecutor();
    //public final static List<String> EMPTY_LIST = Collections.newUnmodifiableList();
    public static final CommandSource<?, ?> CONSOLE_SOURCE;
    private final static Operator operator = new Operator("Console", UUID.fromString("05b11eee-24db-4a21-ba9d-e12e8df9a92f"));
    private static final String packageName;
    private static Method getConsoleMethod;

    static {
        packageName = getRootPath();
        CONSOLE_SOURCE = getConsoleSource();
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
     * Retrieves the command source associated with the system console.
     * This method utilizes reflection to dynamically access the corresponding implementation.
     * If the required method or class cannot be accessed, an {@code IllegalStateException}
     * is thrown to indicate the failure.
     *
     * @return the command source for the system console, represented as a {@code CommandSource<?, ?>}
     * @throws IllegalStateException if the method or class cannot be accessed via reflection
     */
    private static CommandSource<?, ?> getConsoleSource() throws IllegalStateException {
        try {
            if (getConsoleMethod != null)
                return (CommandSource<?, ?>) getConsoleMethod.invoke(null); // Cache target method
            Class<?> commonClass = Class.forName(packageName + ".common.TCommon");
            getConsoleMethod = commonClass.getDeclaredMethod("getConsoleSource");
            getConsoleMethod.setAccessible(true);
            return (CommandSource<?, ?>) getConsoleMethod.invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to access getConsoleSource via reflection", e);
        }
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
    }
}
