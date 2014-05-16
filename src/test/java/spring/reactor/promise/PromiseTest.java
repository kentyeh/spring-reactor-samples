package spring.reactor.promise;

import java.lang.reflect.Array;
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
import reactor.core.Reactor;
import reactor.core.composable.Deferred;
import reactor.core.composable.Promise;
import reactor.event.Event;
import reactor.function.Consumer;
import spring.reactor.reactor.SimpleTest;

;

/**
 *
 * @author Kent Yeh
 */
@ContextConfiguration(classes = spring.reactor.promise.PromiseContext.class)
public class PromiseTest extends AbstractTestNGSpringContextTests implements InitializingBean {

    private static final Logger logger = LogManager.getLogger(SimpleTest.class);
    private static final int POOL_SIZE = 3;
    private static final int INVO_CNT = 10;
    @Autowired
    private AtomicInteger serialno;
    @Autowired
    private PromiseBeanFactory promiseBeanFactory;
    @Autowired
    Reactor reactor;

    private final AtomicInteger counter1 = new AtomicInteger(0);
    private final AtomicInteger counter2 = new AtomicInteger(0);

    Deferred<String, Promise<String>>[] promises1
            = (Deferred<String, Promise<String>>[]) Array.newInstance(Deferred.class, INVO_CNT);
    Deferred<String, Promise<String>>[] promises2
            = (Deferred<String, Promise<String>>[]) Array.newInstance(Deferred.class, INVO_CNT);
    Deferred<String, Promise<String>>[] promises3
            = (Deferred<String, Promise<String>>[]) Array.newInstance(Deferred.class, INVO_CNT);
    Deferred<String, Promise<String>>[] promises4
            = (Deferred<String, Promise<String>>[]) Array.newInstance(Deferred.class, INVO_CNT);

    private final Consumer<Promise<String>> promiseConsumer = new Consumer<Promise<String>>() {

        @Override
        public void accept(Promise<String> t) {
            logger.warn("promise complete with {}", t);
        }
    };
    private final Consumer<String> stringConsumer = new Consumer<String>() {

        @Override
        public void accept(String s) {
            logger.warn("promise success with {}", s);
        }

    };
    private final Consumer<Throwable> exConsumer = new Consumer<Throwable>() {

        @Override
        public void accept(Throwable ex) {
            logger.error("promise error:{}", ex.getMessage());
        }

    };

    @Override

    public void afterPropertiesSet() throws Exception {
        for (int i = 0; i < INVO_CNT; i++) {
            promises1[i] = promiseBeanFactory.getObject();
            promises2[i] = promiseBeanFactory.getObject();
            promises3[i] = promiseBeanFactory.getObject();
            promises4[i] = promiseBeanFactory.getObject();
        }
        for (Deferred<String, Promise<String>> p : promises1) {
            p.compose().onComplete(promiseConsumer).onSuccess(stringConsumer).onError(exConsumer);
        }
        for (Deferred<String, Promise<String>> p : promises2) {
            p.compose().onComplete(promiseConsumer).onSuccess(stringConsumer).onError(exConsumer);
        }
    }

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testPromise1() {
        int serno = counter1.getAndIncrement();
        String msg = String.format("%4d.%s", serno, "Promise1 message");
        logger.info("ReplyTest send :{}", msg);
        if (serno % 5 == 0) {
            promises1[serno].accept(new RuntimeException(msg));
        } else {
            promises1[serno].accept(msg);
        }
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testPromise2() {
        reactor.notify("promise2Handler", Event.wrap(promises2[counter2.getAndIncrement()]));
    }

    @Test
    public void testPromiseWithError() throws InterruptedException {
        for (int i = 0; i < promises3.length; i++) {
            final Deferred<String, Promise<String>> deferred = promises3[i];

            Promise<String> promise = deferred.compose();
            promise.onSuccess(new Consumer<String>() {

                @Override
                public void accept(String s) {
                    if (s.equals(String.valueOf(promises3.length / 3)) || s.equals(String.valueOf(promises3.length - promises3.length / 3))) {
                        throw new IllegalArgumentException("Error raising test");
                    }
                    logger.debug("---> {}", s);
                }
            }).onError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) {
                    logger.error(t.getMessage());
                }
            });
            deferred.accept(String.valueOf(i));
        }
    }

    @Test
    public void testSchedulePromise() throws InterruptedException {
        for (int i = 0; i < promises4.length; i++) {
            final Deferred<String, Promise<String>> deferred = promises4[i];

            Promise<String> promise = deferred.compose();
            promise.onSuccess(new Consumer<String>() {

                @Override
                public void accept(String s) {
                    if (s.equals(String.valueOf(promises4.length / 2))) {
                        throw new IllegalArgumentException("Error raising test");
                    }
                    logger.debug("---> {}", s);
                }
            }).onError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) {
                    logger.error(t.getMessage());
                }
            });
            reactor.schedule(new Consumer<Integer>() {

                @Override
                public void accept(Integer i) {
                    logger.debug("{} -->", i);
                    deferred.accept(String.valueOf(i));
                }

            }, i);
        }
    }

}
