package one.tranic.t.base.scheduler;

import java.util.concurrent.Future;

// TODO
public interface AsyncScheduler<R> {
    Future<R> runTask(Runnable runnable);
}
