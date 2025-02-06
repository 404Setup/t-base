package one.tranic.t.base.scheduler;

// TODO
public interface AsyncScheduler<R> {
    R runTask(Runnable runnable);
}
