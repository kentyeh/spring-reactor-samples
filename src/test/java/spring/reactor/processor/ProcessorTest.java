package spring.reactor.processor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.MatcherAssert;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.Reactor;
import reactor.core.processor.Operation;
import reactor.core.processor.Processor;
import reactor.event.Event;
import reactor.function.Consumer;
import spring.reactor.Pojo;

/**
 *
 * @author Kent Yeh
 */
@ContextConfiguration(classes = spring.reactor.processor.ProcessorContext.class)
public class ProcessorTest extends AbstractTestNGSpringContextTests implements InitializingBean {

    private static final Logger logger = LogManager.getLogger(ProcessorTest.class);

    private static final int POOL_SIZE = 3;
    private static final int INVO_CNT = ProcessorContext.BUFF_CNT;
    @Autowired
    private AtomicInteger serialno;
    @Autowired
    private Reactor reactor;

    @Autowired
    @Qualifier("singleProcPojo")
    Processor<Pojo> singleProcPojo1;

    @Autowired
    @Qualifier("singleProcPojo")
    Processor<Pojo> singleProcPojo2;

    @Autowired
    @Qualifier("multipleProcPojo")
    Processor<Pojo> multipleProcPojo1;

    @Autowired
    @Qualifier("multipleProcPojo")
    Processor<Pojo> multipleProcPojo2;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }

    @Test
    public void testSingle1() {
        Operation<Pojo> op = singleProcPojo1.prepare();
        String msg = String.format("%3d.singleProcPojo1's message.", serialno.incrementAndGet());
        logger.info("send {}", msg);
        op.get().setData(msg);
        op.commit();
    }

    @Test
    public void testSingle2() {
        singleProcPojo2.batch(INVO_CNT, new Consumer<Pojo>() {

            @Override
            public void accept(Pojo pojo) {
                String msg = String.format("%3d.singleProcPojo2's message.", serialno.incrementAndGet());
                logger.info("send {}", msg);
                pojo.setData(msg);
            }
        });
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT*3, invocationTimeOut = 10000)
    public void testMultiple1() throws InterruptedException {
        Operation<Pojo> op = multipleProcPojo1.prepare();
        String msg = String.format("%3d.multipleProcPojo1's message.", serialno.incrementAndGet());
        op.get().setData(msg);
        reactor.notify(Operation.class, Event.wrap(op));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 5000, successPercentage = 100)
    public void testMultiple2NotWork() throws InterruptedException {
        logger.fatal("It not works!");
        multipleProcPojo2.batch(INVO_CNT, new Consumer<Pojo>() {
            @Override
            public void accept(Pojo pojo) {
                String msg = String.format("%3d.multipleProcPojo2's message.", serialno.incrementAndGet());
                logger.info("send {}", msg);
                pojo.setData(msg);
            }
        });
    }
}
