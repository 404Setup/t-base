package one.tranic.t.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * Provides utility methods for working with threads, including creating
 * virtual threads, thread factories, and executors.
 * <p>
 * This class supports fallback mechanisms for environments where virtual threads are not
 * available, allowing compatibility across different JVM versions.
 */
public class Threads {

    private static final Method METHOD_OF_VIRTUAL;
    private static final Method METHOD_VIRTUAL_FACTORY;
    private static final Method METHOD_VIRTUAL_PER_TASK_EXECUTOR;

    static {
        boolean virtualSupported = isVirtualThreadSupported();
        METHOD_OF_VIRTUAL = virtualSupported ? getMethodWithFallback(Thread.class, "ofVirtual", true) : null;
        METHOD_VIRTUAL_FACTORY = virtualSupported ? getVirtualThreadFactoryMethod() : null;
        METHOD_VIRTUAL_PER_TASK_EXECUTOR = virtualSupported ? getMethodWithFallback(Executors.class, "newVirtualThreadPerTaskExecutor", true) : null;
    }

    /**
     * Creates and returns a thread factory for creating virtual threads.
     * Virtual threads provide a lightweight threading model suitable for
     * concurrent programming, allowing the creation of a large number of threads
     * while minimizing resource consumption.
     *
     * @return a ThreadFactory instance configured to create virtual threads, or
     * null if virtual threads are not supported in the current JVM.
     */
    public static ThreadFactory newVirtualThreadFactory() {
        return createVirtualThreadFactory();
    }

    /**
     * Creates a new {@link ExecutorService} that assigns a virtual thread to each submitted task.
     * This method provides an executor service designed for lightweight concurrency tasks using virtual threads.
     *
     * @return a new {@link ExecutorService} configured to create a virtual thread for every task,
     * or {@code null} if virtual threads are not supported on the current JVM.
     */
    public static ExecutorService newVirtualThreadPerTaskExecutor() {
        return createVirtualThreadPerTaskExecutor();
    }

    /**
     * Creates a new virtual thread factory if supported by the current JVM; otherwise,
     * falls back to the default thread factory implementation.
     *
     * @return a {@link ThreadFactory} instance that creates virtual threads if supported, or
     * a default thread factory if virtual threads are not available.
     */
    public static @NotNull ThreadFactory newVirtualThreadFactoryOrDefault() {
        ThreadFactory factory = createVirtualThreadFactory();
        return factory != null ? factory : Executors.defaultThreadFactory();
    }

    /**
     * Creates a new virtual thread-per-task executor if supported by the runtime environment,
     * otherwise returns the provided fallback executor.
     *
     * @param fallbackExecutor the executor service to fallback to if virtual threads are not supported
     * @return a virtual thread-per-task executor if available, otherwise the provided fallback executor
     */
    public static ExecutorService newVirtualThreadPerTaskExecutorOrProvided(ExecutorService fallbackExecutor) {
        ExecutorService executor = createVirtualThreadPerTaskExecutor();
        return executor != null ? executor : fallbackExecutor;
    }

