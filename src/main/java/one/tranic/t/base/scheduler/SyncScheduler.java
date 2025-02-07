package one.tranic.t.base.scheduler;

import java.util.concurrent.Future;

// TODO
public interface SyncScheduler<R> {
    Future<R> runTask(Runnable runnable);
}
