package spring.reactor.processor;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.Environment;
import reactor.core.processor.Processor;
import reactor.core.processor.spec.ProcessorSpec;
import reactor.function.Consumer;
import reactor.function.Supplier;
import spring.reactor.Pojo;

/**
 *
 * @author Kent Yeh
 */
@ContextConfiguration(classes = spring.reactor.processor.ProcessorContext.class)
@Log4j2
public class StrategyTest extends AbstractTestNGSpringContextTests {

    private static final int RUNS = 250000000;
    private static final int BATCH_SIZE = 512;
    //private long start;
    //private String waitStrategy;
    private static final Supplier<Pojo> dataSupplier = new Supplier<Pojo>() {
        @Override
        public Pojo get() {
            return new Pojo();
        }
    };
    private static final Consumer<Pojo> dataConsumer = new Consumer<Pojo>() {
        @Override
        public void accept(Pojo data) {
            data.setData("test");
        }

    };
    private static final Consumer<Pojo> nullConsumer = new Consumer<Pojo>() {
        @Override
        public void accept(Pojo data) {
        }
    };
    @Autowired
    Environment env;

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }

  /*@AfterMethod
    public void cleanup() {
        long end = System.currentTimeMillis();
        long elapsed = (end - start);
        long throughput = Math.round(RUNS / ((double) elapsed / 1000));
        logger.debug("{}  > elapsed: {}ms, throughput: {}/sec", waitStrategy, elapsed, throughput);
    }*/

    @Test
    public void testProcessorThroughput() throws InterruptedException {
        Processor<Pojo> proc = new ProcessorSpec<Pojo>()
                .dataSupplier(dataSupplier)
                .consume(nullConsumer)
                .blockingWaitStrategy()
                .get();

        long start;
        String waitStrategy;
        waitStrategy = "default";
        start = System.currentTimeMillis();

        int runs = RUNS / BATCH_SIZE;

        for (int i = 0; i < runs; i++) {
            proc.batch(BATCH_SIZE, dataConsumer);
        }
        long end = System.currentTimeMillis();
        long elapsed = (end - start);
        long throughput = Math.round(RUNS / ((double) elapsed / 1000));
        log.debug("{}  > elapsed: {}ms, throughput: {}/sec", waitStrategy, elapsed, throughput);
    }

    @Test
    public void testBlockingWaitStrategyThroughput() throws InterruptedException {
        Processor<Pojo> proc = new ProcessorSpec<Pojo>()
                .dataSupplier(dataSupplier)
                .consume(nullConsumer)
                .blockingWaitStrategy()
                .get();

        long start;
        String waitStrategy;
        waitStrategy = "blockingWaitStrategy";
        start = System.currentTimeMillis();

        int runs = RUNS / BATCH_SIZE;
        for (int i = 0; i < runs; i++) {
            proc.batch(BATCH_SIZE, dataConsumer);
        }
        long end = System.currentTimeMillis();
        long elapsed = (end - start);
        long throughput = Math.round(RUNS / ((double) elapsed / 1000));
        log.debug("{}  > elapsed: {}ms, throughput: {}/sec", waitStrategy, elapsed, throughput);
    }

    @Test
    public void testBusySpinWaitStrategyThroughput() throws InterruptedException {
        Processor<Pojo> proc = new ProcessorSpec<Pojo>()
                .dataSupplier(dataSupplier)
                .consume(nullConsumer)
                .busySpinWaitStrategy()
                .get();

        long start;
        String waitStrategy;
        waitStrategy = "busySpinWaitStrategy";
        start = System.currentTimeMillis();

        int runs = RUNS / BATCH_SIZE;
        for (int i = 0; i < runs; i++) {
            proc.batch(BATCH_SIZE, dataConsumer);
        }
        long end = System.currentTimeMillis();
        long elapsed = (end - start);
        long throughput = Math.round(RUNS / ((double) elapsed / 1000));
        log.debug("{}  > elapsed: {}ms, throughput: {}/sec", waitStrategy, elapsed, throughput);
    }

    @Test
    public void testSleepingWaitStrategyThroughput() throws InterruptedException {
        Processor<Pojo> proc = new ProcessorSpec<Pojo>()
                .dataSupplier(dataSupplier)
                .consume(nullConsumer)
                .sleepingWaitStrategy()
                .get();

        long start;
        String waitStrategy;
        waitStrategy = "sleepingWaitStrategy";
        start = System.currentTimeMillis();

        int runs = RUNS / BATCH_SIZE;
        for (int i = 0; i < runs; i++) {
            proc.batch(BATCH_SIZE, dataConsumer);
        }
        long end = System.currentTimeMillis();
        long elapsed = (end - start);
        long throughput = Math.round(RUNS / ((double) elapsed / 1000));
        log.debug("{}  > elapsed: {}ms, throughput: {}/sec", waitStrategy, elapsed, throughput);
    }

    @Test
    public void testYieldingWaitStrategyThroughput() throws InterruptedException {
        Processor<Pojo> proc = new ProcessorSpec<Pojo>()
                .dataSupplier(dataSupplier)
                .consume(nullConsumer)
                .yieldingWaitStrategy()
                .get();

        long start;
        String waitStrategy;
        waitStrategy = "yieldingWaitStrategy";
        start = System.currentTimeMillis();

        int runs = RUNS / BATCH_SIZE;
        for (int i = 0; i < runs; i++) {
            proc.batch(BATCH_SIZE, dataConsumer);
        }
        long end = System.currentTimeMillis();
        long elapsed = (end - start);
        long throughput = Math.round(RUNS / ((double) elapsed / 1000));
        log.debug("{}  > elapsed: {}ms, throughput: {}/sec", waitStrategy, elapsed, throughput);
    }

}