    /**
     * Provides an {@link ExecutorService} with specific configuration by default.
     * It utilizes a factory method to create an executor with a thread pool having
     * an unlimited queue size but bounded by the given core and maximum pool size for concurrency management.
     *
     * @return an {@link ExecutorService} pre-configured with the default settings of
     * corePoolSize as 0, maximumPoolSize as 4, a keep-alive time of 15 seconds,
     * and a {@link SynchronousQueue} as the task queue.
     */
    public static ExecutorService getExecutor() {
        return getExecutor(0,
                4,
                15L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    /**
     * Creates and returns an {@link ExecutorService} instance. If virtual thread support is available and properly configured,
     * a virtual thread-based executor service will be created. Otherwise, a traditional {@link ThreadPoolExecutor} is used.
     *
     * @param corePoolSize    the number of core threads to keep in the pool, even if they are idle.
     * @param maximumPoolSize the maximum number of threads allowed in the pool.
     * @param keepAliveTime   the maximum time that excess idle threads will wait for new tasks before terminating.
     * @param unit            the time unit for the keepAliveTime argument.
     * @param workQueue       the queue to use for holding tasks before they are executed.
     * @return an {@link ExecutorService} instance based on the specified configurations or a virtual thread-based executor if supported.
     */
    public static ExecutorService getExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        if (isVirtualThreadSupported() && METHOD_VIRTUAL_PER_TASK_EXECUTOR != null) {
            try {
                return (ExecutorService) METHOD_VIRTUAL_PER_TASK_EXECUTOR.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue);
    }

    /**
     * Creates and returns an {@link ExecutorService} using the specified parameters.
     * <p>
     * This executor operates as a thread pool with configurations provided by the caller,
     * utilizing a custom thread factory for virtual threads if supported.
     *
     * @param corePoolSize    the number of core threads to keep in the pool, even if they are idle.
     * @param maximumPoolSize the maximum number of threads allowed in the pool.
     * @param keepAliveTime   the maximum time that excess idle threads will wait for new tasks before terminating.
     * @param unit            the time unit for the keepAliveTime argument.
     * @param workQueue       the queue to use for holding tasks before they are executed.
     * @return an {@link ExecutorService} instance configured according to the specified parameters.
     */
    public static ExecutorService getPureExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                newVirtualThreadFactoryOrDefault());
    }

    /**
     * Creates a virtual thread-per-task executor if the current JVM supports virtual threads and the necessary
     * method reference is available. If virtual threads are not supported or the method reference is unavailable,
     * this method returns null.
     *
     * @return an {@link ExecutorService} for executing tasks in virtual threads, or null if virtual threads are not
     * supported or the method reference cannot be invoked.
     */
    private static @Nullable ExecutorService createVirtualThreadPerTaskExecutor() {
        if (!isVirtualThreadSupported() || METHOD_VIRTUAL_PER_TASK_EXECUTOR == null) return null;
        try {
            return (ExecutorService) METHOD_VIRTUAL_PER_TASK_EXECUTOR.invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a ThreadFactory instance for constructing virtual threads if supported by the current JVM.
     * If virtual threads are not supported or required methods are not available, returns null.
     *
     * @return a ThreadFactory for virtual threads, or null if virtual threads are not supported or cannot be created.
     */
    private static @Nullable ThreadFactory createVirtualThreadFactory() {
        if (!isVirtualThreadSupported() || METHOD_OF_VIRTUAL == null || METHOD_VIRTUAL_FACTORY == null) return null;
        try {
            Object builder = METHOD_OF_VIRTUAL.invoke(null);
            return (ThreadFactory) METHOD_VIRTUAL_FACTORY.invoke(builder);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Checks if virtual threads are supported on the current JVM.
     *
     * @return true if the current JVM version is 21 or higher, indicating support for virtual threads; false otherwise.
     */
    private static boolean isVirtualThreadSupported() {
        return Sys.getCurrentJVMVersion() >= 21;
    }

    /**
     * Attempts to retrieve a method with the specified name from the given class. If successful, the method
     * is optionally made accessible based on the provided parameter. If the method cannot be found or any
     * exception occurs during retrieval, returns null.
     *
     * @param clazz      the class from which the method is to be retrieved
     * @param methodName the name of the method to retrieve
     * @param accessible whether the method should be made accessible
     * @return the retrieved Method instance, or null if the method cannot be found or an exception occurs
     */
    private static @Nullable Method getMethodWithFallback(Class<?> clazz, String methodName, boolean accessible) {
        try {
            Method method = clazz.getMethod(methodName);
            if (accessible) method.setAccessible(true);
            return method;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieves the method reference for creating a virtual thread factory, if supported by the JVM.
     * This method attempts to dynamically locate the necessary methods using reflection.
     *
     * @return the Method object representing the virtual thread factory creation method,
     * or null if the required methods are not accessible or unsupported.
     */
    private static @Nullable Method getVirtualThreadFactoryMethod() {
        try {
            Method methodOfVirtual = Thread.class.getMethod("ofVirtual");
            methodOfVirtual.setAccessible(true);
            Class<?> builderClass = Class.forName("java.lang.Thread$Builder");
            Method factoryMethod = builderClass.getMethod("factory");
            factoryMethod.setAccessible(true);
            return factoryMethod;
        } catch (Exception e) {
            return null;
        }
    }
}