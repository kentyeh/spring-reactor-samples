package spring.reactor.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.function.Consumer;
import reactor.io.Buffer;
import reactor.net.NetChannel;
import reactor.net.tcp.TcpClient;
import spring.reactor.Pojo;

/**
 *
 * @author Kent Yeh
 */
@ContextConfiguration(classes = spring.reactor.net.NetContext.class)
@Log4j2
public class NetClientTest extends AbstractTestNGSpringContextTests {

    private static final int POOL_SIZE = 3;
    private static final int INVO_CNT = 8;
    @Autowired
    private AtomicInteger serialno;
    @Autowired
    private TcpClientFactoryBean tcpClientFactoryBean;

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testTcp() throws Exception {
        TcpClient<Pojo<String>, Pojo<String>> tcpClient = tcpClientFactoryBean.getObject();
        tcpClient.open().consume(new Consumer<NetChannel<Pojo<String>, Pojo<String>>>() {

            @Override
            public void accept(NetChannel<Pojo<String>, Pojo<String>> nc) {
                nc.in().consume(new Consumer<Pojo<String>>() {

                    @Override
                    public void accept(Pojo<String> s) {
                        log.debug("Client receive: {}", s);
                    }
                });
                nc.send(new Pojo(String.format("%03d.Hello World", serialno.incrementAndGet())));
            }
        });
    }

    @Test
    void testUdp() throws IOException, ClassNotFoundException {
        DatagramChannel udp = DatagramChannel.open();
        udp.configureBlocking(true);
        udp.connect(new InetSocketAddress(NetContext.UCP_SERVER_PORT));
        Pojo<String> pojo = new Pojo<>(String.format("%03d.Hello World", serialno.incrementAndGet()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(pojo);
        oos.flush();
        oos.close();
        udp.write(Buffer.wrap(baos.toByteArray()).byteBuffer());
        udp.close();
    }

}
