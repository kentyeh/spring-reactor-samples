package spring.reactor.dynamic;

import lombok.Getter;

/**
 *
 * @author Kent Yeh
 */
public class Holder<T> {

    private final @Getter T data;

    public Holder(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data == null ? "" : data.toString();
    }

}
