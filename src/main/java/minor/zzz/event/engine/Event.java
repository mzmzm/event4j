package minor.zzz.event.engine;

/**
 * 事件
 * Created by zhouzb on 2017/5/9.
 */
public class Event<T> {
    private T data;
    private String type;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
