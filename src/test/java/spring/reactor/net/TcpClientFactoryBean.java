package spring.reactor.net;

import org.springframework.beans.factory.FactoryBean;
import reactor.core.Environment;
import reactor.function.Consumer;
import reactor.io.Buffer;
import reactor.io.encoding.Codec;
import reactor.net.netty.tcp.NettyTcpClient;
import reactor.net.tcp.TcpClient;
import reactor.net.tcp.spec.TcpClientSpec;
import spring.reactor.Pojo;

/**
 *
 * @author Kent Yeh
 */
public class TcpClientFactoryBean implements FactoryBean<TcpClient<Pojo<String>, Pojo<String>>> {

    public static final String SYNC_DISPATCHER = "sync";
    private final Environment env;
    private final String dispatcher;
    private final Codec<Buffer, Pojo<String>, Pojo<String>> codec;
    private final String host;
    private final int port;
    private final Consumer<Throwable> errorCatch;

    public TcpClientFactoryBean(Environment env, String dispatcher, Codec<Buffer, Pojo<String>, Pojo<String>> codec, String host, int port) {
        this(env, dispatcher, codec, host, port, null);
    }

    public TcpClientFactoryBean(Environment env, String dispatcher, Codec<Buffer, Pojo<String>, Pojo<String>> codec, String host, int port,
            Consumer<Throwable> errorCatch) {
        this.env = env;
        this.dispatcher = dispatcher;
        this.codec = codec;
        this.host = host;
        this.port = port;
        this.errorCatch = errorCatch;
    }

    @Override
    public TcpClient<Pojo<String>, Pojo<String>> getObject() throws Exception {
        TcpClientSpec<Pojo<String>, Pojo<String>> client
                = new TcpClientSpec<Pojo<String>, Pojo<String>>(NettyTcpClient.class).
                env(env).dispatcher(dispatcher).
                codec(codec).
                connect(host, port);
        return errorCatch != null ? client.uncaughtErrorHandler(errorCatch).get()
                : client.get();
    }

    @Override
    public Class<?> getObjectType() {
        return TcpClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
