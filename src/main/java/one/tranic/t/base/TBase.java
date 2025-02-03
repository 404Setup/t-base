package one.tranic.t.base;

import one.tranic.t.base.command.Operator;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class TBase {
    public final static Executor executor = new ThreadPoolExecutor(0, 8,
            30L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), Thread.ofVirtual().factory());

    private final static Operator operator = new Operator("Console", UUID.fromString("05b11eee-24db-4a21-ba9d-e12e8df9a92f"));

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
}
