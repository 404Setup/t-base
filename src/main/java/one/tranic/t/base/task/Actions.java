package one.tranic.t.base.task;

import one.tranic.t.base.TBase;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Represents a wrapper for executing tasks either synchronously or asynchronously.
 * <p>
 * This class encapsulates a task defined by a {@link Supplier}, offering methods
 * for immediate execution or deferred execution using a shared executor.
 *
 * @param <T> the type of the result produced by the encapsulated task
 */
public class Actions<T> {
    /**
     * A supplier representing the encapsulated task to be executed.
     * <p>
     * This task produces a result of type {@code T} when executed.
     * It can be run either synchronously or asynchronously.
     */
    private final Supplier<T> task;

    /**
     * Constructs a new {@code Actions} instance with the specified task.
     *
     * @param task the {@code Supplier} representing the task to be executed
     */
    public Actions(Supplier<T> task) {
        this.task = task;
    }

    /**
     * Executes the encapsulated task synchronously and returns its result.
     *
     * @return the result produced by the encapsulated task
     */
    public T sync() {
        return task.get();
    }

    /**
     * Executes the encapsulated task asynchronously using a shared executor.
     *
     * @return a {@code CompletableFuture<T>} that completes with the result of the asynchronous task execution
     */
    public CompletableFuture<T> async() {
        return TBase.runAsync(task);
    }
}
