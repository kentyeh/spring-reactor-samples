package spring.reactor.net;

import io.netty.util.NetUtil;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.function.Consumer;
import reactor.io.encoding.JavaSerializationCodec;
import reactor.net.netty.tcp.NettyTcpServer;
import reactor.net.tcp.TcpServer;
import reactor.net.tcp.spec.TcpServerSpec;
import reactor.spring.context.config.EnableReactor;
import reactor.net.NetChannel;
import reactor.net.config.ServerSocketOptions;
import reactor.net.netty.udp.NettyDatagramServer;
import reactor.net.tcp.support.SocketUtils;
import reactor.net.udp.DatagramServer;
import reactor.net.udp.spec.DatagramServerSpec;
import spring.reactor.Pojo;

/**
 *
 * @author Kent Yeh
 */
@Configuration
@EnableReactor
public class NetContext implements InitializingBean, DisposableBean {

    private static final Logger logger = LogManager.getLogger(NetContext.class);
    @Autowired
    Environment env;
    private static final int TCP_SERVER_PORT = 8080;
    public static final int UCP_SERVER_PORT = SocketUtils.findAvailableTcpPort();

    private TcpServer<Pojo<String>, Pojo<String>> tcpserver = null;
    private DatagramServer<Pojo<String>, Pojo<String>> udpserver = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        tcpserver = new TcpServerSpec<Pojo<String>, Pojo<String>>(NettyTcpServer.class)
                .env(env)
                .listen(TCP_SERVER_PORT)
                .dispatcher(Environment.RING_BUFFER)
                .codec(new JavaSerializationCodec<Pojo<String>>())
                .consume(new Consumer<NetChannel<Pojo<String>, Pojo<String>>>() {

                    @Override
                    public void accept(final NetChannel<Pojo<String>, Pojo<String>> nc) {
                        nc.in().consume(new Consumer<Pojo<String>>() {

                            @Override
                            public void accept(Pojo<String> pojo) {
                                logger.warn("Server recieive:[{}]", pojo);
                                pojo.setData(String.format("%3d.server response:%s", serialno().incrementAndGet(), pojo.getData()));
                                nc.send(pojo);
                            }
                        });
                    }
                }).get();
        tcpserver.start().await(5, TimeUnit.SECONDS);

        udpserver = new DatagramServerSpec<Pojo<String>, Pojo<String>>(NettyDatagramServer.class)
                .env(env)
                .listen(UCP_SERVER_PORT)
                .dispatcher(Environment.RING_BUFFER)
                .codec(new JavaSerializationCodec<Pojo<String>>())
                .consumeInput(new Consumer<Pojo<String>>() {

                    @Override
                    public void accept(Pojo<String> pojo) {
                        logger.warn("Server recieive:[{}]", pojo);
                    }
                }).get();
        udpserver.start();
    }

    @Override
    public void destroy() throws Exception {
        if (tcpserver != null) {
            tcpserver.shutdown().await(5, TimeUnit.SECONDS);
        }
        if (udpserver != null) {
            udpserver.shutdown().await(5, TimeUnit.SECONDS);
        }
    }

    @Bean
    public AtomicInteger serialno() {
        return new AtomicInteger(0);
    }

    @Bean
    public TcpClientFactoryBean tcpClientFactoryBean() {
        return new TcpClientFactoryBean(env, TcpClientFactoryBean.SYNC_DISPATCHER,
                new JavaSerializationCodec<Pojo<String>>(), "127.0.0.1", TCP_SERVER_PORT,
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) {
                        logger.error("TcpClient catch {}", t.getMessage());
                    }
                });
    }

}
