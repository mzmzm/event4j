package minor.zzz.event.engine;

/**
 * 事件监听器
 * Created by zhouzb on 2017/5/9.
 */
public interface EventListener {
    <T> void invoke(Event<T> event);
}
