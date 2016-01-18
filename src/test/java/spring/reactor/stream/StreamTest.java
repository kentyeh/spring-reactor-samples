package spring.reactor.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.log4j.Log4j2;
import org.hamcrest.Matcher;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.composable.Deferred;
import reactor.core.composable.Stream;
import reactor.core.composable.spec.Streams;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.event.selector.Selectors;
import reactor.function.Consumer;
import reactor.function.Function;
import reactor.function.Predicate;
import reactor.function.support.Boundary;
import reactor.function.support.Tap;
import reactor.tuple.Tuple2;

/**
 *
 * @author Kent Yeh
 */
@ContextConfiguration(classes = spring.reactor.stream.StreamContext.class)
@Log4j2
public class StreamTest extends AbstractTestNGSpringContextTests implements InitializingBean {

    private static final int POOL_SIZE = 3;
    private static final int INVO_CNT = 10;
    private final Random random = new Random();

    @Autowired
    Environment env;
    @Autowired
    Deferred<String, Stream<String>> simpleStream;
    @Autowired
    Deferred<String, Stream<String>> upperCaseStream;
    @Autowired
    Deferred<String, Stream<String>> halfNotifyStream;
    @Autowired
    AtomicInteger serialno;

    private final Consumer<String> stringConsumer = new Consumer<String>() {

        @Override
        public void accept(String s) {
            log.warn("Stream consume [{}]", s);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        simpleStream.compose().consume(stringConsumer);
        upperCaseStream.compose().map(new Function<String, String>() {

            @Override
            public String apply(String s) {
                return s.toUpperCase();
            }

        }).filter(new Predicate<String>() {

            @Override
            public boolean test(String s) {
                if (!random.nextBoolean()) {
                    log.error("Unfortunately, [{}] not pass.", s);
                    return false;
                }
                return true;
            }
        }).consume(stringConsumer);
        final Boundary boundary = new Boundary();
        halfNotifyStream.compose().filter(new Predicate<String>() {

            @Override
            public boolean test(String s) {
                if (boundary.await(10, TimeUnit.MILLISECONDS)) {
                    log.debug("halfNotifyStream with [{}] had run over half.", s);
                }
                return true;
            }
        }).consume(boundary.bind(stringConsumer, INVO_CNT / 2));
    }

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }

    //@Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    public void testSimple() {
        String msg = String.format("%3d.simpleStream say \"Hello\"", serialno.incrementAndGet());
        log.debug("testSimple send [{}]", msg);
        simpleStream.accept(msg);
    }

    //@Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    public void testUpperStream() {
        String msg = String.format("%3d.testUpperStream say \"Hello\"", serialno.incrementAndGet());
        log.debug("testSimple send [{}]", msg);
        upperCaseStream.accept(msg);
    }

    //@Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    public void testBoundaryStream() {

        String msg = String.format("%3d.testBoundaryStream say \"Hello\"", serialno.incrementAndGet());
        log.debug("testSimple send [{}]", msg);

        halfNotifyStream.accept(msg);
    }

    //@Test
    public void test() {
        final int sum = 0;
        Stream<String> stream = Streams.defer(Arrays.asList(
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")).get();
        Stream<Integer> stream2 = stream.map(new Function<String, Integer>() {

            @Override
            public Integer apply(String s) {
                return Integer.parseInt(s);
            }
        }).filter(new Predicate<Integer>() {

            @Override
            public boolean test(Integer t) {
                return 0 == t % 2;
            }
        }).collect().consume(new Consumer<List<Integer>>() {

            @Override
            public void accept(List<Integer> li) {
                for (Integer i : li) {
                    log.warn(" ---> {}  ", i);
                }
            }

        }).reduce(new Function<Tuple2<List<Integer>, Integer>, Integer>() {

            @Override
            public Integer apply(Tuple2<List<Integer>, Integer> t) {
                int sum = 0;
                for (Integer i : t.getT1()) {
                    sum += i;
                }
                log.warn("summary is {}", sum);
                return sum;
            }
        }).flush();
    }

    //@Test
    public void testComposeFromMultipleValues() throws InterruptedException {
        Stream<Integer> stream = Streams.defer(Arrays.asList(
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10)).get()
                .map(new Function<Integer, Integer>() {
                    int sum = 0;

                    @Override
                    public Integer apply(Integer i) {
                        log.debug("{} += {}", sum, i);
                        sum += i;
                        return sum;
                    }

                });
        await(10, stream, is((equalTo(55))));
    }

//    @Test
    public void testFirstAndLast() throws InterruptedException {
        Stream<Integer> stream = Streams.defer(Arrays.asList(
                1, 2, 3, 4, 5)).get();
        Tap<Integer> first = stream.first().tap();
        Tap<Integer> last = stream.last().tap();
        stream.flush();

        assertThat("First is 1", first.get(), is(1));
        assertThat("Last is 5", last.get(), is(5));
    }

//    @Test
    public void testMapReduce() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        Stream<String> stream = Streams.defer(Arrays.asList(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")).get();
        Stream<Integer> streams2 = stream.map(new Function<String, Integer>() {

            @Override
            public Integer apply(String s) {
                return Integer.parseInt(s);
            }
        }).filter(new Function<Integer, Boolean>() {

            @Override
            public Boolean apply(Integer t) {
                return t < 6;
            }
        }).reduce(new Function<Tuple2<Integer, Integer>, Integer>() {
            @Override
            public Integer apply(Tuple2<Integer, Integer> r) {
                log.debug("{} x {} = {}", r.getT1(), r.getT2(), r.getT1() * r.getT2());
                return r.getT1() * r.getT2();
            }
        }, 1);

//        Stream<List<Integer>> stream3 = streams2.collect().
        await(5, streams2, is((equalTo(120))));
    }

    @Test
    public void testRelaysEventsToReactor() throws InterruptedException {
        Reactor r = Reactors.reactor().get();
        reactor.event.selector.Selector key = Selectors.$();

        final CountDownLatch latch = new CountDownLatch(5);
        r.on(key, new Consumer<Event<Integer>>() {
            @Override
            public void accept(Event<Integer> integerEvent) {
                log.debug("-------> {}",integerEvent.getData());
                latch.countDown();
            }
        });

        Stream<Integer> s = Streams.defer(Arrays.asList(1, 2, 3, 4, 5)).get()
                .consume(key.getObject(), r);
        Tap<Integer> tap = s.tap();

        s.flush(); // Trigger the deferred value to be set
        assertThat("latch was counted down", latch.getCount(), is(0l));
        assertThat("value is 5", tap.get(), is(5));
    }

    private <T> void await(Stream<T> s, Matcher<T> expected) throws InterruptedException {
        await(1, s, expected);
    }

    private <T> void await(int count, Stream<T> s, Matcher<T> expected) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(count);
        final AtomicReference<T> ref = new AtomicReference<>();
        s.consume(new Consumer<T>() {
            @Override
            public void accept(T t) {
                ref.set(t);
                latch.countDown();
            }
        }).when(Exception.class, new Consumer<Exception>() {
            @Override
            public void accept(Exception ex) {
                log.error(ex);
                latch.countDown();
            }
        }).flush();

        long startTime = System.currentTimeMillis();
        T result = null;
        try {
            latch.await(1, TimeUnit.SECONDS);
            result = ref.get();
        } catch (InterruptedException ex) {
            log.error(ex);
        }
        long duration = System.currentTimeMillis() - startTime;
        log.debug(s.debug());
        assertThat(result, expected);
        assertThat(duration, is(lessThan(2000L)));
    }
}
