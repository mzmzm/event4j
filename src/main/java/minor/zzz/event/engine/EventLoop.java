package minor.zzz.event.engine;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Created by zhouzb on 2017/5/17.
 */
public class EventLoop<T> extends AbstractExecutorService {
    private Executor executor;
    private BlockingQueue<Runnable> taskQueue;
    private volatile Thread thread;

    private static final int ST_NOT_STARTED = 1;
    private static final int ST_STARTED = 2;
    private static final int ST_SHUTTING_DOWN = 3;
    private static final int ST_SHUTDOWN = 4;
    private static final int ST_TERMINATED = 5;

    private static final AtomicIntegerFieldUpdater<EventLoop> STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(EventLoop.class, "state");

    private volatile int state = ST_NOT_STARTED;

    public EventLoop() {
        this.executor = new ThreadPerTaskExecutor(new DefaultThreadFactory());
        this.taskQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
    }

    public EventLoop(Executor executor) {
        this.executor = executor;
        this.taskQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
    }

    private void start() {
        if (STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)) {
            doStart();
        }
    }

    private void doStart() {
        assert thread == null;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                thread = Thread.currentThread();
                EventLoop.this.run();
            }
        });
    }

    private void runTask(Runnable task) {
        if (task == null) {
            return;
        }

        try {
            task.run();
        } catch (Throwable t) {
            System.out.println(String.format("A task raised an exception. Task: %s %s)", task, t));
        }
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Runnable task = taskQueue.take();
                runTask(task);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    };

    private void reject(Runnable task) {
        throw new RejectedExecutionException(" task queue is full");
    }

    public void addTask(Runnable task) throws RejectedExecutionException {
        if (STATE_UPDATER.get(this) == ST_NOT_STARTED) {
            start();
        }

        if (!taskQueue.offer(task)) {
//            reject(task);
        }
    }

    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }

    @Override
    public void shutdown() {
        if (isShutdown()) {
            return;
        }

        thread.interrupt();
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown();
        return null;
    }

    @Override
    public boolean isShutdown() {
        return STATE_UPDATER.get(this) >= ST_SHUTDOWN;
    }

    @Override
    public boolean isTerminated() {
        return STATE_UPDATER.get(this) == ST_TERMINATED;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }
}
