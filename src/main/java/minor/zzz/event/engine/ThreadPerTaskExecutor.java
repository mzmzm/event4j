package minor.zzz.event.engine;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Created by zhouzb on 2017/5/21.
 */
public class ThreadPerTaskExecutor implements Executor {
    private final ThreadFactory threadFactory;

    public ThreadPerTaskExecutor(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException("threadFactory");
        }

        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Runnable command) {
        threadFactory.newThread(command).start();
    }
}
