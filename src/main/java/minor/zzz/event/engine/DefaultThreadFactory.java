package minor.zzz.event.engine;

import java.util.concurrent.ThreadFactory;

/**
 * Created by zhouzb on 2017/5/17.
 */
public class DefaultThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        Thread nThread = new Thread(r);
        nThread.setUncaughtExceptionHandler(new DefaultTreadExceptionHandler());
        return nThread;
    }
}

class DefaultTreadExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println(" i got you ");
    }
}
