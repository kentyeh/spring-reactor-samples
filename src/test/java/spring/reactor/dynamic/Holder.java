package spring.reactor.dynamic;

/**
 *
 * @author kent
 */
public class Holder<T> {

    private final T data;

    public Holder(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return data == null ? "" : data.toString();
    }

}
