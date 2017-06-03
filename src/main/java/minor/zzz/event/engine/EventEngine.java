package minor.zzz.event.engine;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * 事件驱动引擎
 * Created by zhouzb on 2017/5/3.
 */
public class EventEngine {

    // singleton
    private EventEngine() {

    }

    private static final class EventEngineHolder {
        public static final EventEngine _engine = new EventEngine();
    }

    public static EventEngine getInstance() {
        return EventEngineHolder._engine;
    }

    private volatile boolean isRunning = false;
    public boolean isRunning() {
        return isRunning;
    }

    // properties
    private ConcurrentMap<String, List<EventListener>> subscriber;
    private Acceptor acceptor;
    private WorkerGroup workerGroup;

    // 注册监听器
    public <T> void registerListener(String eventType, EventListener listener) {
        if (subscriber == null) {
            throw new EventException("## event engine have not been init...");
        }

        if (!subscriber.containsKey(eventType)) {
            subscriber.putIfAbsent(eventType, Collections.synchronizedList(new ArrayList<EventListener>()));
        }

        subscriber.get(eventType).add(listener);
    }

    // 派发事件
    public <T> void dispatchEvent(Event<T> event) {
        if (!isRunning) {
            throw new EventException("## event engine is shut down...");
        }

        acceptor.register(event);
    }

    public void start() {
        if (isRunning) {
            throw new EventException("## event engine is running...");
        }

        subscriber = new ConcurrentHashMap<>();

        workerGroup = new WorkerGroup(16);

        acceptor = new Acceptor(workerGroup);

        isRunning = true;
    }

    private List<EventListener> getEventListener(String type) {
        return subscriber.get(type);
    }

    private class Acceptor extends EventLoop {

        private WorkerGroup workerGroup;

        public Acceptor(WorkerGroup workerGroup) {
            this.workerGroup = workerGroup;
        }

        private <T> Runnable addEvent(Event<T> event) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    List<EventListener> listeners = EventEngine.getInstance().getEventListener(event.getType());
                    if (CollectionUtils.isEmpty(listeners)) {
                        return;
                    }

                    for (EventListener listener : listeners) {
                        workerGroup.next().process(event, listener);
                    }
                }
            };

            return run;
        }

        public <T> void register(Event<T> event) {
            try {
                addTask(addEvent(event));
            } catch (RejectedExecutionException e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
