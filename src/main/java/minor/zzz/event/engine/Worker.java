package minor.zzz.event.engine;

import java.util.List;

/**
 * Created by zhouzb on 2017/5/21.
 */
public class Worker extends EventLoop {

    public <T> void process(Event<T> event, EventListener listener) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                listener.invoke(event);
            }
        };

        addTask(task);
    }
}
