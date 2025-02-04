package one.tranic.t.util;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Threads {

    /**
     * Provides an ExecutorService instance based on the current JVM version.
     * <p>
     * For JVM versions 21 and above, it attempts to create a virtual thread-per-task executor
     * using the new functionality introduced in the respective JVM version. For older versions,
     * it falls back to a ThreadPoolExecutor with specific configurations.
     *
     * @return an ExecutorService instance suitable for the current JVM version;
     * a virtual thread-per-task executor if supported, or a ThreadPoolExecutor otherwise.
     */
    public static ExecutorService getExecutor() {
        int jvmVersion = Sys.getCurrentJVMVersion();
        try {
            if (jvmVersion >= 21) {
                Class<?> executorsClass = Class.forName("java.util.concurrent.Executors");
                Method newVirtualThreadPerTaskExecutorMethod = executorsClass.getMethod("newVirtualThreadPerTaskExecutor");
                return (ExecutorService) newVirtualThreadPerTaskExecutorMethod.invoke(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ThreadPoolExecutor(
                0,
                4,
                15L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>()
        );
    }
}
