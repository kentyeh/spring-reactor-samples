package spring.reactor.dynamic;

import spring.reactor.Pojo;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.function.Consumer;

/**
 *
 * @author Kent Yeh
 */
@ContextConfiguration(classes = spring.reactor.dynamic.DynamicContext.class)
public class DynamicTest extends AbstractTestNGSpringContextTests implements InitializingBean {

    private static final Logger logger = LogManager.getLogger(DynamicTest.class);
    public static final int POOL_SIZE = 3;
    public static final int INVO_CNT = 10;

    @Autowired
    DynamicDispatcher dynamicDispatcher;

    @Autowired
    private AtomicInteger serialno;

    @Override
    public void afterPropertiesSet() throws Exception {
        dynamicDispatcher.onMsg(new Consumer<String>() {

            @Override
            public void accept(String s) {
                logger.warn("DynamicTest receivev String:[{}]", s);
            }
        }).onStringHolder(new Consumer<Pojo<String>>() {

            @Override
            public void accept(Pojo<String> h) {
                logger.warn("DynamicTest receivev StringHolder:[{}]", h);
            }
        });
    }

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testStringDispatcher() {
        int serno = serialno.incrementAndGet();
        String msg = String.format("%3d.%s", serno, "Dynamic Dispatcher");
        logger.info("DynamicTest send String :{}", msg);
        dynamicDispatcher.notifyMsg(msg);
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testStringHolderDispatcher() {
        int serno = serialno.incrementAndGet();
        String msg = String.format("%3d.%s", serno, "Dynamic Dispatcher");
        logger.info("DynamicTest send String Holder:{}", msg);
        dynamicDispatcher.notifyStringHolder(new Pojo<>(msg));
    }
}
