package spring.reactor.reactor;

import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.Reactor;
import reactor.event.Event;

/**
 *
 * @author Kent Yeh
 */
@ContextConfiguration(classes = spring.reactor.reactor.ReactorContext.class)
@Log4j2
public class SimpleTest extends AbstractTestNGSpringContextTests {

    private static final int POOL_SIZE = 3;
    private static final int INVO_CNT = 10;

    @Autowired
    Reactor reactor;
    @Autowired
    Reactor jsonPathReactor;
    @Autowired
    private AtomicInteger serialno;

    @BeforeClass
    public void setup() {
    }

    @AfterClass
    public void teardown() {
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testString() {
        String msg = String.format("%4d.%s", serialno.incrementAndGet(), "Hello World");
        log.info("ReactorTest send:{}", msg);
        reactor.notify("simple.string", Event.wrap(msg));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testException() {
        String msg = String.format("%4d.%s", serialno.incrementAndGet(), "Reactor Exception event");
        log.info("ReactorTest send:{}", msg);
        reactor.notify(RuntimeException.class, Event.wrap(new RuntimeException(msg)));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testUri() throws URISyntaxException {
        int sno = serialno.incrementAndGet();
        //String uri = "/photo/kent.png"; ----Not work
        String uri = String.format("/photo/%d_kent", sno);
        String msg = String.format("%4d.%s", sno, "Hello World");
        log.info("ReactorTest send {} with {}", uri, msg);
        reactor.notify(uri, Event.wrap(msg));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testRegex() {
        String phoneno = String.format("09%08d", serialno.incrementAndGet());
        log.info("ReactorTest send:{}", phoneno);
        reactor.notify(phoneno, Event.wrap(phoneno));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testJsonpath() {
        JSONObject json = new JSONObject();
        JSONArray ja = new JSONArray();
        Integer sno = serialno.incrementAndGet();
        ja.add(String.format("%4d.%s", sno, "Kent"));
        ja.add(String.format("%4d.%s", sno, "Sylvia"));
        json.put("users", ja);
        json.put("total", ja.size());
        log.info("ReactorTest send:{}", json);
        String msg = String.format("%4d.%s", sno, "Hello World");
        jsonPathReactor.notify(json.toString(), Event.wrap(msg).setKey("json-path"));
    }

}
