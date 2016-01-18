package spring.reactor;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Kent Yeh
 * @param <T>
 */
@NoArgsConstructor
public class Pojo<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 4529567644332658079L;

    @Getter @Setter
    private T data;

    public Pojo(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return data == null ? "" : data.toString();
    }

}
