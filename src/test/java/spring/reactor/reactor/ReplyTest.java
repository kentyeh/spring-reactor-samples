package spring.reactor.reactor;

import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class ReplyTest extends AbstractTestNGSpringContextTests {

    private static final Logger logger = LogManager.getLogger(ReplyTest.class);
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
    void testStringReplyString() {
        String msg = String.format("%4d.%s", serialno.incrementAndGet(), "Hello World");
        logger.info("ReplyTest send :{}", msg);
        reactor.notify("string.reply.string", Event.wrap(msg).setReplyTo("simple.string"));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testStringReplyException() {
        String msg = String.format("%4d.%s", serialno.incrementAndGet(), "Hello World");
        logger.info("ReplyTest send :{}", msg);
        reactor.notify("string.reply.exception", Event.wrap(msg).setReplyTo(Exception.class));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testExceptionReplyException() {
        String msg = String.format("%4d.%s", serialno.incrementAndGet(), "Hello World");
        logger.info("ReplyTest send :{}", msg);
        reactor.notify("exception.reply.exception", Event.wrap(msg).setReplyTo(RuntimeException.class));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testReplyUri() throws URISyntaxException {
        int sno = serialno.incrementAndGet();
        String uri = String.format("/reply/photo/%d_kent", sno);
        String msg = String.format("%4d.%s", sno, "Hello World");
        logger.info("ReactorTest send {} with {}", uri, msg);
        reactor.notify(uri, Event.wrap(msg).setReplyTo("simple.string"));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testReplyRegex() {
        String phoneno = String.format("09%08d", serialno.incrementAndGet());
        logger.info("ReplyTest send:{}", phoneno);
        reactor.notify("reply_" + phoneno, Event.wrap(phoneno).setReplyTo("simple.string"));
    }

    @Test(threadPoolSize = POOL_SIZE, invocationCount = INVO_CNT, timeOut = 10000)
    void testJsonpath() {
        JSONObject json = new JSONObject();
        JSONArray ja = new JSONArray();
        Integer sno = serialno.incrementAndGet();
        ja.add(String.format("%4d.%s", sno, "Kent"));
        ja.add(String.format("%4d.%s", sno, "Sylvia"));
        json.put("users", ja);
        json.put("headcount", ja.size());
        logger.info("ReplyTest send:{}", json);
        String msg = String.format("%4d.%s", sno, "Hello World");
        jsonPathReactor.notify(json.toString(), Event.wrap(msg).setKey("simple.string"));
    }

}
