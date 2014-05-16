package spring.reactor.dynamic;

import spring.reactor.Pojo;
import reactor.core.Environment;
import reactor.core.dynamic.DynamicReactor;
import reactor.core.dynamic.annotation.Dispatcher;
import reactor.function.Consumer;

/**
 *
 * @author Kent Yeh
 */
@Dispatcher(Environment.RING_BUFFER)
public interface DynamicDispatcher extends DynamicReactor {

    DynamicDispatcher onMsg(Consumer<String> consumer);

    DynamicDispatcher notifyMsg(String s);

    DynamicDispatcher onStringHolder(Consumer<Pojo<String>> consumer);

    DynamicDispatcher notifyStringHolder(Pojo<String> hs);
}
