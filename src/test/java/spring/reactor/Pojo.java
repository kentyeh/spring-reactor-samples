package spring.reactor;

import java.io.Serializable;

/**
 *
 * @author Kent Yeh
 */
public class Pojo<T extends Serializable> implements Serializable{
    private static final long serialVersionUID = 4529567644332658079L;

    private T data;

    public Pojo() {
    }

    public Pojo(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data == null ? "" : data.toString();
    }

}
